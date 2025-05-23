package sorivma.geometadataservice.application.listener.impl

import org.slf4j.LoggerFactory
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Service
import sorivma.geometadataservice.application.geometadata.MetadataService
import sorivma.geometadataservice.application.listener.GeoMetadataListener
import sorivma.geometadataservice.application.listener.dto.MetadataMessageDto
import sorivma.geometadataservice.application.listener.mapper.toDomain
import java.util.*

@Service
class RabbitMetadataListener(
    private val metadataService: MetadataService
) : GeoMetadataListener {
    private val log = LoggerFactory.getLogger(RabbitMetadataListener::class.java)

    @RabbitListener(queues = ["metadata.in.created.queue"])
    override fun handleCreate(dto: MetadataMessageDto) {
        log.info("Consuming new metadata created event objectId: ${dto.objectId}")
        metadataService.create(dto.toDomain())
    }

    @RabbitListener(queues = ["metadata.in.updated.queue"])
    override fun handleUpdate(dto: MetadataMessageDto) {
        log.info("Consuming new metadata updated event objectId: ${dto.objectId}")
        metadataService.update(dto.objectId, dto.toDomain())
    }

    @RabbitListener(queues = ["metadata.in.deleted.queue"])
    override fun handleDelete(objectId: String) {
        log.info("Consuming delete metadata event objectId: $objectId")
        metadataService.delete(UUID.fromString(objectId))
    }
}