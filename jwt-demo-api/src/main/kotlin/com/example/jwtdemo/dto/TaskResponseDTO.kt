package com.example.jwtdemo.dto

import com.example.jwtdemo.model.Priority
import com.example.jwtdemo.model.Status
import java.time.LocalDate

data class TaskResponseDTO(
    val id: Long,
    val title: String,
    val description: String,
    val status: Status,
    val priority: Priority,
    val dueDate: LocalDate,
    val startDate: LocalDate,
    val isActive: Boolean,
    val userId: Long
)
