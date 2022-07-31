package todoapp.domain

import kotlin.test.Test
import kotlin.test.assertFailsWith

class TodoValidatorTest {

    @Test
    fun `하나_이상의_문자열로_검증시도시_성공해요`() {
        TodoValidator.validate("springrunner")
    }

    @Test
    fun `빈_문자열로_검증시도시_실패해요`() {
        assertFailsWith<TodoRegistrationException> {
            TodoValidator.validate("")
        }
    }
}
