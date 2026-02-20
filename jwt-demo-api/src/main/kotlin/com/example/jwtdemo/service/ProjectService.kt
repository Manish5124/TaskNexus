package com.example.jwtdemo.service

import com.example.jwtdemo.dto.CreateProjectMemberRequest
import com.example.jwtdemo.dto.ProjectRequest
import com.example.jwtdemo.dto.ProjectResponse
import com.example.jwtdemo.exception.ConflictException
import com.example.jwtdemo.exception.NotFoundException
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
import org.slf4j.LoggerFactory
import org.springframework.security.crypto.password.PasswordEncoder

@Service
open class ProjectService(
    private val projectPersistence: ProjectPersistence,
    private val userPersistence: UserPersistence,
    private val userProjectPersistence: UserProjectPersistence,
    private val encoder: PasswordEncoder
) {

    private val log = LoggerFactory.getLogger(ProjectService::class.java)

    open fun createProject(request: ProjectRequest): String {
        log.info("Creating project with name: {}", request.name)

        val project = projectPersistence.save(request.toEntity())

        log.info("Project created successfully with id: {}", project.id)
        return "Project created successfully"
    }

    open fun getAllProjects(): List<ProjectResponse> {
        log.info("Fetching all active projects")

        val projects = projectPersistence.findAllByIsActiveTrue()

        log.info("Total active projects found: {}", projects.size)

        return projects.map { it.toResponseDto() }
    }

    open fun getProjectById(id: Long): ProjectResponse {
        log.info("Fetching project with id: {}", id)

        val project = projectPersistence.findById(id)
            .orElseThrow {
                log.error("Project not found with id: {}", id)
                NotFoundException("Project not found with id $id")
            }

        log.info("Project found with id: {}", id)
        return project.toResponseDto()
    }

    @Transactional
    open fun softDeleteProject(id: Long): String {
        log.warn("Soft delete initiated for project id: {}", id)

        val project = projectPersistence.findById(id)
            .orElseThrow {
                log.error("Project not found with id: {}", id)
                NotFoundException("Project not found with id $id")
            }

        project.isActive = false

        log.info("Project id {} marked as inactive successfully", id)
        return "Project deleted successfully"
    }

    @Transactional
    open fun createAndAssignMember(
        projectId: Long,
        request: CreateProjectMemberRequest
    ): String {

        log.info("Assigning user [{}] to project [{}]", request.username, projectId)

        val project = projectPersistence.findById(projectId)
            .orElseThrow {
                log.error("Project not found with id: {}", projectId)
                NotFoundException("Project not found with id $projectId")
            }

        val user = userPersistence.findByUsername(request.username)
            ?: run {
                log.info("User not found. Creating new user with username: {}", request.username)

                userPersistence.save(
                    User(
                        username = request.username,
                        password = encoder.encode(request.password), // never log password
                        role = Role.valueOf(request.role.uppercase()),
                        email = request.email
                    )
                )
            }

        log.info("User id [{}] ready for project mapping", user.id)

        val alreadyMapped =
            userProjectPersistence.existsByUsersAndProject(user, project)

        if (alreadyMapped) {
            log.warn(
                "User [{}] is already assigned to project [{}]",
                user.username,
                projectId
            )
            throw ConflictException("User already assigned to this project")
        }

        val userProject = UserProject(
            users = user,
            project = project
        )

        userProjectPersistence.save(userProject)

        log.info(
            "User [{}] successfully assigned to project [{}]",
            user.username,
            projectId
        )

        return "Member assigned to project successfully"
    }

    @Transactional
    open fun removeProjectMember(
        projectId: Long,
        userId: Long
    ): String {

        log.info("Removing user [{}] from project [{}]", userId, projectId)

        val project = projectPersistence.findById(projectId)
            .orElseThrow {
                log.error("Project not found with id: {}", projectId)
                NotFoundException("Project not found with id $projectId")
            }

        val user = userPersistence.findById(userId)
            .orElseThrow {
                log.error("User not found with id: {}", userId)
                NotFoundException("User not found with id $userId")
            }

        val mapping = userProjectPersistence
            .findByUsersAndProject(user, project)
            ?: run {
                log.warn(
                    "User [{}] is not assigned to project [{}]",
                    userId,
                    projectId
                )
                throw ConflictException("User is not assigned to this project")
            }

        userProjectPersistence.delete(mapping)

        log.info(
            "User [{}] successfully removed from project [{}]",
            userId,
            projectId
        )

        return "User removed from project successfully"
    }
}