package todoapp.ui.todo

import csstype.ClassName
import kotlinx.coroutines.Deferred
import mu.KotlinLogging
import org.w3c.dom.HTMLInputElement
import react.*
import react.dom.html.InputType
import react.dom.html.ReactHTML.a
import react.dom.html.ReactHTML.button
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.footer
import react.dom.html.ReactHTML.h1
import react.dom.html.ReactHTML.header
import react.dom.html.ReactHTML.input
import react.dom.html.ReactHTML.label
import react.dom.html.ReactHTML.li
import react.dom.html.ReactHTML.main
import react.dom.html.ReactHTML.p
import react.dom.html.ReactHTML.span
import react.dom.html.ReactHTML.strong
import react.dom.html.ReactHTML.ul
import todoapp.domain.Todo
import todoapp.domain.TodoFilter
import todoapp.domain.Todos
import todoapp.support.onError
import todoapp.ui.shared.BrowserHooks.useHash
import todoapp.ui.shared.GlobalHooks.useDefaultErrorHandler

external interface WriteBoxProps : Props {
    var text: String?
}

external interface ListContainerProps : Props {
    var todos: Todos?
    var modify: ((Todo) -> Deferred<Unit>)?
}

external interface ListItemProps : Props {
    var data: Todo
    var onModify: (Todo) -> Deferred<Unit>
    var onClear: (Todo) -> Deferred<Unit>
}

object TodoComponents {

    val WriteBox = FC<WriteBoxProps> {props ->
        val todoContext = useContext(TodoProviders.TodoContext)
        val errorHandler = useDefaultErrorHandler()

        header {
            h1 {
                +(props.text ?: "todos")
            }
            input {
                className = ClassName("new-todo")
                type = InputType.text
                placeholder = "What needs to be done?"
                autoFocus = true
                onKeyDown = { event ->
                    val key = event.asDynamic().key as String
                    val value = event.currentTarget.value
                    if (!event.nativeEvent.isComposing && key == "Enter" && value.isNotBlank()) {
                        if (undefined == todoContext.register) {
                            logger.warn { "`TodoContext.register` handler is undefined" }
                        } else {
                            todoContext.register(value).onError(errorHandler)
                            event.currentTarget.value = ""
                        }
                    }
                }
            }
        }
    }

    val ListContainer = FC<ListContainerProps> {props ->
        val todoContext = useContext(TodoProviders.TodoContext)
        val todos = when {
            undefined != props.todos && null != props.todos -> props.todos!!
            undefined == todoContext.todos -> emptyList()
            else -> todoContext.todos
        }
        val totalCount = if (undefined == todoContext.totalCount) 0 else todoContext.totalCount
        val errorHandler = useDefaultErrorHandler()

        if (todos.isNotEmpty() || totalCount > 0) {
            main {
                className = ClassName("main")
                input {
                    id = "toggle-all"
                    className = ClassName("toggle-all")
                    type = InputType.checkbox
                    onChange = {
                        if (undefined == todoContext.completes) {
                            logger.warn { "`TodoContext.completes` handler is undefined" }
                        } else {
                            todoContext.completes(it.currentTarget.checked).onError(errorHandler)
                        }
                    }
                }
                label {
                    htmlFor = "toggle-all"
                    title = "Mark all as complete"
                }
                ul {
                    className = ClassName("todo-list")
                    todos.forEach { todo ->
                        TodoItem {
                            data = todo
                            onModify = if (undefined != props.modify && null != props.modify) {
                                props.modify!!
                            } else {
                                todoContext.modify
                            }
                            onClear = todoContext.clear
                        }
                    }
                }
            }
        }
    }
    private val TodoItem = FC<ListItemProps> { props ->
        if (undefined == props.onModify) {
            throw IllegalStateException("`TodoItem.onModify` handler is undefined")
        }

        val todo = props.data
        val errorHandler = useDefaultErrorHandler()

        val (editing, setEditing) = useState(false)
        val editInputRef = useRef<HTMLInputElement>(null)

        useEffect(editing) {
            if (editing) {
                editInputRef.current?.focus()
            }
        }

        li {
            className = when {
                editing -> ClassName("editing")
                todo.completed -> ClassName("completed")
                else -> null
            }
            div {
                className = ClassName("view")
                input {
                    className = ClassName("toggle")
                    type = InputType.checkbox
                    checked = todo.completed
                    onChange = { event ->
                        try {
                            props.onModify(todo.update(completed = event.currentTarget.checked)).onError(errorHandler)
                        } catch (error: Throwable) {
                            errorHandler(error)
                        }
                    }
                }
                label {
                    onDoubleClick = {
                        if (!todo.completed) {
                            editInputRef.current?.value = todo.text
                            setEditing(true)
                        }
                    }
                    +todo.text
                }
                button {
                    className = ClassName("destroy")
                    onClick = {
                        if (undefined == props.onClear) {
                            logger.warn { "`TodoItem.onClear` handler is undefined" }
                        } else {
                            props.onClear(todo).onError(errorHandler)
                        }
                    }
                }
            }
            input {
                ref = editInputRef
                className = ClassName("edit")
                type = InputType.text
                defaultValue = todo.text
                onBlur = { event ->
                    try {
                        if (todo.text != event.currentTarget.value) {
                            props.onModify(todo.update(text = event.currentTarget.value)).onError(errorHandler)
                        }
                    } catch (error: Throwable) {
                        errorHandler(error)
                    } finally {
                        setEditing(false)
                    }
                }
                onKeyDown = { event ->
                    when((event.asDynamic().key as String)) {
                        "Escape" -> {
                            event.currentTarget.value = todo.text
                            setEditing(false)
                        }
                        "Enter" -> {
                            event.currentTarget.blur()
                        }
                    }
                }
            }
        }
    }

