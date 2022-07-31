package todoapp.web.client

import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.utils.io.*
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.encodeToString
import todoapp.domain.Todo
import todoapp.domain.Todos
import todoapp.serializer.Serializers
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class HttpClientTodoManagerTest {

    private val todos = Todos(listOf(
        Todo.create("Task One"),
        Todo.create("Task Two"),
        Todo.create("Task Three"),
    ))

    private val responseHeaders = headersOf(HttpHeaders.ContentType, "application/json")
    private val todoManager = HttpClientTodoManager(
        HttpClient(MockEngine) {
            engine {
                addHandler {request ->
                    when(request.url.encodedPath) {
                        "/api/todos" -> respond(
                            content = Serializers.JSON.encodeToString(todos),
                            status = HttpStatusCode.OK,
                            headers = responseHeaders
                        )
                        "/api/todos/${todos[0].id}" -> respond(
                            content = Serializers.JSON.encodeToString(todos[0]),
                            status = HttpStatusCode.OK,
                            headers = responseHeaders
                        )
                        "/api/todos/clear-completed" -> respond(
                            content = ByteReadChannel.Empty,
                            status = HttpStatusCode.OK,
                            headers = responseHeaders
                        )
                        else -> error("Unhandled ${request.url.encodedPath}")
                    }
                }
            }
            install(ContentNegotiation) {
                json(Serializers.JSON)
            }
            expectSuccess = true
        }
    )

    @Test
    fun `Web_API로_등록된_모든_할_일을_조회해요`() = runTest {
        assertEquals(3, todoManager.all().size)
    }

    @Test
    fun `Web_API로_하나의_할_일을_조회해요`() = runTest {
        val todo = todos[0]

        todoManager.clear(todo.id)
    }

    @Test
    fun `Web_API로_완료된_모든_할_일을_정리해요`() = runTest {
        todoManager.clearAllCompleted()
    }
}
