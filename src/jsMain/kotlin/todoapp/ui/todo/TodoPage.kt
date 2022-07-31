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
import todoapp.ui.todo.TodoComponents.Info
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
            // header: 할 일 작성하기 영역
            // main: 할 일 목록 영역
            // footer: 할 일 남은 갯수, 필터링 영역

            find = props.find
            registry = props.registry
            modification = props.modification
            cleanup = props.cleanup
        }
    }
    Info()
}

private val logger = KotlinLogging.logger("todoapp.ui.todo.page")