package com.example.jwtdemo.mapper

import com.example.jwtdemo.dto.ProjectRequest
import com.example.jwtdemo.dto.ProjectResponse
import com.example.jwtdemo.model.Project

class ProjectMapper {

    fun ProjectRequest.toEntity(): Project = Project(
        name = this.name,
        description = this.description
    )

    fun Project.toResponseDto(): ProjectResponse = ProjectResponse(
        id = this.id,
        name = this.name,
        description = this.description,
        isActive = this.isActive
    )

    fun List<Project>.toResponseDtos(): List<ProjectResponse> = map { it.toResponseDto() }



}