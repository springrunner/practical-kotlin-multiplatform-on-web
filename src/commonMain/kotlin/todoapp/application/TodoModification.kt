package todoapp.application

import todoapp.domain.TodoId

/**
 * 할 일 수정기 인터페이스
 *
 * @author springrunner.kr@gmail.com
 */
interface TodoModification {

    /**
     * 등록된 할 일을 수정한다.
     *
     * @param id 할 일 일련번호
     * @param text 할 일 내용
     * @param completed 완료 여부
     */
    suspend fun modify(id: TodoId, text: String, completed: Boolean)
}
