package com.example.jwtdemo.dto

data class ProjectResponse(
    val id: Long,
    val name: String,
    val description: String,
    val isActive: Boolean
)
