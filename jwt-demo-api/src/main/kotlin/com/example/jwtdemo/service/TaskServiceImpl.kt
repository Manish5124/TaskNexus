package com.example.jwtdemo.service

import com.example.jwtdemo.dto.TaskRequest
import com.example.jwtdemo.dto.TaskResponse
import com.example.jwtdemo.model.Task
import com.example.jwtdemo.persistence.ProjectPersistence
import com.example.jwtdemo.persistence.SprintPersistence
import com.example.jwtdemo.persistence.TaskPersistence
import com.example.jwtdemo.persistence.UserPersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class TaskServiceImpl(private val taskPersistence : TaskPersistence,
                      private val userPersistence: UserPersistence,
                      private val sprintPersistence: SprintPersistence,
                      private val projectPersistence: ProjectPersistence,) {



    @Transactional
    fun createTask(request: TaskRequest): TaskResponse {

        //  Date validation
        if (request.startDate.isAfter(request.dueDate)) {
            throw IllegalArgumentException("Start date must be before due date")
        }

        //  Fetch related entities
        val user = userPersistence.findById(request.userId)
            .orElseThrow { RuntimeException("User not found with id ${request.userId}") }

        val project = projectPersistence.findById(request.projectId)
            .orElseThrow { RuntimeException("Project not found with id ${request.projectId}") }

        val sprint = sprintPersistence.findById(request.sprintId)
            .orElseThrow { RuntimeException("Sprint not found with id ${request.sprintId}") }

        //  Create Task entity
        val task = Task(
            title = request.title,
            description = request.description,
           // status = request.status,
            priority = request.priority,
            dueDate = request.dueDate,
            startDate = request.startDate,
            isActive = request.isActive,
            users = user,
            project = project,
            sprint = sprint,
            createdDate = LocalDateTime.now(),
            updatedDate = LocalDateTime.now()
        )


        val savedTask = taskPersistence.save(task)
        return savedTask.toResponse()
    }

    fun Task.toResponse(): TaskResponse {
        return TaskResponse(
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
    }


    @Transactional(readOnly = true)
    fun getTaskByUserId(id : Long): List<TaskResponse> {

        val tasks = taskPersistence.findAllByUsersId(id)

        if (tasks.isEmpty()) {
            throw RuntimeException("No tasks found for user id $id")
        }

        return tasks.map { it.toResponse() }
    }
}