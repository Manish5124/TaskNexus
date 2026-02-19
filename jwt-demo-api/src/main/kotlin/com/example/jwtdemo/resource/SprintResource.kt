package com.example.jwtdemo.resource

import com.example.jwtdemo.dto.SprintRequest
import com.example.jwtdemo.dto.SprintResponseDTO
import com.example.jwtdemo.mapper.toResponseDtos
import com.example.jwtdemo.model.Sprint
import com.example.jwtdemo.service.SprintServiceImpl
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/admin")
class SprintResource(
    private val sprintServiceImpl: SprintServiceImpl
) {

    @PostMapping("/createSprint")
    fun createSprint(
        @RequestBody sprintRequest: SprintRequest
    ): ResponseEntity<String> {

         sprintServiceImpl.createSprint(sprintRequest)
        return ResponseEntity.ok("Sprints saved successfully")
    }

    @GetMapping("/getSprintById/{id}")
    fun getSprintById(@PathVariable id: Long): ResponseEntity<SprintResponseDTO> {

        val sprint = sprintServiceImpl.getSprintsById(id)

        return sprint?.let {
            ResponseEntity.ok(it)
        } ?: ResponseEntity.notFound().build()
    }

    @PutMapping("/updateSprintById/{id}")
    fun updateSprint(
        @PathVariable id: Long,
        @RequestBody request: SprintRequest
    ): Sprint {
        return sprintServiceImpl.updateSprint(id, request)
    }

    @GetMapping("/getAllSprints")
    fun getAllSprints(): List<SprintResponseDTO> {
            return sprintServiceImpl.getAllSprints().toResponseDtos()
    }
}