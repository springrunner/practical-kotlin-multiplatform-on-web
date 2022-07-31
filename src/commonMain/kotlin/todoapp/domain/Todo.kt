package todoapp.domain

import kotlinx.datetime.LocalDateTime
import todoapp.support.now

/**
 * 할 일 모델
 *
 * @author springrunner.kr@gmail.com
 */
@kotlinx.serialization.Serializable
data class Todo(
    val id: TodoId,
    val text: String,
    val completed: Boolean = false,
    val createdDate: LocalDateTime = LocalDateTime.now()
) {

    fun update(
        text: String = this.text,
        completed: Boolean = this.completed
    ): Todo {
        TodoValidator.validate(text)

        return copy(
            text = text,
            completed = completed
        )
    }

    fun complete() = copy(completed = true)

    override fun equals(other: Any?) = when {
        this === other -> true
        other is Todo -> other.id == id
        else -> false
    }

    override fun hashCode() = id.hashCode()

    companion object {

        /**
         * 새로운 할 일을 생성한다
         *
         * @param text 할 일 내용
         */
        fun create(
            text: String,
            currentDateTime: LocalDateTime = LocalDateTime.now(),
            idGenerator: TodoIdGenerator = RandomTodoIdGenerator()
        ): Todo {
            TodoValidator.validate(text = text)

            return Todo(
                id = idGenerator.generateId(),
                text = text,
                completed = false,
                createdDate = currentDateTime
            )
        }
    }
}
