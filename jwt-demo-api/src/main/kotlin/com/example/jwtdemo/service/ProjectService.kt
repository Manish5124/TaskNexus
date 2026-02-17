package com.example.jwtdemo.service

import com.example.jwtdemo.dto.ProjectRequest
import com.example.jwtdemo.model.Project
import com.example.jwtdemo.model.UserProject
import com.example.jwtdemo.persistence.ProjectPersistence
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service


@Service
class ProjectService(
    private val projectPersistence: ProjectPersistence
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
    fun addMember(projectId: Long, userProject: UserProject) {
        val project = getProjectById(projectId)
        project.userProjects.add(userProject)
    }
}