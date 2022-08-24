package todoapp.domain

import io.kotest.matchers.nulls.shouldNotBeNull
import kotlin.test.*

class TodoIdTest {

    @Test
    fun `할일_일련번호_동등성을_확인해요`() {
        assertNotEquals(TodoId("spring"), TodoId("runner"))
        assertEquals(TodoId("spring"), TodoId("spring"))

        assertFalse(TodoId("spring").equals("runner"))
        assertTrue(TodoId("spring").equals("spring"))
    }

    @Test
    fun `램덤_할일_일련번호_생성기로_일련번호를_생성해요`() {
        RandomTodoIdGenerator().generateId().shouldNotBeNull()
    }

    @Test
    fun `UUID_할일_일련번호_생성기로_일련번호를_생성해요`() {
        UUIDTodoIdGenerator().generateId().shouldNotBeNull()
    }
}
