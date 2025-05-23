package sorivma.geoorchestratorservice.infrastructure.persistence.layer.repository.impl

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository
import sorivma.geoorchestratorservice.domain.model.layer.RasterLayerData
import sorivma.geoorchestratorservice.infrastructure.persistence.layer.repository.RasterLayerDataRepository
import java.util.*

@Repository
class JdbcRasterLayerDataRepository(
    private val jdbc: NamedParameterJdbcTemplate
) : RasterLayerDataRepository {

    override fun save(data: RasterLayerData) {
        val sql = """
            INSERT INTO raster_layer_data (layer_id, cog_url, tilejson_url, attribution, opacity)
            VALUES (:layerId, :cogUrl, :tileJsonUrl, :attribution, :opacity)
            ON CONFLICT (layer_id) DO UPDATE SET
                cog_url = :cogUrl,
                tilejson_url = :tileJsonUrl,
                attribution = :attribution,
                opacity = :opacity
        """.trimIndent()

        jdbc.update(
            sql,
            mapOf(
                "layerId" to data.layerId,
                "cogUrl" to data.cogUrl,
                "tileJsonUrl" to data.tileJsonUrl,
                "attribution" to data.attribution,
                "opacity" to data.style.opacity,
            )
        )
    }

    override fun findByLayerId(layerId: UUID): RasterLayerData? {
        val sql = "SELECT * FROM raster_layer_data WHERE layer_id = :layerId"

        return jdbc.query(sql, mapOf("layerId" to layerId)) { rs, _ ->
            RasterLayerData(
                layerId = UUID.fromString(rs.getString("layer_id")),
                cogUrl = rs.getString("cog_url"),
                tileJsonUrl = rs.getString("tilejson_url"),
                attribution = rs.getString("attribution"),
                style = RasterLayerData.RasterStyle(
                    rs.getDouble("opacity")
                )
            )
        }.firstOrNull()
    }

    override fun delete(layerId: UUID) {
        jdbc.update("DELETE FROM raster_layer_data WHERE layer_id = :layerId", mapOf("layerId" to layerId))
    }
}