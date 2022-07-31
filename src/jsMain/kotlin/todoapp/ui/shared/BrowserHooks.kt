package todoapp.ui.shared

import kotlinx.browser.window
import mu.KotlinLogging
import org.w3c.dom.events.EventListener
import react.useEffectOnce
import react.useState

data class UseHashProps(
    val hash: String,
    val update: (String) -> Unit
)

object BrowserHooks {

    fun useHash(): UseHashProps {
        val (hash, setHash) = useState(window.location.hash)

        useEffectOnce {
            val onHashChange = EventListener {
                setHash(window.location.hash)
                logger.trace { "Updated hash: $hash" }
            }

            window.addEventListener("hashchange", onHashChange)
            logger.debug { "Register `hashchange` event-handler on window" }
            cleanup {
                window.removeEventListener("hashchange", onHashChange)
                logger.debug { "Deregister `hashchange` event-handler on window" }
            }
        }

        return UseHashProps(
            hash = hash,
            update = { newValue ->
                if (newValue !== hash) window.location.hash = newValue
            }
        )
    }
}

private val logger = KotlinLogging.logger("todoapp.ui.shared.hooks")
