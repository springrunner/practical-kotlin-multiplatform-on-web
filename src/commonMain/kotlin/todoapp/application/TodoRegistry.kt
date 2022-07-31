package todoapp.application

import todoapp.domain.TodoId

/**
 * 할 일 등록기 인터페이스
 *
 * @author springrunner.kr@gmail.com
 */
interface TodoRegistry {

    /**
     * 새로운 할 일을 등록한다.
     *
     * @param text 할 일 내용
     * @return 생성된 할 일 일련번호
     */
    suspend fun register(text: String): TodoId
}
