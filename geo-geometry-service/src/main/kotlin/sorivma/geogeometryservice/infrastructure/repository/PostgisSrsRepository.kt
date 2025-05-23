package sorivma.geogeometryservice.infrastructure.repository

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository
import sorivma.geogeometryservice.application.repository.SpatialRefSystemRepository

@Repository
class PostgisSrsRepository(
    private val jdbc: NamedParameterJdbcTemplate
): SpatialRefSystemRepository {
    override fun isSupported(srid: Int): Boolean {
        val sql = "SELECT COUNT(*) FROM spatial_ref_sys WHERE srid= :srid"

        val count = jdbc.queryForObject(sql, mapOf("srid" to srid), Int::class.java)

        return (count ?: 0) > 0
    }
}