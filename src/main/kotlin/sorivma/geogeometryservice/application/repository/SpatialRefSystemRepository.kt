package sorivma.geogeometryservice.application.repository

interface SpatialRefSystemRepository {
    fun isSupported(srid: Int): Boolean
}