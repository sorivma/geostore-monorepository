package sorivma.geogeometryservice.domain.exception

import java.util.*

class GeometryNotFoundException(objectId: UUID) :
    RuntimeException("Geometry with objectId '$objectId' not found")