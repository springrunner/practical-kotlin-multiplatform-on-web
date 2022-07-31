package todoapp.ui.todo

import kotlinx.coroutines.*
import mu.KotlinLogging
import react.useCallback
import react.useEffectOnce
import react.useState
import todoapp.application.TodoCleanup
import todoapp.application.TodoFind
import todoapp.application.TodoModification
import todoapp.application.TodoRegistry
import todoapp.domain.Todo
import todoapp.domain.Todos

data class UseTodosProps(
    val todos: Todos,
    val register: (String) -> Deferred<Unit>,
    val modify: (Todo) -> Deferred<Unit>,
    val completes: (Boolean) -> Deferred<Unit>,
    val clear: (Todo) -> Deferred<Unit>,
    val clearAllCompleted: () -> Deferred<Unit>,
)

object TodoHooks {

    fun useTodos(
        find: TodoFind,
        registry: TodoRegistry,
        modification: TodoModification,
        cleanup: TodoCleanup,
        coroutineDispatcher: CoroutineDispatcher = Dispatchers.Default
    ): UseTodosProps {
        logger.trace { "Creating instance of hook `TodoHooks.useTodos()` " }

        val (todos, setTodos) = useState(Todos())
        val fetch = useCallback {
            logger.info { "Fetch todos" }

            CoroutineScope(coroutineDispatcher).launch {
                setTodos(find.all())
            }
        }

        useEffectOnce {
            fetch()
        }

        return UseTodosProps(
            todos = todos,
            register = { text ->
                logger.info { "Register todo, params(text: $text)" }

                CoroutineScope(coroutineDispatcher).async {
                    withContext(coroutineContext) {
                        registry.register(text = text)
                    }
                    fetch()
                }
            },
            modify = { todo ->
                logger.info { "Modify todo, params(id: $todo.id, text: ${todo.text}, completed: ${todo.completed})" }

                CoroutineScope(coroutineDispatcher).async {
                    withContext(coroutineContext) {
                        modification.modify(id = todo.id, text = todo.text, completed = todo.completed)
                    }
                    fetch()
                }
            },
            completes = { completed ->
                logger.info { "Modify all todos, params(completed: $completed)" }

                CoroutineScope(coroutineDispatcher).async {
                    withContext(coroutineContext) {
                        todos.forEach { todo ->
                            modification.modify(id = todo.id, text = todo.text, completed = completed)
                        }
                    }
                    fetch()
                }
            },
            clear = { todo ->
                logger.info { "Clear todo, params(id: ${todo.id})" }

                CoroutineScope(coroutineDispatcher).async {
                    withContext(coroutineContext) {
                        cleanup.clear(todo.id)
                    }
                    fetch()
                }
            },
            clearAllCompleted = {
                logger.info { "Clear completed todos" }

                CoroutineScope(coroutineDispatcher).async {
                    withContext(coroutineContext) {
                        cleanup.clearAllCompleted()
                    }
                    fetch()
                }
            },
        )
    }
}

private val logger = KotlinLogging.logger("todoapp.ui.todo.hooks")
