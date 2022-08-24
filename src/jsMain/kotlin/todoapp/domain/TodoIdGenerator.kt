package todoapp.domain

/**
 * 할 일 일련번호 생성기
 *
 * @author springrunner.kr@gmail.com
 */
actual interface TodoIdGenerator {
    actual fun generateId() = TodoId(external.uuid.v4().lowercase())
}
