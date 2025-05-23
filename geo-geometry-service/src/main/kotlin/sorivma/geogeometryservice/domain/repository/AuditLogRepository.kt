package sorivma.geogeometryservice.domain.repository

import sorivma.geogeometryservice.domain.model.GeometryAuditLog
import java.util.*

interface AuditLogRepository {
    fun save(auditLog: GeometryAuditLog)
    fun findAllByObjectId(objectId: UUID): List<GeometryAuditLog>
}