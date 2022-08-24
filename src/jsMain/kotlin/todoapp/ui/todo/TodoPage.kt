package todoapp.ui.todo

import csstype.ClassName
import mu.KotlinLogging
import react.FC
import react.Props
import react.dom.html.ReactHTML.section
import todoapp.application.TodoCleanup
import todoapp.application.TodoFind
import todoapp.application.TodoModification
import todoapp.application.TodoRegistry
import todoapp.ui.todo.TodoComponents.ControlBar
import todoapp.ui.todo.TodoComponents.Info
import todoapp.ui.todo.TodoComponents.ListContainer
import todoapp.ui.todo.TodoComponents.WriteBox
import todoapp.ui.todo.TodoProviders.TodoProvider

external interface TodoPageProps : Props {
    var find: TodoFind
    var registry: TodoRegistry
    var modification: TodoModification
    var cleanup: TodoCleanup
}

val TodoPage = FC<TodoPageProps> { props ->
    section {
        className = ClassName("todoapp")
        TodoProvider {
            WriteBox {
                text = "todos"
            }
            ListContainer()
            ControlBar()

            find = props.find
            registry = props.registry
            modification = props.modification
            cleanup = props.cleanup
        }
    }
    Info()
}

private val logger = KotlinLogging.logger("todoapp.ui.todo.page")