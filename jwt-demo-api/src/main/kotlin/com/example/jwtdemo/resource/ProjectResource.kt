package com.example.jwtdemo.resource

import com.example.jwtdemo.dto.CreateProjectMemberRequest
import com.example.jwtdemo.dto.ProjectRequest
import com.example.jwtdemo.dto.ProjectResponse
import com.example.jwtdemo.model.UserProject
import com.example.jwtdemo.service.ProjectService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/project")
class ProjectResource(
    private val projectService: ProjectService
) {
    @PostMapping
    fun createProject(@RequestBody request: ProjectRequest): ResponseEntity<String> {
        val response = projectService.createProject(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @GetMapping
    fun getAllProjects(): ResponseEntity<List<ProjectResponse>> {
        val projects = projectService.getAllProjects()
        return ResponseEntity.ok(projects)
    }

    @GetMapping("/{id}")
    fun getProjectById(@PathVariable id: Long): ResponseEntity<ProjectResponse> {
        val project = projectService.getProjectById(id)
        return ResponseEntity.ok(project)
    }

    @DeleteMapping("/{id}")
    fun softDelete(@PathVariable id: Long): ResponseEntity<String> {
        val response = projectService.softDeleteProject(id)
        return ResponseEntity.ok(response)
    }

    @PostMapping("/{id}/members")
    fun addMember(
        @PathVariable id: Long,
        @RequestBody user: CreateProjectMemberRequest
    ): ResponseEntity<String> {
        val response = projectService.createAndAssignMember(id, user)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @DeleteMapping("/{projectId}/member/{userId}")
    fun removeMember(
        @PathVariable projectId: Long,
        @PathVariable userId: Long
    ): ResponseEntity<String> {
        val response = projectService.removeProjectMember(projectId, userId)
        return ResponseEntity.ok(response)
    }

}
