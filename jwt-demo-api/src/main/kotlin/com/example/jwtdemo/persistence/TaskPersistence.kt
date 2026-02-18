package com.example.jwtdemo.persistence

import com.example.jwtdemo.model.Task
import org.springframework.data.jpa.repository.JpaRepository

interface TaskPersistence:JpaRepository<Task, Long> {

    fun findAllByUsersId(userId: Long): List<Task>
}