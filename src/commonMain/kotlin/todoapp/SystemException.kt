package todoapp

/**
 * 시스템 운영 중 발생 가능한 최상위 예외 클래스
 *
 * @author springrunner.kr@gmail.com
 */
abstract class SystemException(
    message: String,
    cause: Throwable? = null
): RuntimeException(message, cause)
