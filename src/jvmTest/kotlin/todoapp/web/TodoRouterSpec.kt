package todoapp.web

import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.hamcrest.Matchers.hasSize
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import todoapp.application.support.InMemoryTodoManager
import todoapp.domain.RandomTodoIdGenerator
import todoapp.web.command.WriteTodoCommand
import java.util.*

/**
 * @author springrunner.kr@gmail.com
 */
@ExperimentalCoroutinesApi
class TodoRouterSpec : FunSpec({

    val todoManager = InMemoryTodoManager(
        todoIdGenerator = RandomTodoIdGenerator()
    )
    val todoRouter = TodoRouter(
        find = todoManager,
        registry = todoManager,
        modification = todoManager,
        cleanup = todoManager
    )

    val client = WebTestClient
        .bindToRouterFunction(todoRouter)
        .configureClient()
        .baseUrl("/api/todos")
        .build()

    suspend fun createTodo(text: String = UUID.randomUUID().toString()) =
        todoManager.register(text = text).let {
            todoManager.byId(it)
        }

    beforeEach {
        // 테스트 실행 전 모든 할 일 정리하기
        todoManager.all().forEach {
            runTest {
                todoManager.clear(it.id)
            }
        }
    }

    test("서버에 등록된 모든 할 일을 조회해요") {
        client.get()
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectAll(
                { it.expectStatus().isOk },
                { it.expectHeader().contentType(MediaType.APPLICATION_JSON) },
                { it.expectBody().jsonPath("$", hasSize<Any>(2)) }
            )
    }

    test("서버에 등록된 할 일을 조회해요") {
        val todoId = todoManager.register(text = UUID.randomUUID().toString())

        client.get().uri("/{id}", todoId)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectAll(
                { it.expectStatus().isOk },
                { it.expectHeader().contentType(MediaType.APPLICATION_JSON) },
                { it.expectBody().jsonPath("$.id").isEqualTo(todoId.value) }
            )
    }

    test("서버에 새로운 할 일 등록해요") {
        val command = WriteTodoCommand(text = UUID.randomUUID().toString())

        client.post()
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(command)
            .exchange()
            .expectAll(
                { it.expectStatus().isCreated },
                { it.expectHeader().contentType(MediaType.APPLICATION_JSON) },
                {
                    val todo = runBlocking {
                        todoManager.all().single()
                    }
                    it.expectBody().jsonPath("$").isEqualTo(todo.id)
                    it.expectHeader().location("/api/todos/${todo.id}")
                }
            )
    }

    test("서버로 할 일 등록 요청시 잘못된 내용을 전달하면 검증 오류가 발생해요") {
        client.post()
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(WriteTodoCommand(text = ""))
            .exchange()
            .expectAll(
                { it.expectStatus().isBadRequest }
            )
    }

    test("서버에 등록된 할 일 내용을 변경해요") {
        val todoId = todoManager.register(text = UUID.randomUUID().toString())
        val command = WriteTodoCommand(text = UUID.randomUUID().toString())

        client.put().uri("/{id}", todoId)
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(command)
            .exchange()
            .expectAll(
                { it.expectStatus().isOk },
                { it.expectHeader().contentType(MediaType.APPLICATION_JSON) },
                {
                    it.expectBody().jsonPath("$.text").isEqualTo(command.text)
                }
            )
    }

    test("서버로 할 일 변경 요청시 잘못된 내용을 전달하면 검증 오류가 발생해요") {
        val todoId = todoManager.register(text = UUID.randomUUID().toString())

        client.put().uri("/{id}", todoId)
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(WriteTodoCommand(text = ""))
            .exchange()
            .expectAll(
                { it.expectStatus().isBadRequest }
            )
    }

    test("서버에 등록된 할 일을 정리해요") {
        val todoId = todoManager.register(text = UUID.randomUUID().toString())

        client.delete().uri("/{id}", todoId)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk

        shouldThrowExactly<IllegalStateException> {
            todoManager.byId(todoId)
        }
    }

    test("서버에 완료된 모든 할 일을 정리해요") {
        createTodo().let { todoManager.modify(it.id, it.text, true) }
        createTodo().let { todoManager.modify(it.id, it.text, false) }
        createTodo().let { todoManager.modify(it.id, it.text, true) }
        createTodo().let { todoManager.modify(it.id, it.text, false) }

        client.post().uri("/clear-completed")
            .exchange()
            .expectStatus().isOk

        todoManager.all().size shouldBe 2
    }
})
