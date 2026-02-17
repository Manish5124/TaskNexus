package com.example.jwtdemo.service

import com.example.jwtdemo.dto.CreateProjectMemberRequest
import com.example.jwtdemo.dto.ProjectRequest
import com.example.jwtdemo.dto.ProjectResponse
import com.example.jwtdemo.model.Role
import com.example.jwtdemo.model.User
import com.example.jwtdemo.model.UserProject
import com.example.jwtdemo.persistence.ProjectPersistence
import com.example.jwtdemo.persistence.UserPersistence
import com.example.jwtdemo.persistence.UserProjectPersistence
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import com.example.jwtdemo.mapper.toEntity
import com.example.jwtdemo.mapper.toResponseDto
import org.springframework.security.crypto.password.PasswordEncoder

@Service
open class ProjectService(
    private val projectPersistence: ProjectPersistence,
    private val userPersistence: UserPersistence,
    private val userProjectPersistence: UserProjectPersistence,
    private val encoder: PasswordEncoder
) {

     open fun createProject(request: ProjectRequest): String {
        projectPersistence.save(request.toEntity())
        return "Project created successfully"
    }

    open fun getAllProjects(): List<ProjectResponse> {
        return projectPersistence.findAllByIsActiveTrue()
            .map { it.toResponseDto() }
    }

    open fun getProjectById(id: Long): ProjectResponse {
        val project = projectPersistence.findById(id)
            .orElseThrow { RuntimeException("Project not found with id $id") }

        return project.toResponseDto()
    }

    @Transactional
    open fun softDeleteProject(id: Long): String {
        val project = projectPersistence.findById(id)
            .orElseThrow { RuntimeException("Project not found") }

        project.isActive = false
        return "Project deleted successfully"
    }

    @Transactional
    open fun createAndAssignMember(
        projectId: Long,
        request: CreateProjectMemberRequest
    ): String {

        val project = projectPersistence.findById(projectId)
            .orElseThrow { RuntimeException("Project not found") }

        val user = User(
            username = request.username,
            password = encoder.encode(request.password),
            role = Role.valueOf(request.role.uppercase()),
            email = request.email,
        )

        val savedUser = userPersistence.save(user)

        val userProject = UserProject(
            users = savedUser,
            project = project
        )

        userProjectPersistence.save(userProject)

        return "Member created and assigned to project successfully"
    }
}