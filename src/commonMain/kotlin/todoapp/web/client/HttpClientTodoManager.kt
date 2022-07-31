package todoapp.web.client

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import mu.KotlinLogging
import todoapp.application.TodoCleanup
import todoapp.application.TodoFind
import todoapp.application.TodoModification
import todoapp.application.TodoRegistry
import todoapp.domain.Todo
import todoapp.domain.TodoId
import todoapp.domain.TodoValidator
import todoapp.domain.Todos
import todoapp.web.command.WriteTodoCommand

/**
 * Web API 기반 할 일 기능 구현 클래스
 *
 * @author springrunner.kr@gmail.com
 */
class HttpClientTodoManager(
    private val httpClient: HttpClient,
    private val webAPIProperties: WebAPIProperties = WebAPIProperties()
): TodoFind, TodoRegistry, TodoModification, TodoCleanup {

    private val logger = KotlinLogging.logger("todoapp.application.HttpClientTodoManager")

    override suspend fun all(): Todos {
        return httpClient.get(webAPIProperties.findAllUrl).body()
    }

    override suspend fun byId(id: TodoId): Todo {
        return httpClient.get(webAPIProperties.findByIdUrl.replace("{id}", id.value)).body()
    }

    override suspend fun register(text: String): TodoId {
        TodoValidator.validate(text)

        return httpClient.post(webAPIProperties.registerUrl) {
            contentType(ContentType.Application.Json)
            setBody(WriteTodoCommand(text))
        }.body<TodoId>().apply {
            logger.debug { "Registered todo (id: $this)" }
        }
    }

    override suspend fun modify(id: TodoId, text: String, completed: Boolean) {
        TodoValidator.validate(text)

        httpClient.put(webAPIProperties.modifyUrl.replace("{id}", id.value)) {
            contentType(ContentType.Application.Json.withoutParameters())
            setBody(WriteTodoCommand(text, completed))
        }.apply {
            logger.debug { "Modified todo (id: $id)" }
        }
    }

    override suspend fun clear(id: TodoId) {
        httpClient.delete(webAPIProperties.clearUrl.replace("{id}", id.value)).apply {
            logger.debug { "Cleared todo (id: $id)" }
        }
    }

    override suspend fun clearAllCompleted() {
        httpClient.post(webAPIProperties.clearCompletedUrl).apply {
            logger.debug { "Cleared completed todos" }
        }
    }

    data class WebAPIProperties(
        val findAllUrl: String =  "http://localhost:8080/api/todos",
        val findByIdUrl: String =  "http://localhost:8080/api/todos/{id}",
        val registerUrl: String = "http://localhost:8080/api/todos",
        val modifyUrl: String = "http://localhost:8080/api/todos/{id}",
        val clearUrl: String = "http://localhost:8080/api/todos/{id}",
        val clearCompletedUrl: String = "http://localhost:8080/api/todos/clear-completed",
    )
}
