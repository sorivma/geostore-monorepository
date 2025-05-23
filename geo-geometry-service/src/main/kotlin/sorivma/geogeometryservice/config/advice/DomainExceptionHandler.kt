package sorivma.geogeometryservice.config.advice

import org.slf4j.LoggerFactory
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import sorivma.geogeometryservice.domain.exception.GeometryNotFoundException
import sorivma.geogeometryservice.domain.exception.VersionNotFoundException
import sorivma.geogeometryservice.config.advice.dto.ErrorResponse
import sorivma.geogeometryservice.domain.exception.SpatialRefSystemIsNotSupported

@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
class DomainExceptionHandler {

    private val log = LoggerFactory.getLogger(DomainExceptionHandler::class.java)

    @ExceptionHandler(GeometryNotFoundException::class)
    fun handleNotFound(ex: GeometryNotFoundException): ResponseEntity<ErrorResponse> {
        log.warn("Geometry not found: ${ex.message}")
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(ErrorResponse.error("GEOMETRY_NOT_FOUND", ex.message))
    }

    @ExceptionHandler(VersionNotFoundException::class)
    fun handleVersionNotFound(ex: VersionNotFoundException): ResponseEntity<ErrorResponse> {
        log.warn("Version not found: ${ex.message}")
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(ErrorResponse.error("VERSION_NOT_FOUND", ex.message))
    }

    @ExceptionHandler(SpatialRefSystemIsNotSupported::class)
    fun handleSpatialRefSystemIsNotSupported(ex: SpatialRefSystemIsNotSupported): ResponseEntity<ErrorResponse> {
        log.warn("Spatial ref system is not supported: ${ex.message}")
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(ErrorResponse.error("SPATIAL_REFSYSTEM_NOT_SUPPORTED", ex.message))
    }
}