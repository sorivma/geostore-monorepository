package sorivma.geoorchestratorservice.infrastructure.objectstore.impl

import io.minio.MinioClient
import io.minio.PutObjectArgs
import io.minio.RemoveObjectArgs
import org.springframework.stereotype.Component
import sorivma.geoorchestratorservice.config.minio.MinioProps
import sorivma.geoorchestratorservice.infrastructure.objectstore.RasterStorageClient
import java.io.InputStream
import java.util.*

@Component
class MinioStorageClient(
    private val minioClient: MinioClient,
    private val props: MinioProps,
) : RasterStorageClient {
    override fun upload(projectId: UUID, layerId: UUID, inputStream: InputStream): String {
        val objectName = objectName(projectId, layerId)

        val arguments = PutObjectArgs.builder()
            .bucket(props.bucket)
            .`object`(objectName)
            .stream(inputStream, -1, PART_SIZE)
            .contentType("image/tiff")
            .build()

        minioClient.putObject(arguments)

        return "${props.bucket}/$objectName"
    }

    override fun delete(projectId: UUID, layerId: UUID) {
        val objectName = objectName(projectId, layerId)

        val arguments = RemoveObjectArgs.builder()
            .bucket(props.bucket)
            .`object`(objectName)
            .build()

        minioClient.removeObject(arguments)
    }

    private fun objectName(projectId: UUID, layerId: UUID): String =
        "project-$projectId/layer-$layerId/layer.tif"

    companion object {
        private const val PART_SIZE = 10 * 1024 * 1024L // 10 MB примерно
    }
}