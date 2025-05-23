package sorivma.geoorchestratorservice.infrastructure.persistence.project.mapper

import sorivma.geoorchestratorservice.domain.model.project.Project
import sorivma.geoorchestratorservice.domain.model.project.ProjectMember
import sorivma.geoorchestratorservice.domain.model.project.ProjectRole
import sorivma.geoorchestratorservice.infrastructure.persistence.project.entity.MemberEntity
import sorivma.geoorchestratorservice.infrastructure.persistence.project.entity.ProjectEntity
import java.util.UUID

fun ProjectEntity.toDomain(members: List<MemberEntity>): Project =
    Project(
        id = this.id,
        name = this.name,
        description = this.description,
        ownerId = this.ownerId,
        members = members.map { it.toDomain() }.toMutableList()
    )

fun MemberEntity.toDomain(): ProjectMember =
    ProjectMember(userId = this.userId, role = ProjectRole.valueOf(this.role))

fun Project.toEntity(): ProjectEntity =
    ProjectEntity(
        id = this.id,
        name = this.name,
        description = this.description,
        ownerId = this.ownerId,
    )

fun ProjectMember.toEntity(projectId: UUID): MemberEntity =
    MemberEntity(userId = this.userId, role = this.role.name, projectId = projectId)