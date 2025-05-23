package sorivma.geoorchestratorservice.shared.exception

import java.util.*

class ProjectAccessDeniedException : RuntimeException("Access denied")
class DuplicateMemberException(userId: UUID) : RuntimeException("User $userId already a member")
class MemberNotFoundException(userId: UUID) : RuntimeException("User $userId not found")
class OwnerCannotBeRemovedException : RuntimeException("Cannot remove project owner")
class IllegalRoleException : RuntimeException("Role OWNER cannot be assigned")
class ProjectNotFoundException(projectId: UUID) : RuntimeException("Project $projectId does not exist")