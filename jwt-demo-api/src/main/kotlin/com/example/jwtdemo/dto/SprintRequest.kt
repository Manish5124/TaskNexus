package com.example.jwtdemo.dto

import jakarta.validation.constraints.NotBlank
import java.time.LocalDate

data class SprintRequest(
    val name: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val projectId: Long
)
