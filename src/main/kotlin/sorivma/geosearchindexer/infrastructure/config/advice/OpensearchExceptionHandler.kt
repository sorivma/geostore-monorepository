package sorivma.geosearchindexer.infrastructure.config.advice

import org.opensearch.client.opensearch._types.OpenSearchException
import org.slf4j.LoggerFactory
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import sorivma.geogeometryservice.config.advice.dto.ErrorResponse

@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
class OpensearchExceptionHandler {
    private val log = LoggerFactory.getLogger(OpensearchExceptionHandler::class.java)

    @ExceptionHandler(OpenSearchException::class)
    fun handleNotReadable(ex: OpenSearchException): ResponseEntity<ErrorResponse> {
        log.warn("Failed to process request: ${ex.message}")
        log.warn("Status: ${ex.status()}")
        log.warn("Root cause (first): ${ex.error().rootCause().first().reason()}")
        log.warn("Reason: ${ex.response().error().reason()}")
        return ResponseEntity
            .internalServerError()
            .body(ErrorResponse.error("OPENSEARCH_REQUEST_FAILED", "Something went wrong"))
    }
}