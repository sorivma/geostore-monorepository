package sorivma.geosearchindexer.shared.dto

import com.fasterxml.jackson.annotation.JsonInclude

data class TemporalExtentDto(
    val start: String,
    @JsonInclude(JsonInclude.Include.NON_NULL)
    val end: String? = null
)
