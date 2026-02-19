package com.example.jwtdemo.mapper

import com.example.jwtdemo.dto.TaskResponse
import com.example.jwtdemo.model.Task

fun Task.toResponseDto(): TaskResponse = TaskResponse(
    id = this.id,
    title = this.title,
    description = this.description,
    status = this.status,
    priority = this.priority,
    dueDate = this.dueDate,
    startDate = this.startDate,
    isActive = this.isActive,
    userId = this.users.id,
    projectId = this.project.id,
    sprintId = this.sprint.id,
    createdDate = this.createdDate,
    updatedDate = this.updatedDate
)


