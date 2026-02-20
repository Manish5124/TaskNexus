package com.example.jwtdemo.resource

import com.example.jwtdemo.dto.SprintRequest
import com.example.jwtdemo.dto.SprintResponseDTO
import com.example.jwtdemo.mapper.toResponseDto
import com.example.jwtdemo.mapper.toResponseDtos
import com.example.jwtdemo.service.SprintService
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/sprint")
class SprintResource(
    private val sprintServiceImpl: SprintService
) {

    private val log = LoggerFactory.getLogger(SprintResource::class.java)

    @PostMapping("/createSprint")
    fun createSprint(
        @RequestBody sprintRequest: SprintRequest
    ): ResponseEntity<String> {

        log.info("Create sprint request received. Sprint name: {}", sprintRequest.name)

        sprintServiceImpl.createSprint(sprintRequest)

        log.info("Sprint created successfully. Sprint name: {}", sprintRequest.name)

        return ResponseEntity.ok("Sprints saved successfully")
    }

    @GetMapping("/getSprintById/{id}")
    fun getSprintById(@PathVariable id: Long): ResponseEntity<SprintResponseDTO> {

        log.info("Fetching sprint with id: {}", id)

        val sprint = sprintServiceImpl.getSprintsById(id)

        return sprint?.let {
            log.info("Sprint found with id: {}", id)
            ResponseEntity.ok(it)
        } ?: run {
            log.warn("Sprint not found with id: {}", id)
            ResponseEntity.notFound().build()
        }
    }

    @PutMapping("/updateSprintById/{id}")
    fun updateSprint(
        @PathVariable id: Long,
        @RequestBody request: SprintRequest
    ): SprintResponseDTO {

        log.info("Update sprint request received for id: {}", id)

        val updatedSprint = sprintServiceImpl.updateSprint(id, request)

        log.info("Sprint updated successfully for id: {}", id)

        return updatedSprint.toResponseDto()
    }

    @GetMapping("/getAllSprints")
    fun getAllSprints(): List<SprintResponseDTO> {

        log.info("Fetching all sprints")

        val sprints = sprintServiceImpl.getAllSprints()

        log.info("Total sprints fetched: {}", sprints.size)

        return sprints.toResponseDtos()
    }

    @GetMapping("/getSprintSizeByProjectId/{projectId}")
    fun getSprintSizeByProjectId(@PathVariable projectId: Long): ResponseEntity<Long> {

        log.info("Fetching sprint size for project id: {}", projectId)

        val sprintCount = sprintServiceImpl.getSprintSizeByProjectId(projectId)

        log.info("Sprint count for project id {}: {}", projectId, sprintCount)

        return ResponseEntity.ok(sprintCount)
    }

}