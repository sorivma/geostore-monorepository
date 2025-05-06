package sorivma.geoorchestratorservice.application.rasterobject.impl

import com.fasterxml.jackson.databind.JsonNode
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import sorivma.geoorchestratorservice.api.`object`.request.RasterUploadResponse
import sorivma.geoorchestratorservice.application.layer.RasterLayerDataService
import sorivma.geoorchestratorservice.application.layer.dto.RasterLayerDataDto
import sorivma.geoorchestratorservice.application.rasterobject.RasterLayerService
import sorivma.geoorchestratorservice.config.renderserver.RenderServerProperties
import sorivma.geoorchestratorservice.domain.model.layer.LayerType
import sorivma.geoorchestratorservice.domain.model.layer.RasterLayerData
import sorivma.geoorchestratorservice.infrastructure.objectstore.RasterStorageClient
import sorivma.geoorchestratorservice.infrastructure.persistence.layer.repository.LayerRepository
import sorivma.geoorchestratorservice.infrastructure.query.TitilerClient
import sorivma.geoorchestratorservice.shared.exception.LayerNotFoundException
import sorivma.geoorchestratorservice.shared.exception.WrongLayerTypeException
import java.io.InputStream
import java.util.*

@Service
class DefaultRasterLayerService(
    private val layerRepository: LayerRepository,
    private val rasterLayerDataService: RasterLayerDataService,
    private val titilerClient: TitilerClient,
    private val rasterStorageClient: RasterStorageClient,
    private val renderServerProperties: RenderServerProperties
): RasterLayerService {
    override fun uploadFromStream(layerId: UUID, inputStream: InputStream): RasterUploadResponse {
        val layer = layerRepository.findById(layerId)
            ?: throw LayerNotFoundException(layerId)

        if (layer.type != LayerType.RASTER) {
            throw WrongLayerTypeException(layerId, LayerType.RASTER, layer.type)
        }

        val cogUrl = rasterStorageClient.upload(layer.projectId, layer.id, inputStream)
        val tileJsonUri = buildTileJsonUri(cogUrl)

        rasterLayerDataService.save(
            layerId = layer.id,
            data = RasterLayerDataDto(
                tileJsonUrl = buildTileJsonUri(cogUrl),
                cogUrl = cogUrl,
                attribution = null,
                opacity = RasterLayerData.RasterStyle().opacity,
            )
        )

        return RasterUploadResponse(
            layerId = layer.id,
            cogUrl = cogUrl,
            tileJsonUrl = tileJsonUri,
        )
    }

    override fun getTileJson(layerId: UUID): JsonNode {
        val data = rasterLayerDataService.getByLayerId(layerId)

        return titilerClient.getTileJson(renderServerProperties.tileMatrixSetId, "s3://${data.cogUrl}")
    }

    override fun getData(layerId: UUID): RasterLayerDataDto {
        return rasterLayerDataService.getByLayerId(layerId)
    }

    @Transactional
    override fun delete(layerId: UUID) {
        val layer = layerRepository.findById(layerId)
            ?: throw LayerNotFoundException(layerId)

        rasterLayerDataService.deleteByLayerId(layerId)
        rasterStorageClient.delete(layer.projectId, layer.id)
    }

    fun buildTileJsonUri(cogUrl: String): String {
        return "${renderServerProperties.baseUrl}/cog/${renderServerProperties.tileMatrixSetId}/tilejson.json?url=s3://$cogUrl"
    }
}