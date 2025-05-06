package sorivma.geoorchestratorservice.application.importing

import java.io.InputStream
import java.util.*

interface ImportFile {
    fun import(layerId: UUID, inputStream: InputStream)
}