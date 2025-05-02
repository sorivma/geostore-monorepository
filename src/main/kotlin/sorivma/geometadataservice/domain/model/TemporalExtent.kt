package sorivma.geometadataservice.domain.model

import java.time.Instant

data class TemporalExtent(
    val start: Instant,
    val end: Instant? = null,
)