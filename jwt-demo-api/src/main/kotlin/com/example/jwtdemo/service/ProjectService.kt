package com.example.jwtdemo.service

import com.example.jwtdemo.dto.CreateProjectMemberRequest
import com.example.jwtdemo.dto.ProjectRequest
import com.example.jwtdemo.dto.ProjectResponse
import com.example.jwtdemo.model.Project
import com.example.jwtdemo.model.Role
import com.example.jwtdemo.model.User
import com.example.jwtdemo.model.UserProject
import com.example.jwtdemo.persistence.ProjectPersistence
import com.example.jwtdemo.persistence.UserPersistence
import com.example.jwtdemo.persistence.UserProjectPersistence
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service


@Service
class ProjectService(
    private val projectPersistence: ProjectPersistence,
    private val userPersistence: UserPersistence,
    private val userProjectPersistence: UserProjectPersistence
) {

//    1. Create Project
    fun createProject(request: ProjectRequest): Project {
        val project = Project(
            id = 0,
            name = request.name,
            description = request.description
        )
        return projectPersistence.save(project)
    }

    // 2. Get All Projects
    fun getAllProjects(): List<Project> {
        return projectPersistence.findAllByIsActiveTrue()
    }

    // 3. Get Project By ID
    fun getProjectById(id: Long): Project {
        return projectPersistence.findById(id)
            .orElseThrow { RuntimeException("Project not found with id $id") }
    }

    // 4. Soft Delete (ADMIN)
    @Transactional
    fun softDeleteProject(id: Long) {
        val project = getProjectById(id)
        project.isActive = false
    }

    // 6. Add Member to Project
    @Transactional
    fun createAndAssignMember(
        projectId: Long,
        request: CreateProjectMemberRequest
    ) {

        val project = projectPersistence.findById(projectId)
            .orElseThrow { RuntimeException("Project not found") }

        // 1️⃣ Create User
        val user = User(
            username = request.username,
            password = request.password,
            role = Role.valueOf(request.role.uppercase()),
            email = request.email,
        )

        val savedUser = userPersistence.save(user)

        // 2️⃣ Assign to Project
        val userProject = UserProject(
            users = savedUser,
            project = project
        )

        userProjectPersistence.save(userProject)
    }
}