    val ControlBar = FC<Props> { _ ->
        val todoContext = useContext(TodoProviders.TodoContext)
        val errorHandler = useDefaultErrorHandler()
        val (hash, updateHash) = useHash()

        val remainingCount = if (undefined == todoContext.remainingCount) 0 else todoContext.remainingCount
        val totalCount = if (undefined == todoContext.totalCount) 0 else todoContext.totalCount
        val currentFilter = TodoFilter.ofHash(hash)

        useEffect(hash) {
            if (undefined == todoContext.filtering) {
                logger.warn { "`TodoContext.filtering` handler is undefined" }
            } else {
                todoContext.filtering(currentFilter)
            }
        }
        useEffectOnce {
            updateHash(currentFilter.value)
        }

        if (totalCount > 0) {
            val selected = ClassName("selected")

            footer {
                className = ClassName("footer")
                span {
                    className = ClassName("todo-count")
                    strong { +"$remainingCount" }
                    +" item(s) left"
                }
                ul {
                    className = ClassName("filters")
                    li {
                        a {
                            className = if (currentFilter.isAll()) selected else null
                            onClick = { updateHash(TodoFilter.ALL.value) }
                            +"All"
                        }
                    }
                    li {
                        +" "
                    }
                    li {
                        a {
                            className = if (currentFilter.isActive()) selected else null
                            onClick = { updateHash(TodoFilter.ACTIVE.value) }
                            +"Active"
                        }
                    }
                    li {
                        +" "
                    }
                    li {
                        a {
                            className = if (currentFilter.isCompleted()) selected else null
                            onClick = { updateHash(TodoFilter.COMPLETED.value) }
                            +"Completed"
                        }
                    }
                }
                button {
                    className = ClassName("clear-completed")
                    onClick = {
                        if (undefined == todoContext.clearAllCompleted) {
                            logger.warn { "`TodoContext.clearAllCompleted` handler is undefined" }
                        } else {
                            todoContext.clearAllCompleted().onError(errorHandler)
                        }
                    }
                    +"Clear completed"
                }
            }
        }
    }

    val Info = FC<Props> {
        footer {
            className = ClassName("info")
            p { +"Double-click to edit a todo" }
            p {
                +"Created by "
                a {
                    href = "https://springrunner.dev/"
                    +"SpringRunner"
                }
            }
            p {
                +"Part of"
                +" "
                a {
                    href = "https://todomvc.com/"
                    +"TodoMVC"
                }
            }
        }
    }
}

private val logger = KotlinLogging.logger("todoapp.ui.todo.components")
