package sorivma.geoorchestratorservice.api.importing

import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import sorivma.geoorchestratorservice.application.importing.ImportFile
import sorivma.geoorchestratorservice.shared.exception.FileTooBigException
import java.util.*

@RestController
@RequestMapping("vector/layer/{layerId}/import")
class GeoJsonImportController(
    private val geoJsonImportService: ImportFile,
) {
    @PostMapping(
        "/geojson",
        consumes = [MediaType.MULTIPART_FORM_DATA_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    @ResponseStatus(HttpStatus.ACCEPTED)
    fun importGeoJson(
        @PathVariable layerId: UUID,
        @RequestPart("file") file: MultipartFile
    ): String {
        if (file.size > MAX_UPLOAD_SIZE_BYTES) {
            throw FileTooBigException(file.size, MAX_UPLOAD_SIZE_BYTES)
        }

        geoJsonImportService.import(
            layerId,
            file.inputStream,
        )

        return "Import started"
    }

    companion object {
        private const val MAX_UPLOAD_SIZE_MB = 50
        private const val MAX_UPLOAD_SIZE_BYTES = MAX_UPLOAD_SIZE_MB * 1024 * 1024L
    }
}