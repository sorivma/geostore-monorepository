package sorivma.geogeometryservice.config.advice.dto

import java.time.OffsetDateTime

data class ErrorResponse(
    val timestamp: OffsetDateTime,
    val code: String,
    val message: String,
) {
    companion object {
        fun error(code: String, message: String?): ErrorResponse {
            return ErrorResponse(
                timestamp = OffsetDateTime.now(),
                code = code,
                message = message ?: "Unknown error",
            )
        }
    }
}