package todoapp.domain

/**
 * @author springrunner.kr@gmail.com
 */
@kotlinx.serialization.Serializable
data class Todos(
    private val content: List<Todo> = emptyList()
) : List<Todo> by content {

    fun remainingCount() = content.count { !it.completed }

    fun totalCount() = content.size

    fun filter(filter: TodoFilter) = Todos(
        filter { todo ->
            when (filter) {
                TodoFilter.ALL -> true
                TodoFilter.ACTIVE -> !todo.completed
                TodoFilter.COMPLETED -> todo.completed
            }
        }
    )
}

enum class TodoFilter(val value: String, val description: String) {
    ALL("all", "모든 할 일"),
    ACTIVE("active", "해야 할 일"),
    COMPLETED("completed", "완료된 할 일");

    fun isAll() = this == ALL
    fun isActive() = this == ACTIVE
    fun isCompleted() = this == COMPLETED

    companion object {

        fun ofValue(filter: String, default: TodoFilter = ALL) = values().firstOrNull {
            it.name.equals(filter, ignoreCase = true)
        } ?: default

        fun ofHash(filter: String, default: TodoFilter = ALL) = if (filter.startsWith("#")) {
            ofValue(filter = filter.substring(1), default = default)
        } else {
            ofValue(filter = filter, default = default)
        }
    }
}
