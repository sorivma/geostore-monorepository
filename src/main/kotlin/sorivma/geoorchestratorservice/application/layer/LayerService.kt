package sorivma.geoorchestratorservice.application.layer

import sorivma.geoorchestratorservice.application.layer.dto.CreateLayerDto
import sorivma.geoorchestratorservice.application.layer.dto.LayerResponse
import sorivma.geoorchestratorservice.application.layer.dto.UpdateLayerDto
import java.util.*

interface LayerService {
    fun create(request: CreateLayerDto): UUID
    fun update(layerId: UUID, request: UpdateLayerDto)
    fun getById(layerId: UUID): LayerResponse
    fun getByProjectId(projectId: UUID): List<LayerResponse>
    fun deleteById(layerId: UUID)
}