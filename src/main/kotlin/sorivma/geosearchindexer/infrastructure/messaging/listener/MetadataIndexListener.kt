package sorivma.geosearchindexer.infrastructure.messaging.listener

import org.slf4j.LoggerFactory
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Component
import sorivma.geosearchindexer.application.service.IndexingService
import sorivma.geosearchindexer.shared.dto.MetadataIndexDto

@Component
class MetadataIndexListener(
    private val indexingService: IndexingService,
) {
    private val log = LoggerFactory.getLogger(MetadataIndexListener::class.java)

    @RabbitListener(queues = ["metadata.index.created.queue"])
    fun onCreated(dto: MetadataIndexDto) {
        log.info("Received created event for objectId=${dto.objectId}")
        indexingService.index(dto)
    }

    @RabbitListener(queues = ["metadata.index.updated.queue"])
    fun onUpdated(dto: MetadataIndexDto) {
        log.info("Received updated event for objectId=${dto.objectId}")
        indexingService.update(dto)
    }

    @RabbitListener(queues = ["metadata.index.deleted.queue"])
    fun onDeleted(objectId: String) {
        log.info("Received deleted event for objectId=$objectId")
        indexingService.delete(objectId)
    }
}