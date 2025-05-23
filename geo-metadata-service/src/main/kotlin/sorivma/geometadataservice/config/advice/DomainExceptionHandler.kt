package sorivma.geometadataservice.config.advice

import org.slf4j.LoggerFactory
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import sorivma.geometadataservice.config.advice.dto.ErrorResponse
import sorivma.geometadataservice.application.exception.GeoMetadataNotFoundException

@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
class DomainExceptionHandler {

    private val log = LoggerFactory.getLogger(DomainExceptionHandler::class.java)

    @ExceptionHandler(GeoMetadataNotFoundException::class)
    fun handleNotFound(ex: GeoMetadataNotFoundException): ResponseEntity<ErrorResponse> {
        log.warn("Geometry not found: ${ex.message}")
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(ErrorResponse.error("GEOMETRY_NOT_FOUND", ex.message))
    }
}