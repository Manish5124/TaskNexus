package com.example.jwtdemo.dto

import java.time.LocalDate

data class UpdateSprintRequest(
    val id: Long? = null,
    val name: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
)
