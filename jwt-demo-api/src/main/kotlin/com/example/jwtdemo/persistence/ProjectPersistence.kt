package com.example.jwtdemo.persistence

import com.example.jwtdemo.model.Project
import org.springframework.data.jpa.repository.JpaRepository

interface ProjectPersistence: JpaRepository<Project, Long> {
    fun findAllByIsActiveTrue(): List<Project>
}