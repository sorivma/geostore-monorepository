package sorivma.geoorchestratorservice.domain.model.project

import sorivma.geoorchestratorservice.shared.exception.*
import java.util.*

data class Project(
    val id: UUID,
    val name: String,
    val description: String?,
    val ownerId: UUID,
    val members: MutableList<ProjectMember> = mutableListOf(ProjectMember(ownerId, ProjectRole.OWNER)),
) {
    fun addMember(userId: UUID, role: ProjectRole) {
        if (role == ProjectRole.OWNER) throw IllegalRoleException()
        if (members.any { it.userId == userId }) throw DuplicateMemberException(userId)

        members.add(ProjectMember(userId, role))
    }

    fun removeMember(userId: UUID) {
        if (userId == ownerId) throw OwnerCannotBeRemovedException()
        members.removeIf { it.userId == userId }
    }

    fun updateMemberRole(userId: UUID, newRole: ProjectRole) {
        if (newRole == ProjectRole.EDITOR) throw IllegalRoleException()
        val index = members.indexOfFirst { it.userId == userId }

        if (index == -1) throw MemberNotFoundException(UUID.randomUUID())

        members[index] = ProjectMember(userId, newRole)
    }

    fun canEdit(userId: UUID): Boolean =
        userId == ownerId || members.any { it.userId == userId && it.role == ProjectRole.EDITOR }

    fun canView(userId: UUID): Boolean =
        userId == ownerId || members.any { it.userId == userId }

    fun assertEditAccess(userId: UUID) {
        if (!canEdit(userId)) throw ProjectAccessDeniedException()
    }

    fun assertViewAccess(userId: UUID) {
        if (!canView(userId)) throw ProjectAccessDeniedException()
    }
}
