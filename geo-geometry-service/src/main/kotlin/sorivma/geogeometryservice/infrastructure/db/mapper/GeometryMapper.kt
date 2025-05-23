package sorivma.geogeometryservice.infrastructure.db.mapper

import org.springframework.stereotype.Component
import sorivma.geogeometryservice.domain.model.Geometry
import java.sql.ResultSet
import java.time.OffsetDateTime
import java.util.*

@Component
class GeometryMapper {
    fun fromResultSet(rs: ResultSet): Geometry =
        Geometry(
            id = UUID.fromString(rs.getString("id")),
            objectId = UUID.fromString(rs.getString("object_id")),
            version = rs.getInt("version"),
            timestamp = rs.getObject("timestamp", OffsetDateTime::class.java),
            active = rs.getBoolean("active"),
            deleted = rs.getBoolean("deleted")
        )
}