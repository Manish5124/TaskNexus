package com.example.jwtdemo.persistence

import com.example.jwtdemo.model.Sprint
import org.springframework.data.jpa.repository.JpaRepository

interface SprintPersistence:JpaRepository<Sprint, Long> {
}