package sorivma.geoorchestratorservice.domain.model.layer

import java.util.UUID

sealed interface LayerData {
    val layerId: UUID
}