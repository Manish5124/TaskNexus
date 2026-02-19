package com.example.jwtdemo.mapper

import com.example.jwtdemo.dto.TaskRequest
import com.example.jwtdemo.dto.TaskResponse
import com.example.jwtdemo.model.Project
import com.example.jwtdemo.model.Sprint
import com.example.jwtdemo.model.Task
import com.example.jwtdemo.model.User
import java.time.LocalDateTime

// =============================
// Request → Entity
// =============================
fun TaskRequest.toEntity(
    user: User,
    project: Project,
    sprint: Sprint
): Task =
    Task(
        title = this.title,
        description = this.description,
        status = this.status,
        priority = this.priority,
        dueDate = this.dueDate,
        startDate = this.startDate,
        isActive = this.isActive,
        users = user,          // ✅ match entity field name
        project = project,
        sprint = sprint,
        createdDate = LocalDateTime.now(),
        updatedDate = LocalDateTime.now()
    )


// =============================
// Entity → Response
// =============================
fun Task.toResponseDto(): TaskResponse =
    TaskResponse(
        id = this.id,
        title = this.title,
        description = this.description,
        status = this.status,
        priority = this.priority,
        dueDate = this.dueDate,
        startDate = this.startDate,
        isActive = this.isActive,
        userId = this.users.id,        // ✅ users instead of user
        projectId = this.project.id,
        sprintId = this.sprint.id,
        createdDate = this.createdDate,
        updatedDate = this.updatedDate
    )


// =============================
// List Mapper
// =============================
fun List<Task>.toResponseDtos(): List<TaskResponse> =
    map { it.toResponseDto() }
