package todoapp.web.command

import kotlinx.serialization.Serializable
import todoapp.domain.TodoValidator

/**
 * 할 일 작성(등록, 수정)시 사용할 명령 클래스
 *
 * @author springrunner.kr@gmail.com
 */
@Serializable
data class WriteTodoCommand(
    val text: String,
    val completed: Boolean = false
) {

    fun validate() = TodoValidator.validate(text)
}
