package sorivma.geoorchestratorservice.infrastructure.query.dto

import com.fasterxml.jackson.annotation.JsonInclude
import java.time.Instant
import java.util.*

data class IndexedMetadata(
    val objectId: UUID,
    val anyText: String,
    val dcType: String,
    @JsonInclude(JsonInclude.Include.NON_NULL)
    val region: String? = null,
    @JsonInclude(JsonInclude.Include.NON_NULL)
    val topicCategory: List<String>? = null,
    @JsonInclude(JsonInclude.Include.NON_NULL)
    val temporalExtent: TemporalExtentDto? = null,
    val indexedAt: Instant = Instant.now()
)