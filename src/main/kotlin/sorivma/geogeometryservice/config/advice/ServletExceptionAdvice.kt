package sorivma.geogeometryservice.config.advice

import org.slf4j.LoggerFactory
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.servlet.resource.NoResourceFoundException
import sorivma.geogeometryservice.config.advice.dto.ErrorResponse
import javax.naming.AuthenticationException

@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
class ServletExceptionAdvice {

    private val log = LoggerFactory.getLogger(ServletExceptionAdvice::class.java)

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleNotReadable(ex: HttpMessageNotReadableException): ResponseEntity<ErrorResponse> {
        log.warn("Failed to parse request: ${ex.message}")
        return ResponseEntity
            .badRequest()
            .body(ErrorResponse.error("REQUEST_NOT_READABLE", "Request payload is malformed"))
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidation(ex: MethodArgumentNotValidException): ResponseEntity<ErrorResponse> {
        val errors = ex.bindingResult.fieldErrors.joinToString("; ") {
            "${it.field} ${it.defaultMessage}"
        }
        log.warn("Validation failed: $errors")
        return ResponseEntity
            .badRequest()
            .body(ErrorResponse.error("VALIDATION_FAILED", errors))
    }

    @ExceptionHandler(MissingServletRequestParameterException::class)
    fun handleMissingParam(ex: MissingServletRequestParameterException): ResponseEntity<ErrorResponse> {
        log.warn("Missing request parameter: ${ex.parameterName}")
        return ResponseEntity
            .badRequest()
            .body(ErrorResponse.error("MISSING_PARAMETER", "Required parameter '${ex.parameterName}' is missing"))
    }

    @ExceptionHandler(NoResourceFoundException::class)
    fun handleNoResource(ex: NoResourceFoundException): ResponseEntity<ErrorResponse> {
        log.warn("Resource not found: ${ex.message}")
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(ErrorResponse.error("RESOURCE_NOT_FOUND", ex.message))
    }

    @ExceptionHandler(AccessDeniedException::class)
    fun handleAccessDenied(ex: AccessDeniedException): ResponseEntity<ErrorResponse> {
        log.warn("Access denied: ${ex.message}")
        return ResponseEntity
            .status(HttpStatus.FORBIDDEN)
            .body(ErrorResponse.error("ACCESS_DENIED", "You don't have permission to perform this action"))
    }

    @ExceptionHandler(AuthenticationException::class)
    fun handleUnauthenticated(ex: AuthenticationException): ResponseEntity<ErrorResponse> {
        log.warn("Authentication failed: ${ex.message}")
        return ResponseEntity
            .status(HttpStatus.UNAUTHORIZED)
            .body(ErrorResponse.error("UNAUTHORIZED", "Authentication required"))
    }
}