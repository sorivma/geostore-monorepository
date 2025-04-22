package sorivma.geogeometryservice.domain.exception

class SpatialRefSystemIsNotSupported(srid: Int) : RuntimeException("Spatial ref system is not supported: $srid")