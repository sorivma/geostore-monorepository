package sorivma.geometadataservice.application.publisher

import sorivma.geometadataservice.domain.model.GeoMetadata
import java.util.*

interface MetadataEventPublisher {
    fun publishCreated(metadata: GeoMetadata)
    fun publishUpdated(metadata: GeoMetadata)
    fun publishDeleted(objectId: UUID)
}