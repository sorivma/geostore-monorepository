package sorivma.geogeometryservice.infrastructure.repository

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository
import sorivma.geogeometryservice.domain.model.GeometryAuditLog
import sorivma.geogeometryservice.domain.repository.AuditLogRepository
import sorivma.geogeometryservice.infrastructure.db.mapper.AuditLogMapper
import java.util.*

@Repository
class JdbcGeometryAuditRepository(
    private val jdbc: NamedParameterJdbcTemplate,
    private val mapper: AuditLogMapper
): AuditLogRepository {
    override fun save(auditLog: GeometryAuditLog) {
        val entity = mapper.toEntity(auditLog)
        jdbc.update(
            """
            INSERT INTO geometry_audit_log 
            (id, object_id, version, action, user_id, timestamp, metadata)
            VALUES (:id, :objectId, :version, :action, :userId, :timestamp, :metadata)
            """.trimIndent(),
            mapper.toParams(entity)
        )
    }

    override fun findAllByObjectId(objectId: UUID): List<GeometryAuditLog> {
        return jdbc.query(
            """
            SELECT * FROM geometry_audit_log
            WHERE object_id = :objectId
            ORDER BY timestamp DESC
            """.trimIndent(),
            mapOf("objectId" to objectId)
        ) { rs, _ -> mapper.fromResultSet(rs) }
    }
}