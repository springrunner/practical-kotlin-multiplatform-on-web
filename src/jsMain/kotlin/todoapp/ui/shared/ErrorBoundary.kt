package todoapp.ui.shared

import mu.KotlinLogging
import react.*
import react.dom.html.ReactHTML
import todoapp.ui.createInstance

external interface ErrorBoundaryProps : PropsWithChildren {
    var fallback: ReactNode?
    var onError: ((error: Throwable, info: ErrorInfo) -> Unit)?
}
external interface ErrorBoundaryState : State {
    var error: Throwable?
}

private class RErrorBoundary(props: ErrorBoundaryProps): RComponent<ErrorBoundaryProps, ErrorBoundaryState>(props) {

    private val errorNode = props.fallback ?: ReactHTML.p.create {
        +"Something went wrong!"
    }

    private fun hasError() = state.error != null

    override fun RBuilder.render() {
        ReactHTML.section {
            if (hasError()) {
                child(errorNode)
            } else {
                props.children()
            }
        }
    }

    override fun componentDidCatch(error: Throwable, info: ErrorInfo) {
        if (props.onError != null) {
            props.onError!!(error, info)
        } else {
            logger.error(error) { "An error occurred on... ${info.componentStack}" }
        }
    }

    companion object : RStatics<ErrorBoundaryProps, ErrorBoundaryState, RErrorBoundary, Nothing>(RErrorBoundary::class) {
        init {
            getDerivedStateFromError = { _error ->
                createInstance<ErrorBoundaryState> {
                    error = _error
                }
            }
        }
    }
}

object ErrorBoundaryComponents {

    val ErrorBoundary = RErrorBoundary::class.react
}

private val logger = KotlinLogging.logger("todoapp.ui.shared.ErrorBoundary")
