package sorivma.geosearchindexer.api.dto

import java.time.LocalDate

data class TemporalFilter(
    val start: LocalDate? = null,
    val end: LocalDate? = null,
)
