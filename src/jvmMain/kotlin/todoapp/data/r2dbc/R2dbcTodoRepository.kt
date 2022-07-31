package todoapp.data.r2dbc

import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.ReadingConverter
import org.springframework.data.convert.WritingConverter
import org.springframework.data.domain.Sort
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.data.relational.core.query.Criteria.where
import org.springframework.data.relational.core.query.Query.empty
import org.springframework.data.relational.core.query.Query.query
import reactor.core.publisher.Mono
import todoapp.data.TodoEntity
import todoapp.data.TodoState
import todoapp.domain.Todo
import todoapp.domain.TodoId
import todoapp.domain.TodoRepository
import todoapp.domain.Todos

/**
 * Spring Data R2DBC 기반 할 일 저장소 구현체
 *
 * @author springrunner.kr@gmail.com
 */
internal class R2dbcTodoRepository(
    private val entityTemplate: R2dbcEntityTemplate
): TodoRepository {

    private val entityClass = TodoEntity::class.java

    override suspend fun findByOrderByCreatedDateAsc() = Todos(
        entityTemplate
            .select(empty().sort(Sort.by(Sort.Order.asc("createdDate"))), entityClass)
            .map { it.toModel() }
            .asFlow()
            .toList()
    )

    override suspend fun findById(id: TodoId) = loadById(id)?.toModel()

    override suspend fun save(todo: Todo) {
        val entity = loadById(todo.id)
        if (entity == null) {
            entityTemplate.insert(TodoEntity.of(todo))
        } else {
            entityTemplate.update(entity.update(todo.text, todo.completed))
        }.await()
    }

    override suspend fun delete(todo: Todo) {
        loadById(todo.id)?.run {
            entityTemplate.delete(this).await()
        }
    }

    private suspend fun loadById(id: TodoId) = entityTemplate
        .selectOne(query(where("id").`is`(id.value)), entityClass)
        .awaitSingleOrNull()

    private suspend fun exists(id: TodoId) = entityTemplate
        .exists(query(where("id").`is`(id.value)), entityClass).awaitSingle()

    private suspend fun <T> Mono<T>.await() = thenReturn(Unit).awaitSingle()
}

@WritingConverter
class TodoIdToStringConverter : Converter<TodoId, String> {
    override fun convert(source: TodoId) = source.value
}

@ReadingConverter
class StringToTodoIdConverter : Converter<String, TodoId> {
    override fun convert(source: String) = TodoId(source)
}

@WritingConverter
class TodoStateToIntConverter : Converter<TodoState, Int> {
    override fun convert(source: TodoState) = source.code
}

@ReadingConverter
class IntToTodoStateConverter : Converter<Int, TodoState> {
    override fun convert(source: Int) = TodoState.ofCode(source)
}
