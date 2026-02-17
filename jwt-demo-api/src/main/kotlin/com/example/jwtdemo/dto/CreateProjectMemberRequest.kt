package com.example.jwtdemo.dto

import com.example.jwtdemo.model.Role

data class CreateProjectMemberRequest(
    val username: String,
    val password: String,
    val email: String,
    val role: String
)
