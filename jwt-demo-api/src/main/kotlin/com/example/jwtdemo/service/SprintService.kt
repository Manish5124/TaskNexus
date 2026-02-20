package com.example.jwtdemo.service

import com.example.jwtdemo.dto.SprintRequest
import com.example.jwtdemo.dto.SprintResponseDTO
import com.example.jwtdemo.dto.TaskResponse
import com.example.jwtdemo.dto.UpdateSprintRequest
import com.example.jwtdemo.exception.ConflictException
import com.example.jwtdemo.exception.NotFoundException
import com.example.jwtdemo.model.Sprint
import com.example.jwtdemo.persistence.ProjectPersistence
import com.example.jwtdemo.persistence.SprintPersistence
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class SprintService(
    private val sprintPersistence: SprintPersistence,
    private val projectPersistence: ProjectPersistence,
) {

    private val log = LoggerFactory.getLogger(SprintService::class.java)

    fun createSprint(request: SprintRequest): Sprint {

        log.info(
            "Creating sprint '{}' for project id {}",
            request.name,
            request.projectId
        )

        if (request.startDate.isAfter(request.endDate)) {
            log.warn(
                "Invalid sprint dates. Start: {}, End: {}",
                request.startDate,
                request.endDate
            )
            throw ConflictException("Start date must be before end date")
        }

        val project = projectPersistence.findById(request.projectId)
            .orElseThrow {
                log.error("Project not found with id {}", request.projectId)
                NotFoundException("Project not found with id ${request.projectId}")
            }

        val overlappingSprints = sprintPersistence.findOverlappingSprints(
            projectId = request.projectId,
            startDate = request.startDate,
            endDate = request.endDate
        )

        if (overlappingSprints.isNotEmpty()) {
            log.warn(
                "Sprint date overlap detected for project id {}",
                request.projectId
            )
            throw ConflictException("Sprint dates overlap with an existing sprint")
        }

        val sprint = Sprint(
            name = request.name,
            startDate = request.startDate,
            endDate = request.endDate,
            project = project,
            createdDate = LocalDateTime.now(),
            updatedDate = LocalDateTime.now()
        )

        val savedSprint = sprintPersistence.save(sprint)

        log.info(
            "Sprint '{}' created successfully with id {}",
            savedSprint.name,
            savedSprint.id
        )

        return savedSprint
    }

    fun getSprintsById(id: Long): SprintResponseDTO? {

        log.info("Fetching sprint with id {}", id)

        val sprint = sprintPersistence.findById(id).orElse(null)
            ?: run {
                log.warn("Sprint not found with id {}", id)
                return null
            }

        log.info("Sprint found with id {}. Total tasks: {}", id, sprint.tasks.size)

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

    fun getSprintSizeByProjectId(id: Long ): Long {
        log.info("Fetching sprint size for project id {}", id)

        val sprintCount = sprintPersistence.countByProjectId(id)

        log.info("Total sprints found for project id {}: {}", id, sprintCount)

        return sprintCount

    }

    fun getAllSprints(): List<Sprint> {

        log.info("Fetching all sprints")

        val sprints = sprintPersistence.findAll()

        log.info("Total sprints fetched: {}", sprints.size)

        return sprints
    }

    @Transactional
    fun updateSprint(id: Long, request: UpdateSprintRequest): Sprint {

        log.info("Updating sprint with id {}", id)

        val existingSprint = sprintPersistence.findById(id)
            .orElseThrow {
                log.error("Sprint not found with id {}", id)
                RuntimeException("Sprint not found")
            }

        if (request.startDate.isAfter(request.endDate)) {
            log.warn(
                "Invalid update dates for sprint id {}. Start: {}, End: {}",
                id,
                request.startDate,
                request.endDate
            )
            throw IllegalArgumentException("Start date must be before end date")
        }

        existingSprint.name = request.name
        existingSprint.startDate = request.startDate
        existingSprint.endDate = request.endDate
        existingSprint.updatedDate = LocalDateTime.now()

        val updatedSprint = sprintPersistence.save(existingSprint)

        log.info("Sprint id {} updated successfully", id)

        return updatedSprint
    }
}