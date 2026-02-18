package com.example.jwtdemo.dto

import java.time.LocalDateTime

data class ApiResponse<T>(
    val timestamp: LocalDateTime = LocalDateTime.now(),
    val status: Int,
    val success: Boolean,
    val message: String,
    val path: String,
    val data:  T? =  null
)
