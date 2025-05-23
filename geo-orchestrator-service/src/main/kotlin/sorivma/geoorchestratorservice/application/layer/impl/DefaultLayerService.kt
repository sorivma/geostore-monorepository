package sorivma.geoorchestratorservice.application.layer.impl

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import sorivma.geoorchestratorservice.application.layer.LayerService
import sorivma.geoorchestratorservice.application.layer.RasterLayerDataService
import sorivma.geoorchestratorservice.application.layer.VectorLayerDataService
import sorivma.geoorchestratorservice.application.layer.dto.CreateLayerDto
import sorivma.geoorchestratorservice.application.layer.dto.LayerResponse
import sorivma.geoorchestratorservice.application.layer.dto.UpdateLayerDto
import sorivma.geoorchestratorservice.application.project.ProjectAccessService
import sorivma.geoorchestratorservice.domain.model.layer.Layer
import sorivma.geoorchestratorservice.domain.model.layer.LayerType
import sorivma.geoorchestratorservice.infrastructure.persistence.layer.repository.LayerRepository
import sorivma.geoorchestratorservice.shared.security.SecurityContextHolderFacade
import java.util.*

@Service
class DefaultLayerService(
    private val layerRepo: LayerRepository,
    private val projectAccess: ProjectAccessService,
    private val vectorService: VectorLayerDataService,
    private val rasterService: RasterLayerDataService,
    private val security: SecurityContextHolderFacade
) : LayerService {

    private val logger = LoggerFactory.getLogger(DefaultLayerService::class.java)

    override fun create(request: CreateLayerDto): UUID {
        val userId = currentUserId()

        projectAccess.assertCanView(request.projectId)

        val layerId = UUID.randomUUID()
        val layer = Layer(
            id = layerId,
            projectId = request.projectId,
            name = request.name,
            type = request.type,
            order = request.order,
            geometryType = request.geometryType,
        )

        layerRepo.save(layer)

        logger.info("User {} created new layer {} in project {}", userId, layerId, request.projectId)

        return layerId
    }

    override fun update(layerId: UUID, request: UpdateLayerDto) {
        val existingLayer = layerRepo.findById(layerId) ?: error("Layer not found: $layerId")
        val userId = currentUserId()

        projectAccess.assertCanView(existingLayer.projectId)

        val updatedLayer = existingLayer.copy(
            name = request.name,
            order = request.order
        )

        layerRepo.save(updatedLayer)

        logger.info("User {} updated layer {} in project {}", userId, layerId, existingLayer.projectId)
    }

    override fun getById(layerId: UUID): LayerResponse {
        val layer = layerRepo.findById(layerId) ?: error("Layer not found: $layerId")
        projectAccess.assertCanView(layer.projectId)

        logger.info("User {} accessed layer {} from project {}", currentUserId(), layer.id, layer.projectId)

        return layer.toResponse()
    }

    override fun getByProjectId(projectId: UUID): List<LayerResponse> {
        projectAccess.assertCanView(projectId)

        logger.info("User {} fetched layers for project {}", currentUserId(), projectId)

        return layerRepo.findByProject(projectId).map { it.toResponse() }
    }

    @Transactional
    override fun deleteById(layerId: UUID) {
        val layer = layerRepo.findById(layerId) ?: error("Layer not found: $layerId")
        projectAccess.assertCanEdit(layer.projectId)

        when (layer.type) {
            LayerType.VECTOR -> vectorService.deleteByLayerId(layer.id)
            LayerType.RASTER -> rasterService.deleteByLayerId(layer.id)
        }

        layerRepo.delete(layer.id)

        logger.info("User {} deleted layer {} from project {}", currentUserId(), layer.id, layer.projectId)
    }

    private fun currentUserId(): UUID = security.currentUserId()

    private fun Layer.toResponse() = LayerResponse(
        id = id,
        projectId = projectId,
        name = name,
        type = type,
        order = order
    )
}