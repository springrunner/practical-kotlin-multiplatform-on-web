package todoapp.application.support

import kotlinx.coroutines.test.runTest
import todoapp.domain.RandomTodoIdGenerator
import todoapp.domain.TodoId
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class InMemoryTodoManagerTest {

    private val todoManager = InMemoryTodoManager(RandomTodoIdGenerator())

    @Test
    fun `등록된_모든_할_일을_조회해요`() = runTest {
        cleanup()
        register("one", "two", "three")

        assertEquals(3, todoManager.all().size)
    }

    @Test
    fun `하나의_할_일을_조회해요`() = runTest {
        cleanup()

        val registeredId = register("one")[0]

        assertEquals("one", todoManager.byId(registeredId).text)

    }

    @Test
    fun `새로운_할_일을_등록_후_변경해요`() = runTest {
        cleanup()

        val registered = todoManager.register("springrunner").let {
            todoManager.byId(it)
        }

        assertEquals("springrunner", registered.text)
        assertEquals(false, registered.completed)

        val modified = todoManager.modify(registered.id, "infcon.day", true).let {
            todoManager.byId(registered.id)
        }

        assertEquals("infcon.day", modified.text)
        assertEquals(true, modified.completed)
    }

    @Test
    fun `등록된_할_일을_정리해요`() = runTest {
        cleanup()

        val registered = todoManager.register("springrunner").let {
            todoManager.byId(it)
        }

        todoManager.clear(registered.id)

        assertFailsWith<IllegalStateException> {
            todoManager.byId(registered.id)
        }
    }

    @Test
    fun `완료된_모든_할_일을_정리해요`() = runTest {
        cleanup()

        todoManager.register("one")
        register("two", "three").forEach { registeredId ->
            todoManager.byId(registeredId).let { todo ->
                todoManager.modify(todo.id, todo.text, true)
            }
        }

        todoManager.clearAllCompleted()

        assertEquals(1, todoManager.all().size)
    }

    private suspend fun cleanup() {
        for (todo in todoManager.all()) {
            todoManager.clear(todo.id)
        }
    }

    private suspend fun register(vararg texts: String): Array<TodoId> {
        return texts.map { todoManager.register(it) }.toTypedArray()
    }
}
