package com.example.jwtdemo.service

import com.example.jwtdemo.dto.SprintRequest
import com.example.jwtdemo.dto.SprintResponseDTO
import com.example.jwtdemo.dto.TaskResponseDTO
import com.example.jwtdemo.model.Project
import com.example.jwtdemo.model.Sprint
import com.example.jwtdemo.persistence.ProjectPersistence
import com.example.jwtdemo.persistence.SprintPersistence
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime


@Service
class SprintServiceImpl(
    private val sprintPersistence: SprintPersistence,
    private val projectPersistence: ProjectPersistence,
) {

    fun createSprint(request: SprintRequest): Sprint {

        if (request.startDate.isAfter(request.endDate)) {
            throw IllegalArgumentException("Start date must be before end date")
        }

        val project = projectPersistence.findById(request.projectId)
            .orElseThrow { RuntimeException("Project not found") }

        val sprint = Sprint(
            name = request.name,
            startDate = request.startDate,
            endDate = request.endDate,
            project = project,
            createdDate = LocalDateTime.now(),
            updatedDate = LocalDateTime.now()
        )

        return sprintPersistence.save(sprint)
    }


    fun getSprintsById(id: Long): SprintResponseDTO? {

        val sprint = sprintPersistence.findById(id).orElse(null)
            ?: return null

        return SprintResponseDTO(
            id = sprint.id,
            name = sprint.name,
            startDate = sprint.startDate,
            endDate = sprint.endDate,
            tasks = sprint.tasks.map { task ->
                TaskResponseDTO(
                    id = task.id,
                    title = task.title,
                    description = task.description,
                    status = task.status,
                    priority = task.priority,
                    dueDate = task.dueDate,
                    startDate = task.startDate,
                    isActive = task.isActive,
                    userId = task.users.id
                )
            }
        )
    }


    fun getAllSprints(): List<Sprint> {
            return sprintPersistence.findAll();
     }

    @Transactional
    fun updateSprint(id: Long, request: SprintRequest): Sprint {

        val existingSprint = sprintPersistence.findById(id)
            .orElseThrow { RuntimeException("Sprint not found") }

        if (request.startDate.isAfter(request.endDate)) {
            throw IllegalArgumentException("Start date must be before end date")
        }

        val project = projectPersistence.findById(request.projectId)
            .orElseThrow { RuntimeException("Project not found") }

        existingSprint.name = request.name
        existingSprint.startDate = request.startDate
        existingSprint.endDate = request.endDate
        existingSprint.project = project
        existingSprint.updatedDate = LocalDateTime.now()

        return sprintPersistence.save(existingSprint)
    }
}