package sorivma.geosearchindexer.application.service.mapper

import sorivma.geosearchindexer.domain.model.IndexedMetadata
import sorivma.geosearchindexer.domain.model.TemporalExtent
import sorivma.geosearchindexer.shared.dto.MetadataIndexDto
import java.time.Instant
import java.util.*

fun MetadataIndexDto.toIndexed(): IndexedMetadata {
    return IndexedMetadata(
        objectId = UUID.fromString(this.objectId),
        anyText = this.anyText,
        dcType = this.dcType,
        region = this.region,
        topicCategory = this.topicCategory,
        temporalExtent = this.temporalExtent?.let {
            TemporalExtent(
                parseEpochString(it.start),
                it.end?.let { end -> parseEpochString(end) }
            )
        }
    )
}

fun parseEpochString(raw: String): Instant {
    val seconds = raw.substringBefore('.').toLong()
    val nanos = raw.substringAfter('.', "0").padEnd(9, '0').take(9).toLong()
    return Instant.ofEpochSecond(seconds, nanos)
}