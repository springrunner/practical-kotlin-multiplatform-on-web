package todoapp.web.validation

import org.springframework.http.HttpStatus
import org.springframework.validation.BeanPropertyBindingResult
import org.springframework.validation.Errors
import org.springframework.validation.Validator
import org.springframework.web.server.ResponseStatusException

fun <T : Any> Validator.process(target: T): T {
    val errors: Errors = BeanPropertyBindingResult(target, target.javaClass.simpleName);
    validate(target, errors);
    if (errors.hasErrors()) {
        throw ValidationException(errors.toString())
    }
    return target
}

class ValidationException(reason: String): ResponseStatusException(HttpStatus.BAD_REQUEST, reason)
