package sorivma.geoorchestratorservice.infrastructure.persistence.layer.repository.impl

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository
import sorivma.geoorchestratorservice.domain.model.layer.Layer
import sorivma.geoorchestratorservice.domain.model.layer.LayerType
import sorivma.geoorchestratorservice.infrastructure.persistence.layer.repository.LayerRepository
import java.util.*

@Repository
class JdbcLayerRepository(
    private val jdbc: NamedParameterJdbcTemplate
) : LayerRepository {

    override fun save(layer: Layer): Layer {
        val sql = """
            INSERT INTO layers (id, project_id, name, type, order_num, geometry_type)
            VALUES (:id, :projectId, :name, :type, :orderNum, :geometryType)
            ON CONFLICT (id) DO UPDATE SET
                project_id = :projectId,
                name = :name,
                type = :type,
                order_num = :orderNum,
                geometry_type = :geometryType
        """.trimIndent()

        jdbc.update(
            sql,
            mapOf(
                "id" to layer.id,
                "projectId" to layer.projectId,
                "name" to layer.name,
                "type" to layer.type.name,
                "orderNum" to layer.order,
                "geometryType" to layer.geometryType.name
            )
        )

        return layer
    }

    override fun findById(id: UUID): Layer? {
        val sql = "SELECT * FROM layers WHERE id = :id"
        val params = mapOf("id" to id)

        return jdbc.query(sql, params) { rs, _ ->
            Layer(
                id = UUID.fromString(rs.getString("id")),
                projectId = UUID.fromString(rs.getString("project_id")),
                name = rs.getString("name"),
                type = LayerType.valueOf(rs.getString("type")),
                order = rs.getInt("order_num"),
                geometryType = Layer.GeometryType.valueOf(rs.getString("geometry_type"))
            )
        }.firstOrNull()
    }

    override fun delete(id: UUID) {
        jdbc.update("DELETE FROM layers WHERE id = :id", mapOf("id" to id))
    }

    override fun findByProject(projectId: UUID): List<Layer> {
        val sql = "SELECT * FROM layers WHERE project_id = :projectId ORDER BY order_num"
        return jdbc.query(sql, mapOf("projectId" to projectId)) { rs, _ ->
            Layer(
                id = UUID.fromString(rs.getString("id")),
                projectId = UUID.fromString(rs.getString("project_id")),
                name = rs.getString("name"),
                type = LayerType.valueOf(rs.getString("type")),
                order = rs.getInt("order_num"),
                geometryType = Layer.GeometryType.valueOf(rs.getString("geometry_type"))
            )
        }
    }
}
