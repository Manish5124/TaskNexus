package com.example.jwtdemo.service

import com.example.jwtdemo.dto.SprintRequest
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


    fun getSprintsById(id: Long): Sprint? {
        return sprintPersistence.findById(id).orElse(null)
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