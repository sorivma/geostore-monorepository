package sorivma.geometadataservice.application.listener.dto

import java.time.Instant
import java.util.*

data class MetadataMessageDto(
    val objectId: UUID,
    val dcType: String = "Dataset",
    val createdAt: Instant,
    val temporalExtent: TemporalExtentMessageDto? = null,
    val source: String? = null,
    val region: String? = null,
    val topicCategory: List<String>? = null,
    val properties: Map<String, Any>? = null
)