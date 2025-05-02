package sorivma.geometadataservice.domain.model

import java.time.Instant
import java.util.UUID

data class GeoMetadata(
    val objectId: UUID,
    val dcType: String = "Dataset",
    val createdAt: Instant,
    val temporalExtent: TemporalExtent? = null,
    val source: String? = null,
    val region: String? = null,
    val topicCategory: List<String>? = null,
    val properties: Map<String, Any>? = null
)
