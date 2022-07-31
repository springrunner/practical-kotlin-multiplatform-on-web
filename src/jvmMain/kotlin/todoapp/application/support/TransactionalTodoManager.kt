package todoapp.application.support

import org.springframework.transaction.annotation.Transactional
import todoapp.application.TodoCleanup
import todoapp.application.TodoFind
import todoapp.application.TodoModification
import todoapp.application.TodoRegistry

/**
 * 스프링 트랜잭션이 적용된 할 일 관리 컴포넌트
 *
 * @author springrunner.kr@gmail.com
 */
@Transactional
class TransactionalTodoManager(
    private val find: TodoFind,
    private val registry: TodoRegistry,
    private val modification: TodoModification,
    private val cleanup: TodoCleanup
): TodoFind by find, TodoRegistry by registry, TodoModification by modification, TodoCleanup by cleanup
