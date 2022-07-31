package todoapp.ui.shared

import io.ktor.client.call.*
import io.ktor.client.plugins.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import mu.KLogger
import mu.KotlinLogging
import react.useContext
import todoapp.web.ServerError

object GlobalHooks {

    fun useDefaultErrorHandler(
        coroutineDispatcher: CoroutineDispatcher = Dispatchers.Default,
        logger: KLogger = KotlinLogging.logger("todoapp.ui.shared.hooks")
    ): (Throwable) -> Unit {
        logger.trace { "Creating instance of hook `GlobalHooks.useDefaultErrorHandler()` " }

        val snackbar = useContext(SnackbarProviders.SnackbarContext)

        return { error ->
            CoroutineScope(coroutineDispatcher).launch {
                val errorMessage = when(error) {
                    is ServerResponseException -> error.response.body<ServerError>().message
                    else -> error.message ?: "Something went wrong..."
                }
                logger.error(error) { errorMessage }

                snackbar.show(errorMessage, Severity.ERROR, 3000)
            }
        }
    }
}
