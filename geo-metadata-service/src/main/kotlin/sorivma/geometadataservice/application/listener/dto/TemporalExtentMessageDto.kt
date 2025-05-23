package sorivma.geometadataservice.application.listener.dto

import java.time.Instant

class TemporalExtentMessageDto(
    val start: Instant,
    val end: Instant? = null,
)
