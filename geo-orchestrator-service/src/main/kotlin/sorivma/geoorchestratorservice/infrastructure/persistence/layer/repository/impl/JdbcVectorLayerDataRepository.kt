package sorivma.geoorchestratorservice.infrastructure.persistence.layer.repository.impl

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import sorivma.geoorchestratorservice.domain.model.layer.VectorLayerData
import sorivma.geoorchestratorservice.infrastructure.persistence.layer.repository.VectorLayerDataRepository
import java.util.*

@Repository
class JdbcVectorLayerDataRepository(
    private val jdbc: NamedParameterJdbcTemplate
) : VectorLayerDataRepository {

    override fun save(data: VectorLayerData) {
        val sql = """
            INSERT INTO vector_layer_data (layer_id, object_ids, fill_color, stroke_color, stroke_width)
            VALUES (:layerId, :objectIds, :fillColor, :strokeColor, :strokeWidth)
            ON CONFLICT (layer_id) DO UPDATE SET
                object_ids = :objectIds,
                fill_color = :fillColor,
                stroke_color = :strokeColor,
                stroke_width = :strokeWidth
        """.trimIndent()

        jdbc.update(
            sql,
            mapOf(
                "layerId" to data.layerId,
                "objectIds" to data.objectIds.toTypedArray(),
                "fillColor" to data.style.fillColor,
                "strokeColor" to data.style.strokeColor,
                "strokeWidth" to data.style.strokeWidth
            )
        )
    }

    override fun findByLayerId(layerId: UUID): VectorLayerData? {
        val sql = "SELECT * FROM vector_layer_data WHERE layer_id = :layerId"

        return jdbc.query(sql, mapOf("layerId" to layerId)) { rs, _ ->
            VectorLayerData(
                layerId = UUID.fromString(rs.getString("layer_id")),
                objectIds = (rs.getArray("object_ids").array as Array<*>)
                    .map { UUID.fromString(it.toString()) },
                style = VectorLayerData.VectorStyle(
                    fillColor = rs.getString("fill_color"),
                    strokeColor = rs.getString("stroke_color"),
                    strokeWidth = rs.getDouble("stroke_width")
                ),
            )
        }.firstOrNull()
    }

    override fun deleteByLayerId(layerId: UUID) {
        jdbc.update("DELETE FROM vector_layer_data WHERE layer_id = :layerId", mapOf("layerId" to layerId))
    }

    @Transactional
    override fun deleteObject(layerId: UUID, objectId: UUID) {


        val currentObjectIds: List<UUID> = jdbc.queryForObject(
            "SELECT object_ids FROM vector_layer_data WHERE layer_id = :layerId",
            mapOf("layerId" to layerId)
        ) { rs, _ ->
            (rs.getArray("object_ids").array as Array<*>)
                .map { UUID.fromString(it.toString()) }
        }?.toList() ?: return

        val updatedIds = currentObjectIds.filterNot { it == objectId }

        jdbc.update(
            "UPDATE vector_layer_data SET object_ids = :updatedIds WHERE layer_id = :layerId",
            mapOf(
                "updatedIds" to updatedIds.toTypedArray(),
                "layerId" to layerId
            )
        )
    }

}