package sorivma.geometadataservice.infrastructure.db.mapper

import sorivma.geometadataservice.domain.model.GeoMetadata
import sorivma.geometadataservice.infrastructure.db.entity.MetadataDocument
import java.util.*

fun GeoMetadata.toDocument(): MetadataDocument = MetadataDocument(
    id = this.objectId.toString(),
    objectId = this.objectId.toString(),
    dcType = this.dcType,
    createdAt = this.createdAt,
    temporalExtent = this.temporalExtent,
    source = this.source,
    region = this.region,
    topicCategory = this.topicCategory,
    properties = this.properties
)

fun MetadataDocument.toDomain(): GeoMetadata = GeoMetadata(
    objectId = UUID.fromString(this.objectId),
    dcType = this.dcType,
    createdAt = this.createdAt,
    temporalExtent = this.temporalExtent,
    source = this.source,
    region = this.region,
    topicCategory = this.topicCategory,
    properties = this.properties
)