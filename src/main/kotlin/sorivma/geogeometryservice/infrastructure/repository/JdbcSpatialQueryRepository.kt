package sorivma.geogeometryservice.infrastructure.repository

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository
import sorivma.geogeometryservice.api.spatial.dto.BoundingBox
import sorivma.geogeometryservice.api.spatial.dto.response.BoundingBoxResponse
import sorivma.geogeometryservice.api.spatial.dto.response.CrsResponse
import sorivma.geogeometryservice.application.repository.SpatialQueryRepository
import java.util.*

@Repository
class JdbcSpatialQueryRepository(
    private val jdbc: NamedParameterJdbcTemplate
) : SpatialQueryRepository {
    override fun findBoundingBoxes(objectIds: List<UUID>, srid: Int): List<BoundingBoxResponse> {
        val sql = """
            SELECT object_id,
                st_xmin(st_transform(geometry, :srid)) AS minx,
                st_ymin(st_transform(geometry, :srid)) AS miny,
                st_xmax(st_transform(geometry, :srid)) AS maxx,
                st_ymax(st_transform(geometry, :srid)) AS maxy
            FROM geometries
            WHERE object_id IN (:objectIds) AND active = true AND deleted = false
        """.trimIndent()

        val params = mapOf("objectIds" to objectIds, "srid" to srid)

        return jdbc.query(sql, params) { rs, _ ->
            BoundingBoxResponse(
                objectId = rs.getString("object_id"),
                srid = srid,
                bbox = BoundingBox(
                    minX = rs.getDouble("minx"),
                    minY = rs.getDouble("miny"),
                    maxX = rs.getDouble("maxx"),
                    maxY = rs.getDouble("maxy")
                )
            )
        }
    }

    override fun findIntersectingObjectIds(
        objectIds: List<UUID>,
        bbox: BoundingBox,
        bboxSrid: Int,
        targetSrid: Int
    ): List<String> {
        val sql = """
            SELECT object_id
            FROM geometries
            WHERE object_id IN (:objectIds)
                AND active = true AND deleted = false
                AND st_intersects(
                    geometry,
                    ST_transform(st_makeenvelope(:minX, :minY, :maxX, :maxY, :srid), :targetSrid)
                )
        """.trimIndent()

        val params = mapOf(
            "objectIds" to objectIds,
            "minX" to bbox.minX,
            "minY" to bbox.minY,
            "maxX" to bbox.maxX,
            "maxY" to bbox.maxY,
            "srid" to bboxSrid,
            "targetSrid" to targetSrid
        )

        return jdbc.query(sql, params) { rs, _ ->
            rs.getString("object_id")
        }
    }

    override fun findCrsByObjectIds(objectIds: List<UUID>): List<CrsResponse> {
        val sql = """
            SELECT object_id, ST_SRID(geometry) AS srid
            FROM geometries
            WHERE object_id IN (:objectIds) AND active = true AND deleted = false
        """.trimIndent()

        val params = mapOf("objectIds" to objectIds)

        return jdbc.query(sql, params) { rs, _ ->
            CrsResponse(
                objectId = rs.getString("object_id"),
                srid = rs.getInt("srid"),
            )
        }
    }
}