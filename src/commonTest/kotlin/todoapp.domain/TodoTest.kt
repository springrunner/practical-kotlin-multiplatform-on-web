package todoapp.domain

import kotlinx.datetime.LocalDateTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotEquals

class TodoTest {

    private val todoId = TodoId("0d31a103-7ee1-4e4e-96af-12c858e18970")
    private val todoIdGenerator = object : TodoIdGenerator {
        override fun generateId() = todoId
    }
    private val dateTime = LocalDateTime.parse("2020-08-26T13:00:00")

    @Test
    fun `새로운_할일을_생성해요`() {
        val todo = Todo.create(
            text = "one",
            currentDateTime = dateTime,
            idGenerator = todoIdGenerator
        )

        assertEquals(todoId, todo.id)
        assertEquals("one", todo.text)
        assertEquals(false, todo.completed)
        assertEquals(dateTime, todo.createdDate)
    }

    @Test
    fun `빈_문자열로_새로운_할일을_생성할수없어요`() {
        assertFailsWith<TodoRegistrationException> {
            Todo.create(text = "")
        }
    }

    @Test
    fun `할일_내용과_완료여부를_변경해요`() {
        val todo = Todo.create(
            text = "one",
            currentDateTime = dateTime,
            idGenerator = todoIdGenerator
        ).update(text = "two", completed = true)

        assertEquals("two", todo.text)
        assertEquals(true, todo.completed)

        assertEquals("three", todo.update(text = "three").text)
        assertEquals(false, todo.update(completed = false).completed)
    }

    @Test
    fun `할일을_완료해요`() {
        val todo = Todo.create(
            text = "one",
            currentDateTime = dateTime,
            idGenerator = todoIdGenerator
        )

        assertEquals(false, todo.completed)
        assertEquals(true, todo.complete().completed)
    }

    @Test
    fun `할일_동등성을_확인해요`() {
        val created = Todo.create(
            text = "one",
            currentDateTime = dateTime,
            idGenerator = todoIdGenerator
        )

        assertEquals(created, created.complete())
        assertNotEquals(created, Todo.create(text = "two"))
    }
}
