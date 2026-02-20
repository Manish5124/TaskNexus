package com.example.jwtdemo.mapper

import com.example.jwtdemo.dto.SprintRequest
import com.example.jwtdemo.dto.SprintResponseDTO
import com.example.jwtdemo.model.Project
import com.example.jwtdemo.model.Sprint


fun SprintRequest.toEntity(project: Project): Sprint =
    Sprint(
        name = this.name,
        startDate = this.startDate,
        endDate = this.endDate,
        project = project
    )


fun Sprint.toResponseDto(): SprintResponseDTO  =
    SprintResponseDTO(
        id = this.id,
        name = this.name,
        startDate = this.startDate,
        endDate = this.endDate,
        tasks = this.tasks.map { it.toResponseDto() }, // assuming Task mapper exists
    )

// List Mapper
fun List<Sprint>.toResponseDtos(): List<SprintResponseDTO> =
    map { it.toResponseDto() }
