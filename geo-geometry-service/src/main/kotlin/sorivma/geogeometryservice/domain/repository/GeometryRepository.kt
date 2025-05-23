package sorivma.geogeometryservice.domain.repository

import sorivma.geogeometryservice.domain.model.Geometry
import java.util.*

interface GeometryRepository {
    fun save(geometry: Geometry, rawGeometry: String, format: String, sourceSrid: Int)
    fun findActiveByObjectId(objectId: UUID): Geometry?
    fun findVersionByObjectId(objectId: UUID, version: Int): Geometry?
    fun findAllVersionsByObjectId(objectId: UUID): List<Geometry>
    fun deactivateCurrentVersion(objectId: UUID)
}