package sorivma.geosearchindexer.shared.dto

import com.fasterxml.jackson.annotation.JsonInclude

data class MetadataIndexDto(
    val objectId: String,
    val anyText: String,
    val dcType: String,
    @JsonInclude(JsonInclude.Include.NON_NULL)
    val region: String? = null,
    @JsonInclude(JsonInclude.Include.NON_NULL)
    val topicCategory: List<String>? = null,
    @JsonInclude(JsonInclude.Include.NON_NULL)
    val temporalExtent: TemporalExtentDto? = null
)
