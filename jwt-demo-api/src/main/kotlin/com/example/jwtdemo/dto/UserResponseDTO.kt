package com.example.jwtdemo.dto

import org.springframework.data.annotation.CreatedDate

data class UserResponseDTO(
    val username: String,
    val email: String,
    val createdDate: String,
    val isActive: Boolean,
)
