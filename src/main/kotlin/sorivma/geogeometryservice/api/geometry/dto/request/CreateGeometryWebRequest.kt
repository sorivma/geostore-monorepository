package sorivma.geogeometryservice.api.geometry.dto.request

import java.util.UUID

data class CreateGeometryWebRequest(
    val objectId: UUID,
    val geometry: Any,
)
