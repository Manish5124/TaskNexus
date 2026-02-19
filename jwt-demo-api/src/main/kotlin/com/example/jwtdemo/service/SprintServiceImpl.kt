package com.example.jwtdemo.service

import com.example.jwtdemo.dto.SprintRequest
import com.example.jwtdemo.dto.SprintResponseDTO
import com.example.jwtdemo.dto.TaskResponse
import com.example.jwtdemo.exception.ConflictException
import com.example.jwtdemo.exception.NotFoundException
//import com.example.jwtdemo.dto.TaskResponseDTO
import com.example.jwtdemo.model.Sprint
import com.example.jwtdemo.persistence.ProjectPersistence
import com.example.jwtdemo.persistence.SprintPersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime


@Service
class SprintServiceImpl(
    private val sprintPersistence: SprintPersistence,
    private val projectPersistence: ProjectPersistence,
) {

    fun createSprint(request: SprintRequest): Sprint {

        // Validate date order
        if (request.startDate.isAfter(request.endDate)) {
            throw ConflictException("Start date must be before end date")
        }

        //  Fetch project
        val project = projectPersistence.findById(request.projectId)
            .orElseThrow { NotFoundException("Project not found with id ${request.projectId}") }

        // Check overlapping sprints inside same project
        val overlappingSprints = sprintPersistence.findOverlappingSprints(
            projectId = request.projectId,
            startDate = request.startDate,
            endDate = request.endDate
        )

        if (overlappingSprints.isNotEmpty()) {
            throw ConflictException("Sprint dates overlap with an existing sprint")
        }

        //  Create sprint
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
                TaskResponse(
                    id = task.id,
                    title = task.title,
                    description = task.description,
                    status = task.status,
                    priority = task.priority,
                    dueDate = task.dueDate,
                    startDate = task.startDate,
                    isActive = task.isActive,
                    userId = task.users.id,
                    projectId = task.project.id,
                    sprintId = task.sprint.id,
                    createdDate = task.createdDate,
                    updatedDate = task.updatedDate
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