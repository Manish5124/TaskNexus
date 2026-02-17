package com.example.jwtdemo.resource

import com.example.jwtdemo.dto.SprintRequest
import com.example.jwtdemo.model.Sprint
import com.example.jwtdemo.service.SprintServiceImpl
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
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

}