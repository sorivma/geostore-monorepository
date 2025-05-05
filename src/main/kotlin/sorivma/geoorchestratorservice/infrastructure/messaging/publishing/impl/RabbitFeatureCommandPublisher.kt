package sorivma.geoorchestratorservice.infrastructure.messaging.publishing.impl

import org.slf4j.LoggerFactory
import org.springframework.amqp.core.MessagePostProcessor
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.stereotype.Component
import sorivma.geoorchestratorservice.infrastructure.messaging.publishing.FeatureCommandPublisher
import sorivma.geoorchestratorservice.infrastructure.messaging.publishing.dto.CreateGeometryRequest
import sorivma.geoorchestratorservice.infrastructure.messaging.publishing.dto.MetadataMessageDto
import sorivma.geoorchestratorservice.infrastructure.messaging.publishing.dto.UpdateGeometryRequest
import java.util.*

@Component
class RabbitFeatureCommandPublisher(
    private val rabbitTemplate: RabbitTemplate
) : FeatureCommandPublisher {

    private val logger = LoggerFactory.getLogger(RabbitFeatureCommandPublisher::class.java)


    override fun publishMetadataCreated(event: MetadataMessageDto) {
        rabbitTemplate.convertAndSend("metadata.in.created.queue", event)
        logger.info("Published to metadata.in.created.queue (geo-exchange), objectId={}", event.objectId)
    }

    override fun publishMetadataUpdated(event: MetadataMessageDto) {
        rabbitTemplate.convertAndSend( "metadata.in.updated.queue", event)
        logger.info("Published to metadata.in.updated.queue (geo-exchange), objectId={}", event.objectId)
    }

    override fun publishMetadataDeleted(objectId: UUID) {
        rabbitTemplate.convertAndSend( "metadata.in.deleted.queue", objectId.toString())
        logger.info("Published to metadata.in.deleted.queue (geo-exchange), objectId={}", objectId)
    }

    override fun publishGeometryCreated(event: CreateGeometryRequest) {
        rabbitTemplate.convertAndSend(
            "geometry.in.created.queue",
            event,
            withAuthHeader()
        )
        logger.info("Published to geometry.in.created.queue, objectId={}", event.objectId)
    }

    override fun publishGeometryUpdated(event: UpdateGeometryRequest) {
        rabbitTemplate.convertAndSend(
            "geometry.in.updated.queue",
            event,
            withAuthHeader()
        )
        logger.info("Published to geometry.in.updated.queue, objectId={}", event.objectId)
    }

    override fun publishGeometryDeleted(objectId: UUID) {
        rabbitTemplate.convertAndSend(
            "geometry.in.deleted.queue",
            objectId,
            withAuthHeader()
        )
    }

    private fun bearerToken(): String {
        val auth = SecurityContextHolder.getContext().authentication
        val token = (auth.credentials as? Jwt) ?: throw IllegalStateException("No token found")
        return token.tokenValue
    }

    private fun withAuthHeader(): MessagePostProcessor = MessagePostProcessor { message ->
        val token = bearerToken()
        message.messageProperties.setHeader("Authorization", token)

        message
    }
}