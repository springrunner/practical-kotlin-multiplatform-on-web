package todoapp.application

import todoapp.domain.TodoId

/**
 * 할 일 정리기 인터페이스
 *
 * @author springrunner.kr@gmail.com
 */
interface TodoCleanup {

    /**
     * 등록된 할 일을 정리(삭제)한다.
     *
     * @param id 할 일 일련번호
     */
    suspend fun clear(id: TodoId)

    /**
     * 완료된 할 일을 정리(삭제)한다.
     */
    suspend fun clearAllCompleted()
}
