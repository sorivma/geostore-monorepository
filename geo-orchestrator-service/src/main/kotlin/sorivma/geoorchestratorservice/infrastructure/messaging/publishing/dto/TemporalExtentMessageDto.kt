package sorivma.geoorchestratorservice.infrastructure.messaging.publishing.dto

import java.time.Instant

class TemporalExtentMessageDto(
    val start: Instant,
    val end: Instant? = null,
)
