package sorivma.geoorchestratorservice.infrastructure.objectstore

import java.io.InputStream
import java.util.*

interface RasterStorageClient {
    fun upload(projectId: UUID, layerId: UUID, inputStream: InputStream): String
    fun delete(projectId: UUID, layerId: UUID)
}