package todoapp.web

import mu.KLogging
import org.springframework.web.reactive.function.server.*
import todoapp.application.TodoCleanup
import todoapp.application.TodoFind
import todoapp.application.TodoModification
import todoapp.application.TodoRegistry
import todoapp.domain.TodoId
import todoapp.web.command.WriteTodoCommand
import todoapp.web.validation.WriteTodoCommandValidator
import todoapp.web.validation.process
import java.net.URI

/**
 * 할 일 관리 라우터
 *
 * @author springrunner.kr@gmail.com
 */
class TodoRouter(
    val find: TodoFind,
    val registry: TodoRegistry,
    val modification: TodoModification,
    val cleanup: TodoCleanup
): RouterFunction<ServerResponse> {

    private val validator = WriteTodoCommandValidator()

    private val delegate = coRouter {
        "/api/todos".nest {
            GET("") {
                ok().bodyValueAndAwait(find.all())
            }

            // TODO 할 일 일련번호로 할 일을 조회하는 HandlerFunction을 작성하세요
            // 요청 : GET /{id}
            // 응답 : Todo Instance
            // 귀뜸: TodoFind 인터페이스가 제공하는 byId 메서드를 이용해보세요

            POST("") { request ->
                val command = request.awaitBody<WriteTodoCommand>().apply {
                    validator.process(target = this)
                }

                registry.register(command.text).let {
                    created(URI.create("/api/todos/$it")).bodyValueAndAwait(it)
                }
            }
            PUT("/{id}") { request ->
                val id = TodoId(request.pathVariable("id"))
                val command = request.awaitBody<WriteTodoCommand>()

                // TODO command 객체 값을 검증하세요
                // 귀띔: 할 일 등록하기 핸들러의 코드를 잘 살펴보세요

                modification.modify(id, command.text, command.completed)
                ok().bodyValueAndAwait(find.byId(id))
            }

            // TODO 할 일을 정리하는 HandlerFunction을 작성하세요
            // 요청 : DELETE /{id}
            // 응답 : Unit
            // 귀뜸: TodoCleanup 인터페이스가 제공하는 clear 메서드를 이용해보세요

            POST("/clear-completed") {
                cleanup.clearAllCompleted()
                ok().buildAndAwait()
            }
        }
    }

    override fun route(request: ServerRequest) = delegate.route(request)

    companion object : KLogging()
}
