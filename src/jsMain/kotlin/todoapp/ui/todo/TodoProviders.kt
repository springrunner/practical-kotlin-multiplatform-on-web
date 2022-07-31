package todoapp.ui.todo

import kotlinx.coroutines.Deferred
import mu.KotlinLogging
import react.*
import todoapp.application.TodoCleanup
import todoapp.application.TodoFind
import todoapp.application.TodoModification
import todoapp.application.TodoRegistry
import todoapp.domain.Todo
import todoapp.domain.TodoFilter
import todoapp.ui.createInstance
import todoapp.ui.todo.TodoHooks.useTodos

external interface TodoContextProps : PropsWithChildren {
    var todos: List<Todo>
    var remainingCount: Int
    var totalCount: Int
    var register: (String) -> Deferred<Unit>
    var modify: (Todo) -> Deferred<Unit>
    var completes: (Boolean) -> Deferred<Unit>
    var clear: (Todo) -> Deferred<Unit>
    var clearAllCompleted: () -> Deferred<Unit>
    var filtering: (TodoFilter) -> Unit
}

external interface TodoProviderProps : PropsWithChildren {
    var find: TodoFind
    var registry: TodoRegistry
    var modification: TodoModification
    var cleanup: TodoCleanup
}

object TodoProviders {

    val TodoContext = createContext(
        createInstance<TodoContextProps> {  }
    )

    val TodoProvider = FC<TodoProviderProps> {props ->
        val (todos, registerTodo, modifyTodo, completesTodo, clearTodo, clearAllCompletedTodos) = useTodos(
            find = props.find,
            registry = props.registry,
            modification = props.modification,
            cleanup = props.cleanup
        )

        val (filteredTodos, setFilteredTodos) = useState(todos)
        val (filter, setFilter) = useState(TodoFilter.ALL)

        useEffect(todos, filter) {
            logger.info { "Filter todos, params(type: $filter, todos: ${todos.size})" }

            setFilteredTodos(todos.filter(filter))
        }

        TodoContext.Provider {
            value = createInstance {
                this.todos = filteredTodos
                this.remainingCount = todos.remainingCount()
                this.totalCount = todos.totalCount()
                this.register = registerTodo
                this.modify = modifyTodo
                this.completes = completesTodo
                this.clear = clearTodo
                this.clearAllCompleted = clearAllCompletedTodos
                this.filtering = { todoFilter ->
                    setFilter(todoFilter)
                }
            }
            +props.children

            logger.trace { "Creating instance of provider `TodoContext.Provider` " }
        }
    }
}

private val logger = KotlinLogging.logger("todoapp.ui.todo.providers")
