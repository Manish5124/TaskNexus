package com.example.jwtdemo.resource

import com.example.jwtdemo.dto.CreateProjectMemberRequest
import com.example.jwtdemo.dto.ProjectRequest
import com.example.jwtdemo.dto.ProjectResponse
import com.example.jwtdemo.service.ProjectService
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/project")
class ProjectResource(
    private val projectService: ProjectService
) {

    private val logger = LoggerFactory.getLogger(ProjectResource::class.java)

    @PostMapping
    fun createProject(@RequestBody request: ProjectRequest): ResponseEntity<String> {
        logger.info("Create project request received. Project name: {}", request.name)

        val response = projectService.createProject(request)

        logger.info("Project created successfully. Project name: {}", request.name)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @GetMapping
    fun getAllProjects(): ResponseEntity<List<ProjectResponse>> {
        logger.info("Fetching all projects")

        val projects = projectService.getAllProjects()

        logger.info("Total projects fetched: {}", projects.size)
        return ResponseEntity.ok(projects)
    }

    @GetMapping("/{id}")
    fun getProjectById(@PathVariable id: Long): ResponseEntity<ProjectResponse> {
        logger.info("Fetching project with ID: {}", id)

        val project = projectService.getProjectById(id)

        logger.info("Project fetched successfully with ID: {}", id)
        return ResponseEntity.ok(project)
    }

    @DeleteMapping("/{id}")
    fun softDelete(@PathVariable id: Long): ResponseEntity<String> {
        logger.warn("Soft delete requested for project ID: {}", id)

        val response = projectService.softDeleteProject(id)

        logger.info("Project soft deleted successfully. ID: {}", id)
        return ResponseEntity.ok(response)
    }

    @PostMapping("/{id}/members")
    fun addMember(
        @PathVariable id: Long,
        @RequestBody user: CreateProjectMemberRequest
    ): ResponseEntity<String> {
        logger.info("Adding member to project ID: {} with username: {}", id, user.username)

        val response = projectService.createAndAssignMember(id, user)

        logger.info("Member added successfully to project ID: {}", id)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @DeleteMapping("/{projectId}/member/{userId}")
    fun removeMember(
        @PathVariable projectId: Long,
        @PathVariable userId: Long
    ): ResponseEntity<String> {
        logger.warn("Removing member {} from project {}", userId, projectId)

        val response = projectService.removeProjectMember(projectId, userId)

        logger.info("Member {} removed from project {}", userId, projectId)
        return ResponseEntity.ok(response)
    }
}