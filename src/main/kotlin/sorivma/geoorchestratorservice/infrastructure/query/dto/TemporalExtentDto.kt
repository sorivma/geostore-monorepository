package sorivma.geoorchestratorservice.infrastructure.query.dto

import com.fasterxml.jackson.annotation.JsonInclude
import java.time.Instant

data class TemporalExtentDto(
    val start: Instant,
    @JsonInclude(JsonInclude.Include.NON_NULL)
    val end: Instant? = null
)
