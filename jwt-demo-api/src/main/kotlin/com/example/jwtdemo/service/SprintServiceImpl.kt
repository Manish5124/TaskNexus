package com.example.jwtdemo.service

import com.example.jwtdemo.dto.SprintRequest
import com.example.jwtdemo.model.Project
import com.example.jwtdemo.model.Sprint
import com.example.jwtdemo.persistence.ProjectPersistence
import com.example.jwtdemo.persistence.SprintPersistence
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
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

    fun getSprintById()
}