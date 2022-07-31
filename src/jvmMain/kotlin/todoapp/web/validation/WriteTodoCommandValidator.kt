package todoapp.web.validation

import org.springframework.util.ClassUtils
import org.springframework.validation.Errors
import org.springframework.validation.Validator
import todoapp.domain.TodoException
import todoapp.web.command.WriteTodoCommand

/**
 * 할 일 작성(등록, 변경) 명령 입력값 검증기
 *
 * @author springrunner.kr@gmail.com
 */
class WriteTodoCommandValidator : Validator {

    override fun supports(clazz: Class<*>) = ClassUtils.isAssignable(clazz, WriteTodoCommand::class.java)

    override fun validate(target: Any, errors: Errors) {
        if (target is WriteTodoCommand) {
            try {
                target.validate()
            } catch (error: TodoException) {
                errors.reject("InvalidParameters", error.message!!)
            }
        }
    }
}
