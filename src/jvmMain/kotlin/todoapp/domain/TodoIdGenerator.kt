package todoapp.domain

import java.util.UUID

/**
 * 할 일 일련번호 생성기
 *
 * @author springrunner.kr@gmail.com
 */
actual interface TodoIdGenerator {
    actual fun generateId() = TodoId(UUID.randomUUID().toString().lowercase())
}
