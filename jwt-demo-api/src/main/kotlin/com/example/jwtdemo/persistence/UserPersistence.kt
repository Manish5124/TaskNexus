package com.example.jwtdemo.persistence

import com.example.jwtdemo.model.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserPersistence: JpaRepository<User, Long>{
    fun findByUsername(username: String): User?
    fun existsByUsername(username: String): Boolean
}