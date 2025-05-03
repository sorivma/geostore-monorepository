package sorivma.geometadataservice.api.mapper

import sorivma.geometadataservice.api.dto.MetadataDto
import sorivma.geometadataservice.api.dto.TemporalExtentDto
import sorivma.geometadataservice.domain.model.GeoMetadata
import sorivma.geometadataservice.domain.model.TemporalExtent
import java.time.Instant
import java.util.*

fun MetadataDto.toDomain(): GeoMetadata = GeoMetadata(
    objectId = UUID.fromString(objectId),
    dcType = dcType,
    createdAt = Instant.now(),
    temporalExtent = temporalExtent?.let {
        TemporalExtent(it.start, it.end)
    },
    source = source,
    region = region,
    topicCategory = topicCategory,
    properties = properties
)

fun GeoMetadata.toDto(): MetadataDto = MetadataDto(
    objectId = objectId.toString(),
    dcType = dcType,
    temporalExtent = temporalExtent?.let {
        TemporalExtentDto(it.start, it.end)
    },
    source = source,
    region = region,
    topicCategory = topicCategory,
    properties = properties
)