package sorivma.geoorchestratorservice.application.layer.impl

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import sorivma.geoorchestratorservice.application.layer.RasterLayerDataService
import sorivma.geoorchestratorservice.application.layer.dto.RasterLayerDataDto
import sorivma.geoorchestratorservice.application.project.ProjectAccessService
import sorivma.geoorchestratorservice.domain.model.layer.Layer
import sorivma.geoorchestratorservice.domain.model.layer.RasterLayerData
import sorivma.geoorchestratorservice.infrastructure.persistence.layer.repository.LayerRepository
import sorivma.geoorchestratorservice.infrastructure.persistence.layer.repository.RasterLayerDataRepository
import sorivma.geoorchestratorservice.shared.exception.LayerNotFoundException
import sorivma.geoorchestratorservice.shared.security.SecurityContextHolderFacade
import java.util.*

@Service
class DefaultRasterLayerDataService(
    private val repository: RasterLayerDataRepository,
    private val layerRepository: LayerRepository,
    private val projectAccessService: ProjectAccessService,
    private val security: SecurityContextHolderFacade
) : RasterLayerDataService {

    private val logger = LoggerFactory.getLogger(DefaultRasterLayerDataService::class.java)

    override fun save(layerId: UUID, data: RasterLayerDataDto) {
        getLayerWithEditAccess(layerId)

        val entity = RasterLayerData(
            layerId = layerId,
            tileJsonUrl = data.tileJsonUrl,
            cogUrl = data.cogUrl,
            attribution = data.attribution,
            style = RasterLayerData.RasterStyle(
                opacity = data.opacity
            )
        )

        repository.save(entity)

        logger.info("User {} saved raster data for layer {}", currentUserId(), layerId)
    }

    override fun getByLayerId(layerId: UUID): RasterLayerDataDto {
        getLayerWithViewAccess(layerId)

        val data = repository.findByLayerId(layerId)
            ?: throw IllegalStateException("No raster data found for layer $layerId")

        return RasterLayerDataDto(
            tileJsonUrl = data.tileJsonUrl,
            cogUrl = data.cogUrl,
            attribution = data.attribution,
            opacity = data.style.opacity
        )
    }

    override fun deleteByLayerId(layerId: UUID) {
        getLayerWithEditAccess(layerId)

        repository.delete(layerId)

        logger.info("User {} deleted raster data from layer {}", currentUserId(), layerId)
    }

    private fun getLayerWithEditAccess(layerId: UUID): Layer {
        val layer = layerRepository.findById(layerId)
            ?: throw LayerNotFoundException(layerId)

        projectAccessService.assertCanView(layer.projectId)
        return layer
    }

    private fun getLayerWithViewAccess(layerId: UUID): Layer {
        val layer = layerRepository.findById(layerId)
            ?: throw LayerNotFoundException(layerId)

        projectAccessService.assertCanView(layer.projectId)
        return layer
    }

    private fun currentUserId(): UUID = security.currentUserId()
}