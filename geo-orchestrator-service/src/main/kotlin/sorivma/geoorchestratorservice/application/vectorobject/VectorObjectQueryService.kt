package sorivma.geoorchestratorservice.application.vectorobject

import sorivma.geoorchestratorservice.application.vectorobject.dto.BboxDto
import sorivma.geoorchestratorservice.application.vectorobject.dto.VectorObjectDto
import java.util.UUID

interface VectorObjectQueryService {
    fun getAll(
        layerId: UUID,
        includeMetadata: Boolean,
        format: String,
        srid: Int,
    ): List<VectorObjectDto>

    fun getByBbox(
        layerId: UUID,
        bbox: BboxDto,
        srid: Int,
        includeMetadata: Boolean,
        format: String
    ): List<VectorObjectDto>

    fun getByObjectId(
        layerId: UUID,
        objectId: UUID,
        includeMetadata: Boolean,
        format: String,
        srid: Int,
    ): VectorObjectDto
}