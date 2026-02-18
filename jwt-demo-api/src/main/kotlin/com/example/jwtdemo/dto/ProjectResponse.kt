package com.example.jwtdemo.dto

import java.time.LocalDateTime

data class ProjectResponse(
    val id: Long,
    val name: String,
    val description: String,
    val isActive: Boolean,
    val createdDate: LocalDateTime
)
