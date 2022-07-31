package todoapp.serializer

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import todoapp.domain.Todo
import todoapp.domain.TodoId
import kotlin.test.Test
import kotlin.test.assertEquals

class SerializationTest {

    @Test
    fun `할일객체를_JSON으로_직렬화하거나_JSON을_할일객체로_역직렬화할수있어요`() {
        val todo = Todo(
            id = TodoId("test"),
            text = "Task One",
            completed = false,
            createdDate = LocalDateTime.parse("2020-08-26T13:00:00")
        )

        val encoded = Serializers.JSON.encodeToString(todo)
        assertEquals("""
            {"id":"test","text":"Task One","completed":false,"createdDate":"2020-08-26T13:00"}
        """.trimIndent(), encoded)

        val decoded = Serializers.JSON.decodeFromString<Todo>(encoded)
        assertEquals(todo, decoded)
        assertEquals(todo.text, decoded.text)
        assertEquals(todo.completed, decoded.completed)
    }
}
