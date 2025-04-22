package sorivma.geogeometryservice.application.listener.impl

import org.slf4j.LoggerFactory
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.core.convert.converter.Converter
import org.springframework.messaging.handler.annotation.Headers
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.stereotype.Service
import sorivma.geogeometryservice.application.dto.request.CreateGeometryRequest
import sorivma.geogeometryservice.application.dto.request.UpdateGeometryRequest
import sorivma.geogeometryservice.application.service.api.GeometryService
import java.util.*

@Service
class RabbitGeometryListener(
    private val geometryService: GeometryService,
    private val jwtDecoder: JwtDecoder,
    private val jwtAuthenticationConverter: Converter<Jwt, out AbstractAuthenticationToken>
) {

    private val log = LoggerFactory.getLogger(RabbitGeometryListener::class.java)

    @RabbitListener(queues = ["geometry.in.created.queue"])
    fun onCreate(
        @Payload createGeometryRequest: CreateGeometryRequest,
        @Headers headers: Map<String, Any>
    ) {
        log.info("Consuming new geometry created event, objectId: ${createGeometryRequest.objectId}")

        withSecurityContext(headers) {
            try {
                geometryService.create(createGeometryRequest)
            } catch (e: Exception) {
                log.warn("Exception occurred while creating geometry event with id ${createGeometryRequest.objectId}", e)
            }
        }
    }

    @RabbitListener(queues = ["geometry.in.updated.queue"])
    fun onUpdate(
        @Payload updateGeometryRequest: UpdateGeometryRequest,
        @Headers headers: Map<String, Any>
    ) {
        log.info("Consuming geometry updated event, objectId: ${updateGeometryRequest.objectId}")

        withSecurityContext(headers) {
            geometryService.update(updateGeometryRequest)
        }
    }

    @RabbitListener(queues = ["geometry.in.deleted.queue"])
    fun onDelete(
        @Payload objectId: String,
        @Headers headers: Map<String, Any>
    ) {
        log.info("Consuming delete geometry event, objectId: $objectId")

        withSecurityContext(headers) {
            try {
                geometryService.delete(UUID.fromString(objectId))
            } catch (e: Exception) {
                log.warn("Exception occured while deleting geometry with id $objectId", e)
            }
        }
    }

    /**
     * Устанавливает SecurityContext из JWT токена в заголовке "Authorization" и выполняет действие.
     */
    private fun withSecurityContext(headers: Map<String, Any>, action: () -> Unit) {
        try {
            val rawToken = headers["Authorization"]?.toString()
            log.info("Accessing token: $rawToken")
            if (!rawToken.isNullOrBlank()) {
                val jwt = jwtDecoder.decode(rawToken)
                val authentication = jwtAuthenticationConverter.convert(jwt)
                SecurityContextHolder.getContext().authentication = authentication
            } else throw IllegalArgumentException("Token is empty")
        } catch (ex: Exception) {
            log.warn("JWT token from RabbitMQ headers is invalid or missing", ex)
        }

        try {
            action()
        } finally {
            SecurityContextHolder.clearContext()
        }
    }
}