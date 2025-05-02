package sorivma.geometadataservice.application.listener.mapper

import sorivma.geometadataservice.application.listener.dto.MetadataMessageDto
import sorivma.geometadataservice.domain.model.GeoMetadata
import sorivma.geometadataservice.domain.model.TemporalExtent

fun MetadataMessageDto.toDomain(): GeoMetadata = GeoMetadata(
    objectId = objectId,
    dcType = dcType,
    createdAt = createdAt,
    temporalExtent = temporalExtent?.let {
        TemporalExtent(it.start, it.end)
    },
    source = source,
    region = region,
    topicCategory = topicCategory,
    properties = properties
)