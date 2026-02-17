package com.example.jwtdemo.resource

import com.example.jwtdemo.dto.CreateProjectMemberRequest
import com.example.jwtdemo.dto.ProjectRequest
import com.example.jwtdemo.model.UserProject
import com.example.jwtdemo.service.ProjectService
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
@RequestMapping("/api/admin")
class ProjectResource(
    private val projectService: ProjectService
) {
    @PostMapping
    fun createProject(@RequestBody request: ProjectRequest) =
        projectService.createProject(request)

    @GetMapping
    fun getAllProjects() =
        projectService.getAllProjects()

    @GetMapping("/{id}")
    fun getProjectById(@PathVariable id: Long) =
        projectService.getProjectById(id)

    @DeleteMapping("/{id}")
    fun softDelete(@PathVariable id: Long) =
        projectService.softDeleteProject(id)

    @PostMapping("/{id}/members")
    fun addMember(
        @PathVariable id: Long,
        @RequestBody user: CreateProjectMemberRequest
    ) = projectService.createAndAssignMember(id, user)
}
