package sorivma.geometadataservice.application.geometadata

import sorivma.geometadataservice.domain.model.GeoMetadata
import java.util.UUID

interface MetadataService {
    fun create(metadata: GeoMetadata)
    fun getByObjectId(objectId: UUID): GeoMetadata
    fun update(objectId: UUID, metadata: GeoMetadata)
    fun delete(objectId: UUID)
}