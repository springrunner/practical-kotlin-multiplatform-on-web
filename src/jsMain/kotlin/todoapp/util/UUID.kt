package todoapp.util

/**
 * @author springrunner.kr@gmail.com
 */
value class UUID private constructor(private val value: String) {

    override fun toString() = value

    companion object {

        fun randomUUID() = UUID(external.uuid.v4().lowercase())
    }
}
