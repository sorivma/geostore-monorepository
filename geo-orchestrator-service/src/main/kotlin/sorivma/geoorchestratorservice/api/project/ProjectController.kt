package sorivma.geoorchestratorservice.api.project

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import sorivma.geoorchestratorservice.api.project.request.CreateProjectRequest
import sorivma.geoorchestratorservice.api.project.response.ProjectCreatedResponse
import sorivma.geoorchestratorservice.api.project.response.ProjectResponse
import sorivma.geoorchestratorservice.application.project.ProjectService
import sorivma.geoorchestratorservice.application.project.command.CreateProjectCommand
import java.util.*

@RestController
@RequestMapping("/projects")
class ProjectController(
    private val service: ProjectService
) {

    @PostMapping
    fun create(@RequestBody request: CreateProjectRequest): ResponseEntity<ProjectCreatedResponse> {
        val command = CreateProjectCommand(
            name = request.name,
            description = request.description
        )
        val id = service.create(command)
        return ResponseEntity.status(HttpStatus.CREATED).body(ProjectCreatedResponse(id))
    }

    @GetMapping("/{id}")
    fun getById(@PathVariable id: UUID): ResponseEntity<ProjectResponse> {
        val project = service.getById(id)
        return ResponseEntity.ok(ProjectResponse.fromDomain(project))
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: UUID): ResponseEntity<Void> {
        service.delete(id)
        return ResponseEntity.noContent().build()
    }

    @GetMapping("/owned")
    fun getOwned(): ResponseEntity<List<ProjectResponse>> {
        val projects = service.getOwnedBy()
        return ResponseEntity.ok(projects.map(ProjectResponse::fromDomain))
    }

    @GetMapping("/visible")
    fun getVisible(): ResponseEntity<List<ProjectResponse>> {
        val projects = service.getVisibleTo()
        return ResponseEntity.ok(projects.map(ProjectResponse::fromDomain))
    }
}