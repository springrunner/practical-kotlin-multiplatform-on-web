package todoapp.domain

/**
 * 할 일 저장소 인터페이스
 *
 * @author springrunner.kr@gmail.com
 */
interface TodoRepository {

    suspend fun findByOrderByCreatedDateAsc(): Todos

    suspend fun findById(id: TodoId): Todo?

    suspend fun save(todo: Todo)

    suspend fun delete(todo: Todo)
}
