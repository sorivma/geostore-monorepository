package sorivma.geometadataservice.infrastructure.db.entity

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import sorivma.geometadataservice.domain.model.TemporalExtent
import java.time.Instant

@Document(collection = "metadata")
data class MetadataDocument(
    @Id val id: String,
    val objectId: String,
    val dcType: String,
    val createdAt: Instant,
    val temporalExtent: TemporalExtent?,
    val source: String?,
    val region: String?,
    val topicCategory: List<String>?,
    val properties: Map<String, Any>?,
)
