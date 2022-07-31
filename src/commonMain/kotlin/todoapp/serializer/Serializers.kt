package todoapp.serializer

import kotlinx.serialization.json.Json

/**
 * @author springrunner.kr@gmail.com
 */
object Serializers {

    val JSON = Json {
        encodeDefaults = true
        ignoreUnknownKeys = true
        coerceInputValues = true
        isLenient = true
        allowSpecialFloatingPointValues = true
        allowStructuredMapKeys = true
        prettyPrint = false
        useArrayPolymorphism = false
    }
}
