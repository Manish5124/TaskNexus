package com.example.jwtdemo.persistence

import com.example.jwtdemo.model.Sprint
import com.example.jwtdemo.model.User
import org.springframework.data.jpa.repository.JpaRepository

interface SprintPersistence:JpaRepository<Sprint, Long> {
}