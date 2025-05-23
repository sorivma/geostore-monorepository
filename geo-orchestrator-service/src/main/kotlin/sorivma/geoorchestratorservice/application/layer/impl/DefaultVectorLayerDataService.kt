package sorivma.geoorchestratorservice.application.layer.impl

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import sorivma.geoorchestratorservice.application.layer.VectorLayerDataService
import sorivma.geoorchestratorservice.application.layer.dto.VectorLayerDataDto
import sorivma.geoorchestratorservice.application.project.ProjectAccessService
import sorivma.geoorchestratorservice.domain.model.layer.Layer
import sorivma.geoorchestratorservice.domain.model.layer.VectorLayerData
import sorivma.geoorchestratorservice.infrastructure.messaging.publishing.FeatureCommandPublisher
import sorivma.geoorchestratorservice.infrastructure.persistence.layer.repository.LayerRepository
import sorivma.geoorchestratorservice.infrastructure.persistence.layer.repository.VectorLayerDataRepository
import sorivma.geoorchestratorservice.shared.exception.LayerNotFoundException
import sorivma.geoorchestratorservice.shared.security.SecurityContextHolderFacade
import java.util.*

@Service
class DefaultVectorLayerDataService(
    private val repository: VectorLayerDataRepository,
    private val layerRepository: LayerRepository,
    private val projectAccessService: ProjectAccessService,
    private val eventPublisher: FeatureCommandPublisher,
    private val security: SecurityContextHolderFacade
) : VectorLayerDataService {

    private val logger = LoggerFactory.getLogger(DefaultVectorLayerDataService::class.java)

    override fun save(layerId: UUID, data: VectorLayerDataDto) {
        getLayerWithEditAccess(layerId)

        val entity = VectorLayerData(
            layerId = layerId,
            objectIds = data.objectIds,
            style = VectorLayerData.VectorStyle(
                fillColor = data.fillColor,
                strokeColor = data.strokeColor,
                strokeWidth = data.strokeWidth
            )
        )

        repository.save(entity)

        logger.info("User {} saved vector data for layer {}", currentUserId(), layerId)
    }

    override fun getByLayerId(layerId: UUID): VectorLayerDataDto {
        getLayerWithViewAccess(layerId)

        val data = repository.findByLayerId(layerId) ?: throw IllegalStateException("No vector data for layer $layerId")

        return VectorLayerDataDto(
            objectIds = data.objectIds,
            fillColor = data.style.fillColor,
            strokeColor = data.style.strokeColor,
            strokeWidth = data.style.strokeWidth
        )
    }

    @Transactional
    override fun deleteByLayerId(layerId: UUID) {
        getLayerWithEditAccess(layerId)

        val existing = repository.findByLayerId(layerId)
        repository.deleteByLayerId(layerId)

        logger.info("User {} deleted vector data from layer {}", currentUserId(), layerId)

        existing?.objectIds?.forEach { objectId ->
            eventPublisher.publishMetadataDeleted(objectId)
            eventPublisher.publishGeometryDeleted(objectId)
        }
    }

    @Transactional
    override fun delete(objectId: UUID, layerId: UUID) {
        getLayerWithEditAccess(layerId)

        repository.deleteObject(layerId, objectId)

        logger.info("User {} deleted vector object {} from layer {}", currentUserId(), objectId, layerId)

        eventPublisher.publishMetadataDeleted(objectId)
        eventPublisher.publishGeometryDeleted(objectId)
    }

    override fun existsByLayerId(layerId: UUID): Boolean {
        repository.findByLayerId(layerId) ?: return false
        return true
    }

    private fun getLayerWithEditAccess(layerId: UUID): Layer {
        val layer = layerRepository.findById(layerId) ?: throw LayerNotFoundException(layerId)
        projectAccessService.assertCanEdit(layer.projectId)
        return layer
    }

    private fun getLayerWithViewAccess(layerId: UUID): Layer {
        val layer = layerRepository.findById(layerId) ?: throw LayerNotFoundException(layerId)
        projectAccessService.assertCanView(layer.projectId)
        return layer
    }

    private fun currentUserId(): UUID = security.currentUserId()
}
