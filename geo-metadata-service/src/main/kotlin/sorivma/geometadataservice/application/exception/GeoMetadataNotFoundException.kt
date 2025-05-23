package sorivma.geometadataservice.application.exception

import java.util.UUID

class GeoMetadataNotFoundException(objectId: UUID) : RuntimeException("Could not find geo metadata for $objectId")