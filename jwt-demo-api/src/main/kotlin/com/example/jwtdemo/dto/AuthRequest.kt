package com.example.jwtdemo.dto

import com.example.jwtdemo.model.Role

data class AuthRequest(val username: String,
                       val password: String,
                       val email: String,
                       val role: String)
