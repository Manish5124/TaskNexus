package com.example.jwtdemo.dto

import com.example.jwtdemo.model.Priority
import com.example.jwtdemo.model.Status
import java.time.LocalDate
import java.time.LocalDateTime

data class TaskRequest(
    val title: String,

    val description: String,

    val status: Status,

    val priority: Priority,

    val dueDate: LocalDate,

    val startDate: LocalDate,

    val isActive: Boolean = true,

    val userId: Long,

    val projectId: Long,

    val sprintId: Long,

)
