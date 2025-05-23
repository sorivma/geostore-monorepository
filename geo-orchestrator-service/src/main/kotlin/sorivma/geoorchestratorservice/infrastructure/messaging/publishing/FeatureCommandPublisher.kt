package sorivma.geoorchestratorservice.infrastructure.messaging.publishing

import sorivma.geoorchestratorservice.infrastructure.messaging.publishing.dto.CreateGeometryRequest
import sorivma.geoorchestratorservice.infrastructure.messaging.publishing.dto.MetadataMessageDto
import sorivma.geoorchestratorservice.infrastructure.messaging.publishing.dto.UpdateGeometryRequest
import java.util.*

interface FeatureCommandPublisher {
    fun publishMetadataCreated(event: MetadataMessageDto)
    fun publishMetadataUpdated(event: MetadataMessageDto)
    fun publishMetadataDeleted(objectId: UUID)

    fun publishGeometryCreated(event: CreateGeometryRequest)
    fun publishGeometryUpdated(event: UpdateGeometryRequest)
    fun publishGeometryDeleted(objectId: UUID)
}