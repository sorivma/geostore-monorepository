package sorivma.geoorchestratorservice.infrastructure.query.dto

import com.fasterxml.jackson.annotation.JsonInclude
import sorivma.geoorchestratorservice.infrastructure.query.dto.TemporalExtentDto

data class MetadataDto(
    val objectId: String,
    val dcType: String = "Dataset",
    @JsonInclude(JsonInclude.Include.NON_NULL)
    val temporalExtent: TemporalExtentDto? = null,
    @JsonInclude(JsonInclude.Include.NON_NULL)
    val source: String? = null,
    @JsonInclude(JsonInclude.Include.NON_NULL)
    val region: String? = null,
    @JsonInclude(JsonInclude.Include.NON_NULL)
    val topicCategory: List<String>? = null,
    @JsonInclude(JsonInclude.Include.NON_NULL)
    val properties: Map<String, Any>? = null
)
