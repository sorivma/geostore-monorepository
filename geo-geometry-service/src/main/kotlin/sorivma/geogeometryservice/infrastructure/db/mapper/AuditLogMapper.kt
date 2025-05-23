package sorivma.geogeometryservice.infrastructure.db.mapper

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.springframework.stereotype.Component
import sorivma.geogeometryservice.domain.model.GeometryAuditLog
import sorivma.geogeometryservice.domain.model.GeometryAuditLogAction
import sorivma.geogeometryservice.infrastructure.db.entity.GeometryAuditLogEntity
import java.sql.ResultSet
import java.time.OffsetDateTime
import java.util.*

@Component
class AuditLogMapper(
    private val objectMapper: ObjectMapper,
) {
    fun toEntity(model: GeometryAuditLog): GeometryAuditLogEntity =
        GeometryAuditLogEntity(
            id = model.id,
            objectId = model.objectId,
            version = model.version,
            action = model.action.name,
            userId = model.userId,
            timestamp = model.timestamp,
            metadata = if (model.metadata.isNotEmpty()) objectMapper.writeValueAsString(model.metadata) else null
        )

    fun toParams(entity: GeometryAuditLogEntity): Map<String, Any?> = mapOf(
        "id" to entity.id,
        "objectId" to entity.objectId,
        "version" to entity.version,
        "action" to entity.action,
        "userId" to entity.userId,
        "timestamp" to entity.timestamp,
        "metadata" to entity.metadata
    )

    fun fromResultSet(rs: ResultSet): GeometryAuditLog {
        val metadataJson = rs.getString("metadata")
        val metadata: Map<String, Any> =
            if (metadataJson.isNullOrBlank()) emptyMap()
            else objectMapper.readValue(metadataJson)

        return GeometryAuditLog(
            id = UUID.fromString(rs.getString("id")),
            objectId = UUID.fromString(rs.getString("object_id")),
            version = rs.getInt("version"),
            action = GeometryAuditLogAction.valueOf(rs.getString("action")),
            userId = UUID.fromString(rs.getString("user_id")),
            timestamp = rs.getObject("timestamp", OffsetDateTime::class.java),
            metadata = metadata
        )
    }
}