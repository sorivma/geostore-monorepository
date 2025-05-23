package sorivma.geogeometryservice.application.service.impl

import org.slf4j.LoggerFactory
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.stereotype.Service
import sorivma.geogeometryservice.application.service.api.GeometryAuditLogService
import sorivma.geogeometryservice.application.dto.response.GeometryAuditLogDto
import sorivma.geogeometryservice.domain.model.Geometry
import sorivma.geogeometryservice.domain.model.GeometryAuditLog
import sorivma.geogeometryservice.domain.model.GeometryAuditLogAction
import sorivma.geogeometryservice.domain.repository.AuditLogRepository
import java.time.OffsetDateTime
import java.util.*

@Service
class GeometryAuditLogServiceImpl(
    private val auditLogRepository: AuditLogRepository,
) : GeometryAuditLogService {
    private val log = LoggerFactory.getLogger(GeometryServiceImpl::class.java)

    override fun record(geometry: Geometry, action: GeometryAuditLogAction, timestamp: OffsetDateTime) {
        val jwt = SecurityContextHolder.getContext().authentication.principal as Jwt

        log.info("User with Claims [${jwt.claims}] action on geometry [${geometry.id}] with action [$action]")

        val userId = UUID.fromString(jwt.claims["sub"] as String)

        auditLogRepository.save(
            GeometryAuditLog(
                id = UUID.randomUUID(),
                objectId = UUID.randomUUID(),
                version = geometry.version,
                action = action,
                userId = userId,
                timestamp = timestamp
            )
        )
    }

    override fun getLogs(objectId: UUID): List<GeometryAuditLogDto> {
        return auditLogRepository.findAllByObjectId(objectId)
            .map {
                GeometryAuditLogDto(
                    version = it.version,
                    action = it.action,
                    userId = it.userId,
                    timestamp = it.timestamp,
                    metadata = it.metadata
                )
            }
    }
}