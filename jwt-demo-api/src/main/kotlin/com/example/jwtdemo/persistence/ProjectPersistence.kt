package com.example.jwtdemo.persistence

import com.example.jwtdemo.model.Project
import com.example.jwtdemo.model.User
import org.springframework.data.jpa.repository.JpaRepository

interface ProjectPersistence: JpaRepository<Project, Long> {
}