package todoapp.data.r2dbc

import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.data.relational.core.query.Criteria.empty
import org.springframework.data.relational.core.query.Query.query
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig
import todoapp.data.TodoEntity
import todoapp.domain.Todo

/**
 * @author springrunner.kr@gmail.com
 */
@ExperimentalCoroutinesApi
@SpringJUnitConfig(classes = [SpringDataR2dbcConfiguration::class])
internal class R2dbcTodoRepositorySpecs(
    repository: R2dbcTodoRepository,
    template: R2dbcEntityTemplate
): FunSpec() {
    override fun extensions() = listOf(SpringExtension)

    init {
        val domainType = TodoEntity::class.java

        val one = Todo.create(text = "Task One")
        val two = Todo.create(text = "Task Two")
        val three = Todo.create(text = "Task Three")

        beforeEach {
            template.delete(domainType).all().block()
        }

        test("데이터베이스에 등록된 모든 할 일을 생성된 시간순으로 조회해요") {
            arrayOf(one, two, three).forEach { repository.save(it) }

            repository.findByOrderByCreatedDateAsc().let { todos ->
                todos.count() shouldBe 3
                todos[0].text shouldBe "Task One"
                todos[2].text shouldBe "Task Three"
            }
        }

        test("데이터베이스에서 일련번호로 할 일을 조회해요") {
            repository.save(three)

            repository.findById(three.id)!!.let { todo ->
                todo.text shouldBe three.text
                todo.completed shouldBe three.completed
            }
        }

        test("데이터베이스에 할 일을 등록하고, 변경해요") {
            repository.save(one)
            template.count(query(empty()), domainType).awaitSingle() shouldBe 1

            repository.save(one.complete())
            repository.findById(one.id)!!.let { todo ->
                todo.completed shouldBe true
            }
        }

        test("데이터베이스에 등록된 할 일을 삭제해요") {
            repository.save(two)
            repository.findById(two.id).shouldNotBeNull()

            repository.delete(two)
            repository.findById(two.id).shouldBeNull()
        }
    }
}
