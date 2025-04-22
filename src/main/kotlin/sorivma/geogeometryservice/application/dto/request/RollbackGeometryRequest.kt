package sorivma.geogeometryservice.application.dto.request

import java.util.UUID

data class RollbackGeometryRequest(
    val objectId: UUID,
    val targetVersion: Int
)