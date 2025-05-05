package sorivma.geoorchestratorservice.infrastructure.persistence.project.entity

import org.springframework.data.relational.core.mapping.Table
import java.util.*

@Table("project_members")
data class MemberEntity(
    val userId: UUID,
    val projectId: UUID,
    val role: String
)