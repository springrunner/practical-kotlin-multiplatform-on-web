package todoapp.domain

import todoapp.SystemException

/**
 * 할 일 서비스 제공 중 발생 가능한 최상위 예외 클래스
 *
 * @author springrunner.kr@gmail.com
 */
open class TodoException(
    message: String,
    cause: Throwable? = null
): SystemException(message = message, cause = cause)

/**
 * 할 일 등록 과정에서 발생 가능한 예외
 *
 * @author springrunner.kr@gmail.com
 */
class TodoRegistrationException(message: String): TodoException(message)

/**
 * 저장소에서 할 일을 찾는 과정에서 할 일이 없을 때 발생 가능한 예외
 */
class TodoNotFoundException(message: String): TodoException(message) {

    companion object {
        fun of(id: TodoId) = TodoNotFoundException("not found todo(id: $id)")
    }
}

object TodoExceptions {
    fun creation(message: String) = TodoRegistrationException(message)
    fun notFound(id: TodoId) = TodoNotFoundException.of(id)
}
