package todoapp.application

import kotlinx.coroutines.test.runTest
import todoapp.domain.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class DefaultTodoManagerTest {

    private val todoManager = DefaultTodoManager(
        todoIdGenerator = RandomTodoIdGenerator(),
        todoRepository = object : TodoRepository {

            private var todos = Todos(content = mutableListOf())

            override suspend fun findByOrderByCreatedDateAsc() = todos

            override suspend fun findById(id: TodoId) = todos.firstOrNull { it.id == id }

            override suspend fun save(todo: Todo) {
                todos = Todos(
                    content = if (todos.contains(todo)) {
                        todos.minus(todo).plus(todo)
                    } else {
                        todos.plus(todo)
                    }
                )
            }

            override suspend fun delete(todo: Todo) {
                todos = Todos(content = todos.minus(todo))
            }
        }
    )

    @Test
    fun `등록된_모든_할_일을_조회해요`() = runTest {
        register("one", "two", "three")

        assertEquals(3, todoManager.all().size)
    }

    @Test
    fun `하나의_할_일을_조회해요`() = runTest {
        val registeredId = register("one")[0]

        assertEquals("one", todoManager.byId(registeredId).text)

    }

    @Test
    fun `새로운_할_일을_등록_후_변경해요`() = runTest {
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
        val registered = todoManager.register("springrunner").let {
            todoManager.byId(it)
        }

        todoManager.clear(registered.id)

        assertFailsWith<TodoNotFoundException> {
            todoManager.byId(registered.id)
        }
    }

    @Test
    fun `완료된_모든_할_일을_정리해요`() = runTest {
        todoManager.register("one")
        register("two", "three").forEach { registeredId ->
            todoManager.byId(registeredId).let { todo ->
                todoManager.modify(todo.id, todo.text, true)
            }
        }

        todoManager.clearAllCompleted()

        assertEquals(1, todoManager.all().size)
    }

    private suspend fun register(vararg texts: String): Array<TodoId> {
        return texts.map { todoManager.register(it) }.toTypedArray()
    }
}
