package sorivma.geoorchestratorservice.infrastructure.persistence.project.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.MappedCollection
import org.springframework.data.relational.core.mapping.Table
import java.util.*

@Table("projects")
data class ProjectEntity(
    @Id val id: UUID,
    val name: String,
    val description: String?,
    val ownerId: UUID,
)
