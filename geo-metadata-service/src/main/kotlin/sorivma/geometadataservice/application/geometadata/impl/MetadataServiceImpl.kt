package sorivma.geometadataservice.application.geometadata.impl

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import sorivma.geometadataservice.application.geometadata.MetadataService
import sorivma.geometadataservice.application.exception.GeoMetadataNotFoundException
import sorivma.geometadataservice.application.publisher.MetadataEventPublisher
import sorivma.geometadataservice.domain.model.GeoMetadata
import sorivma.geometadataservice.infrastructure.db.mapper.toDocument
import sorivma.geometadataservice.infrastructure.db.mapper.toDomain
import sorivma.geometadataservice.infrastructure.repository.MetadataRepository
import java.util.*

@Service
class MetadataServiceImpl(
    private val metadataRepository: MetadataRepository,
    private val metadataEventPublisher: MetadataEventPublisher,
) : MetadataService {
    private val log: Logger = LoggerFactory.getLogger(MetadataServiceImpl::class.java)

    override fun create(metadata: GeoMetadata) {
        val saved = metadataRepository.save(
            metadata.toDocument()
        )
        try {
            metadataEventPublisher.publishCreated(metadata)
        } catch (e: Exception) {
            log.warn("Error while publishing metadata", e)
            metadataRepository.deleteById(saved.id)
        }
    }

    override fun getByObjectId(objectId: UUID): GeoMetadata {
        return metadataRepository.findByObjectId(objectId.toString())?.toDomain() ?: throw GeoMetadataNotFoundException(objectId)
    }

    override fun update(objectId: UUID, metadata: GeoMetadata) {
        val existing = getByObjectId(objectId)
        val updated = metadata.toDocument().copy(id = existing.objectId.toString(), createdAt = existing.createdAt)
        metadataRepository.save(updated)
        try {
            metadataEventPublisher.publishUpdated(metadata)
        } catch (e: Exception) {
            log.warn("Error while publishing metadata", e)
            metadataRepository.save(existing.toDocument())
        }
    }

    override fun delete(objectId: UUID) {
        val existing = metadataRepository.findByObjectId(objectId.toString())?.let {
            metadataRepository.delete(it)
            it
        } ?: throw GeoMetadataNotFoundException(objectId)
        try {
            metadataEventPublisher.publishDeleted(objectId)
        } catch (e: Exception) {
            log.warn("Error while publishing delete event for metadata", e)
            metadataRepository.save(existing)
        }
    }
}