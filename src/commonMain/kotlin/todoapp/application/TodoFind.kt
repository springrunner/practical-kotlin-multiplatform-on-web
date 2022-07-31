package todoapp.application

import todoapp.domain.Todo
import todoapp.domain.TodoId
import todoapp.domain.Todos

/**
 * 할 일 검색기 인터페이스
 *
 * @author springrunner.kr@gmail.com
 */
interface TodoFind {

    /**
     * 등록된 모든 할 일 목록을 반환한다.
     * 할 일이 없으면 빈 목록을 반환한다.
     *
     * @return 할 일 목록
     */
    suspend fun all(): Todos

    /**
     * 할 일 일련번호로 할 일를 찾아 반환한다.
     *
     * @return 할 일
     */
    suspend fun byId(id: TodoId): Todo
}
