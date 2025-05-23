package sorivma.geoorchestratorservice.infrastructure.query.dto

import java.time.LocalDate

data class TemporalFilter(
    val start: LocalDate? = null,
    val end: LocalDate? = null,
)
