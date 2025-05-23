package sorivma.geometadataservice.infrastructure.repository

import org.springframework.data.mongodb.repository.MongoRepository
import sorivma.geometadataservice.infrastructure.db.entity.MetadataDocument

interface MetadataRepository : MongoRepository<MetadataDocument, String> {
    fun findByObjectId(objectId: String): MetadataDocument?
}