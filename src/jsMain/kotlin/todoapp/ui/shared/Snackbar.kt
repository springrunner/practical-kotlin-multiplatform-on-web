package todoapp.ui.shared

import mu.KotlinLogging
import react.*
import react.dom.events.SyntheticEvent
import todoapp.ui.createInstance

data class UseSnackbarProps(
    val show : (message: String, severity: Severity, autoHideDuration: Number?) -> Unit,
    val hide: (event: SyntheticEvent<*, *>?, reason: String) -> Unit,
    val showed: Boolean = false,
    val autoHideDuration: Number? = null,
    val message: String = "",
    val severity: Severity = Severity.NORMAL
)

enum class Severity {
    NORMAL, SUCCESS, INFO, WARNING, ERROR,
}

object SnackbarHooks {

    fun useSnackbar(): UseSnackbarProps {
        logger.trace { "Creating instance of hook `SnackbarHooks.useSnackbar()` " }

        val (showed, setShowed) = useState(false)
        val (autoHideDuration, setAutoHideDuration) = useState<Number?>(null)
        val (message, setMessage) = useState("")
        val (severity, setSeverity) = useState(Severity.NORMAL)

        return UseSnackbarProps(
            show = { _message, _severity, _autoHideDuration  ->
                logger.debug { "Show snackbar, params(message: $_message, severity: $_severity, autoHideDuration: $_autoHideDuration)" }

                setAutoHideDuration(_autoHideDuration)
                setMessage(_message)
                setSeverity(_severity)
                setShowed(true)
            },
            hide = { _event, _reason ->
                logger.debug { "Hide snackbar, params(event: $_event, reason: $_reason)" }

                setShowed(false)
            },
            showed = showed,
            autoHideDuration = autoHideDuration,
            message = message,
            severity = severity
        )
    }
}

external interface SnackbarProps : Props {
    var showed: Boolean?
    var autoHideDuration: Number?
    var message: String
    var severity: Severity?
    var onHide: (event: SyntheticEvent<*, *>?, reason: String) -> Unit
}

object SnackbarComponents {

    val Snackbar = FC<SnackbarProps> { props ->
        mui.material.Snackbar {
            open = props.showed ?: false
            autoHideDuration = props.autoHideDuration
            if (props.severity == null || props.severity == Severity.NORMAL) {
                message = ReactNode(props.message)
            } else {
                child(
                    mui.material.Alert.create {
                        severity = when(props.severity) {
                            Severity.SUCCESS -> mui.material.AlertColor.success
                            Severity.INFO -> mui.material.AlertColor.info
                            Severity.WARNING -> mui.material.AlertColor.warning
                            Severity.ERROR -> mui.material.AlertColor.error
                            else -> mui.material.AlertColor.info
                        }
                        +props.message
                    }
                )
            }
            anchorOrigin = createInstance<mui.material.SnackbarOrigin> {
                vertical = mui.material.SnackbarOriginVertical.top
                horizontal = mui.material.SnackbarOriginHorizontal.center
            }
            onClose = { event, reason ->
                props.onHide(event, "$reason")
            }
        }
    }
}

object SnackbarProviders {

    val SnackbarContext = createContext(
        UseSnackbarProps(
            show = { _, _, _  ->
                logger.warn { "show handler is undefined" }
            },
            hide = { _, _ ->
                logger.warn { "hide handler is undefined" }
            },
        )
    )

    val SnackbarProvider = FC<PropsWithChildren> { providerProps ->
        val useSnackbarProps = SnackbarHooks.useSnackbar()

        SnackbarContext.Provider {
            value = useSnackbarProps
            +providerProps.children
            SnackbarComponents.Snackbar {
                showed = useSnackbarProps.showed
                autoHideDuration = useSnackbarProps.autoHideDuration
                message = useSnackbarProps.message
                severity = useSnackbarProps.severity
                onHide = useSnackbarProps.hide
            }

            logger.trace { "Creating instance of provider `SnackbarContext.Provider` " }
        }
    }
}

private val logger = KotlinLogging.logger("todoapp.ui.shared.Snackbar")
