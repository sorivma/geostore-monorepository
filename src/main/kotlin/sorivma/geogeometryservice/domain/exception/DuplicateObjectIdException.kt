package sorivma.geogeometryservice.domain.exception

import java.util.*

class DuplicateObjectIdException(objectId: UUID) : RuntimeException("Object $objectId is already in use")
