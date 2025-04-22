package sorivma.geogeometryservice.infrastructure.repository

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository
import sorivma.geogeometryservice.config.geo.GeoProperties
import sorivma.geogeometryservice.domain.model.Geometry
import sorivma.geogeometryservice.domain.repository.GeometryRepository
import sorivma.geogeometryservice.infrastructure.db.formatter.registry.GeometryFormatRegistry
import sorivma.geogeometryservice.infrastructure.db.mapper.GeometryMapper
import java.util.*

@Repository
class JdbcGeometryRepository(
    private val jdbc: NamedParameterJdbcTemplate,
    private val formatRegistry: GeometryFormatRegistry,
    private val geoProperties: GeoProperties,
    private val mapper: GeometryMapper,
) : GeometryRepository {
    override fun save(geometry: Geometry, rawGeometry: String, format: String, sourceSrid: Int) {
        val formatter = formatRegistry.get(format)

        val sql = """
            INSERT INTO geometries (
                id, object_id, version, timestamp, geometry, active, deleted
            ) VALUES (
                :id, :objectId, :version, :timestamp,
                ${formatter.fromSqlExpression("geom", inputSrid = sourceSrid, targetSrid = geoProperties.defaultSrid)},
                :active, :deleted
            )
        """.trimIndent()

        val params = mapOf(
            "id" to geometry.id,
            "objectId" to geometry.objectId,
            "version" to geometry.version,
            "timestamp" to geometry.timestamp,
            "geom" to rawGeometry,
            "active" to geometry.active,
            "deleted" to geometry.deleted
        )

        jdbc.update(sql, params)
    }

    override fun findActiveByObjectId(objectId: UUID): Geometry? {
        val sql = """
        SELECT * FROM geometries 
        WHERE object_id = :objectId AND active = true AND deleted = false
        """.trimIndent()

        return jdbc.query(sql, mapOf("objectId" to objectId)) { rs, _ ->
            mapper.fromResultSet(rs)
        }.firstOrNull()
    }

    override fun findVersionByObjectId(objectId: UUID, version: Int): Geometry? {
        val sql = """
        SELECT * FROM geometries 
        WHERE object_id = :objectId AND version = :version
        """.trimIndent()

        return jdbc.query(
            sql, mapOf(
                "objectId" to objectId,
                "version" to version
            )
        ) { rs, _ -> mapper.fromResultSet(rs) }
            .firstOrNull()
    }

    override fun findAllVersionsByObjectId(objectId: UUID): List<Geometry> {
        return jdbc.query(
            "SELECT * FROM geometries WHERE object_id = :objectId ORDER BY version DESC",
            mapOf("objectId" to objectId)
        ) { rs, _ -> mapper.fromResultSet(rs) }
    }

    override fun deactivateCurrentVersion(objectId: UUID) {
        jdbc.update(
            """
            UPDATE geometries SET active = false WHERE object_id = :objectId AND active = true
            """.trimIndent(),
            mapOf("objectId" to objectId)
        )
    }
}