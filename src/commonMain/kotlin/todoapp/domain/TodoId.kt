package todoapp.domain

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * 할 일 일련번호
 *
 * @author springrunner.kr@gmail.com
 */
@kotlinx.serialization.Serializable(with = TodoId.TodoIdSerializer::class)
data class TodoId(val value: String) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        return when(other) {
            is TodoId -> value == other.value
            is String -> value == other
            else -> false
        }
    }
    override fun hashCode() = value.hashCode()
    override fun toString() = value

    internal object TodoIdSerializer : KSerializer<TodoId> {
        override val descriptor = PrimitiveSerialDescriptor("TodoId", PrimitiveKind.STRING)

        override fun deserialize(decoder: Decoder): TodoId {
            return TodoId(decoder.decodeString())
        }

        override fun serialize(encoder: Encoder, value: TodoId) {
            encoder.encodeString(value.toString())
        }
    }
}

expect interface TodoIdGenerator {

    open fun generateId(): TodoId
}

class UUIDTodoIdGenerator: TodoIdGenerator

class RandomTodoIdGenerator: TodoIdGenerator {

    private val CHARACTERS : List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')
    override fun generateId() = (1..36)
        .map { CHARACTERS[kotlin.random.Random.nextInt(0, CHARACTERS.size)] }
        .joinToString("")
        .let { TodoId(it) }
}
