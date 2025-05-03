package sorivma.geometadataservice.application.publisher.impl

import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.stereotype.Service
import sorivma.geometadataservice.application.publisher.MetadataEventPublisher
import sorivma.geometadataservice.application.publisher.dto.MetadataIndexDto
import sorivma.geometadataservice.application.publisher.dto.TemporalExtentDto
import sorivma.geometadataservice.application.publisher.textGenerator.AnyTextGenerator
import sorivma.geometadataservice.domain.model.GeoMetadata
import java.util.*

@Service
class RabbitEventPublisher(
    private val rabbitTemplate: RabbitTemplate,
    private val textGenerator: AnyTextGenerator
): MetadataEventPublisher {
    override fun publishCreated(metadata: GeoMetadata) {
        rabbitTemplate.convertAndSend(
            "geo.exchange",
            "metadata.index.created",
            metadata.toIndexDto()
        )
    }

    override fun publishUpdated(metadata: GeoMetadata) {
        rabbitTemplate.convertAndSend(
            "geo.exchange",
            "metadata.index.updated",
            metadata.toIndexDto()
        )
    }

    override fun publishDeleted(objectId: UUID) {
        rabbitTemplate.convertAndSend(
            "geo.exchange",
            "metadata.index.deleted",
            objectId.toString()
        )
    }

    fun GeoMetadata.toIndexDto(): MetadataIndexDto = MetadataIndexDto(
        objectId = objectId.toString(),
        anyText = textGenerator.fromMetadata(this),
        dcType = dcType,
        region = region,
        topicCategory = topicCategory,
        temporalExtent = temporalExtent?.let {
            TemporalExtentDto(it.start, it.end)
        }
    )
}