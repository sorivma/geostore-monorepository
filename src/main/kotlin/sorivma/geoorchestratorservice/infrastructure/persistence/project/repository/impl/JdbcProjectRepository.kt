package sorivma.geoorchestratorservice.infrastructure.persistence.project.repository.impl

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository
import sorivma.geoorchestratorservice.domain.model.project.Project
import sorivma.geoorchestratorservice.domain.model.project.ProjectMember
import sorivma.geoorchestratorservice.domain.model.project.ProjectRole
import sorivma.geoorchestratorservice.infrastructure.persistence.project.repository.ProjectRepository
import java.util.*

@Repository
class JdbcProjectRepository(
    private val jdbc: NamedParameterJdbcTemplate
) : ProjectRepository {
    override fun save(project: Project): Project {
        val upsertProjectSql = """
            INSERT INTO projects (id, name, description, owner_id)
            VALUES (:id, :name, :description, :ownerId)
            ON CONFLICT (id) DO UPDATE SET
                name = EXCLUDED.name,
                description = EXCLUDED.description,
                owner_id = EXCLUDED.owner_id
        """.trimIndent()

        jdbc.update(
            upsertProjectSql,
            mapOf(
                "id" to project.id,
                "name" to project.name,
                "description" to project.description,
                "ownerId" to project.ownerId
            )
        )

        val deleteMembersSql = "DELETE FROM project_members WHERE project_id = :projectId"
        jdbc.update(deleteMembersSql, mapOf("projectId" to project.id))

        val insertMemberSql = """
            INSERT INTO project_members (project_id, user_id, role)
            VALUES (:projectId, :userId, :role)
        """.trimIndent()

        project.members.forEach { member ->
            jdbc.update(
                insertMemberSql,
                mapOf(
                    "projectId" to project.id,
                    "userId" to member.userId,
                    "role" to member.role.name
                )
            )
        }

        return project
    }

    override fun findById(id: UUID): Project? {
        val projectSql = "SELECT * FROM projects WHERE id = :id"
        val projects = jdbc.query(projectSql, mapOf("id" to id)) { rs, _ ->
            Project(
                id = UUID.fromString(rs.getString("id")),
                name = rs.getString("name"),
                description = rs.getString("description"),
                ownerId = UUID.fromString(rs.getString("owner_id")),
                members = mutableListOf()
            )
        }

        if (projects.isEmpty()) return null
        val project = projects.first()

        val members = findMembersForProject(project.id)
        return project.copy(members = members.toMutableList())
    }

    override fun deleteById(id: UUID) {
        val sql = "DELETE FROM projects WHERE id = :id"
        jdbc.update(sql, mapOf("id" to id))
    }

    override fun existsById(id: UUID): Boolean {
        val sql = "SELECT COUNT(*) FROM projects WHERE id = :id"
        return jdbc.queryForObject(sql, mapOf("id" to id), Long::class.java)!! > 0
    }

    override fun findByOwnerId(ownerId: UUID): List<Project> {
        val sql = "SELECT * FROM projects WHERE owner_id = :ownerId"
        val projects = jdbc.query(sql, mapOf("ownerId" to ownerId)) { rs, _ ->
            Project(
                id = UUID.fromString(rs.getString("id")),
                name = rs.getString("name"),
                description = rs.getString("description"),
                ownerId = UUID.fromString(rs.getString("owner_id")),
                members = mutableListOf()
            )
        }
        return mapWithMembers(projects)
    }

    override fun findAllVisibleTo(userId: UUID): List<Project> {
        val sql = """
            SELECT DISTINCT p.*
            FROM projects p
            LEFT JOIN project_members m ON p.id = m.project_id
            WHERE p.owner_id = :userId OR m.user_id = :userId
        """.trimIndent()

        val projects = jdbc.query(sql, mapOf("userId" to userId)) { rs, _ ->
            Project(
                id = UUID.fromString(rs.getString("id")),
                name = rs.getString("name"),
                description = rs.getString("description"),
                ownerId = UUID.fromString(rs.getString("owner_id")),
                members = mutableListOf()
            )
        }

        return mapWithMembers(projects)
    }

    private fun findMembersForProject(projectId: UUID): List<ProjectMember> {
        val sql = "SELECT user_id, role FROM project_members WHERE project_id = :projectId"
        return jdbc.query(sql, mapOf("projectId" to projectId)) { rs, _ ->
            ProjectMember(
                userId = UUID.fromString(rs.getString("user_id")),
                role = ProjectRole.valueOf(rs.getString("role"))
            )
        }
    }

    private fun mapWithMembers(projects: List<Project>): List<Project> {
        if (projects.isEmpty()) return emptyList()

        val ids = projects.map { it.id }
        val sql = """
            SELECT project_id, user_id, role
            FROM project_members
            WHERE project_id IN (:ids)
        """.trimIndent()

        val members = jdbc.query(sql, mapOf("ids" to ids)) { rs, _ ->
            val projectId = UUID.fromString(rs.getString("project_id"))
            val userId = UUID.fromString(rs.getString("user_id"))
            val role = ProjectRole.valueOf(rs.getString("role"))
            projectId to ProjectMember(userId, role)
        }

        val memberMap: Map<UUID, List<ProjectMember>> = members.groupBy({ it.first }, { it.second })

        return projects.map { p ->
            p.copy(members = memberMap[p.id]?.toMutableList() ?: mutableListOf())
        }
    }
}