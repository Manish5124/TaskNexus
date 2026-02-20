package com.example.jwtdemo.dto

import java.time.LocalDate

data class SprintResponseDTO(
    val id: Long,
    val name: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val tasks: List<TaskResponse>
)
