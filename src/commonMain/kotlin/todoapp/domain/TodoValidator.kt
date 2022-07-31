package todoapp.domain

/**
 * 할 일 검증기 인터페이스
 *
 * @author springrunner.kr@gmail.com
 */
object TodoValidator {

    fun validate(text: String) {
        if (text.isBlank()) throw TodoExceptions.creation("todo text is must not be null or empty")
    }
}
