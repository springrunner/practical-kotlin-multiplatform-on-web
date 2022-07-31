package todoapp.data

import kotlinx.datetime.toKotlinLocalDateTime
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.PersistenceConstructor
import org.springframework.data.relational.core.mapping.Table
import todoapp.domain.Todo
import todoapp.domain.TodoId
import java.time.LocalDateTime

/**
 * 할 일 엔티티 모델
 *
 * @author springrunner.kr@gmail.com
 */
@Table("todos")
internal class TodoEntity @PersistenceConstructor private constructor(
    @Id
    val id: TodoId,
    val text: String,
    val state: TodoState,
    val createdDate: LocalDateTime,
    val lastModifiedDate: LocalDateTime
) {

    fun update(text: String, completed: Boolean)  = TodoEntity(
        id = id,
        text = text,
        state = if (completed) TodoState.COMPLETED else TodoState.ACTIVE,
        createdDate = createdDate,
        lastModifiedDate = LocalDateTime.now()
    )

    fun toModel() = Todo(
        id = id,
        text = text,
        completed = state.isCompleted(),
        createdDate = createdDate.toKotlinLocalDateTime()
    )

    override fun toString(): String {
        return "Todo(id='$id', text='$text', state=$state, createdDate=$createdDate, lastModifiedDate=$lastModifiedDate)"
    }

    companion object {

        fun of(todo: Todo) = TodoEntity(
            id = todo.id,
            text = todo.text,
            state = if (todo.completed) TodoState.COMPLETED else TodoState.ACTIVE,
            createdDate = LocalDateTime.now(),
            lastModifiedDate = LocalDateTime.now()
        )
    }
}

enum class TodoState(val code: Int, val value: String, val description: String) {
    ACTIVE(0,"active","처리해야 할 일"),
    COMPLETED(10,"COMPLETED","완료된 할 일");

    fun isCompleted() = this == COMPLETED

    companion object {
        fun ofCode(code: Int) = values().first { it.code == code }
        fun ofValue(value: String) = values().first { it.value.equals(other = value, ignoreCase = true) }
    }
}
