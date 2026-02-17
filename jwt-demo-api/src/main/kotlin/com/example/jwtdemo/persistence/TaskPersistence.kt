package com.example.jwtdemo.persistence

import com.example.jwtdemo.model.Task
import com.example.jwtdemo.model.User
import org.springframework.data.jpa.repository.JpaRepository

interface TaskPersistence:JpaRepository<Task, Long> {
}