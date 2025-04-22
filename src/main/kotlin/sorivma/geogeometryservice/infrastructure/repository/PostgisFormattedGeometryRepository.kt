package sorivma.geogeometryservice.infrastructure.repository

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository
import sorivma.geogeometryservice.application.model.FormattedGeometry
import sorivma.geogeometryservice.application.repository.FormattedGeometryRepository
import sorivma.geogeometryservice.config.geo.GeoProperties
import sorivma.geogeometryservice.infrastructure.db.formatter.registry.GeometryFormatRegistry
import java.util.*

@Repository
class PostgisFormattedGeometryRepository(
    private val jdbc: NamedParameterJdbcTemplate,
    private val formatRegistry: GeometryFormatRegistry,
    private val geoProperties: GeoProperties
): FormattedGeometryRepository {
    override fun findFormatted(objectId: UUID, version: Int, format: String, srid: Int?): FormattedGeometry? {
        val targetSrid = srid ?: geoProperties.defaultSrid

        val formatter = formatRegistry.get(format)

        val sql = """
            SELECT ${formatter.toSqlExpression(srid = targetSrid)} AS geometry
            FROM geometries
            WHERE object_id = :objectId AND version = :version
        """.trimIndent()

        val params = mapOf(
            "objectId" to objectId,
            "version" to version,
        )

        val raw = jdbc.queryForObject(sql, params, Any::class.java)

        return raw?.let {
            FormattedGeometry(
                format = format,
                geometry = formatter.convertRaw(raw)
            )
        }
    }
}