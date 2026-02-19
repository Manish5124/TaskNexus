package com.example.jwtdemo.service

import com.example.jwtdemo.dto.TaskRequest
import com.example.jwtdemo.dto.TaskResponse
import com.example.jwtdemo.exception.NotFoundException
import com.example.jwtdemo.mapper.toResponseDto
import com.example.jwtdemo.model.Status
import com.example.jwtdemo.model.Task
import com.example.jwtdemo.persistence.ProjectPersistence
import com.example.jwtdemo.persistence.SprintPersistence
import com.example.jwtdemo.persistence.TaskPersistence
import com.example.jwtdemo.persistence.UserPersistence
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.LocalDateTime

@Service
open class TaskServiceImpl(
    private val taskPersistence: TaskPersistence,
    private val userPersistence: UserPersistence,
    private val sprintPersistence: SprintPersistence,
    private val projectPersistence: ProjectPersistence
) {

    private val log = LoggerFactory.getLogger(TaskServiceImpl::class.java)

    @Transactional
    open fun createTask(request: TaskRequest): TaskResponse {

        log.info("Creating task with title: {}", request.title)

        // Date validation
        if (request.startDate.isAfter(request.dueDate)) {
            log.error("Invalid date range: startDate {} is after dueDate {}", request.startDate, request.dueDate)
            throw IllegalArgumentException("Start date must be before due date")
        }

        // Fetch User
        val user = userPersistence.findById(request.userId)
            .orElseThrow {
                log.error("User not found with id: {}", request.userId)
                NotFoundException("User not found with id ${request.userId}")
            }

        // Fetch Project
        val project = projectPersistence.findById(request.projectId)
            .orElseThrow {
                log.error("Project not found with id: {}", request.projectId)
                NotFoundException("Project not found with id ${request.projectId}")
            }

        // Fetch Sprint
        val sprint = sprintPersistence.findById(request.sprintId)
            .orElseThrow {
                log.error("Sprint not found with id: {}", request.sprintId)
                NotFoundException("Sprint not found with id ${request.sprintId}")
            }

        val task = Task(
            title = request.title,
            description = request.description,
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

        log.info("Task created successfully with id: {}", savedTask.id)

        return savedTask.toResponse()
    }


    @Transactional(readOnly = true)
    open fun getTaskByUserId(id: Long): List<TaskResponse> {

        log.info("Fetching tasks for user id: {}", id)

        val tasks = taskPersistence.findAllByUsersId(id)

        if (tasks.isEmpty()) {
            log.warn("No tasks found for user id: {}", id)
            throw NotFoundException("No tasks found for user id $id")
        }

        return tasks.map { it.toResponse() }
    }


    @Transactional(readOnly = true)
    open fun getTaskBySprintId(id: Long): List<TaskResponse> {

        log.info("Fetching tasks for sprint id: {}", id)

        val tasks = taskPersistence.findAllBySprintId(id)

        if (tasks.isEmpty()) {
            log.warn("No tasks found for sprint id: {}", id)
            throw NotFoundException("No tasks found for sprint id $id")
        }

        return tasks.map { it.toResponse() }
    }


    private fun Task.toResponse(): TaskResponse {
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

    @Transactional
    open fun updateTask(taskId: Long, request: TaskRequest): TaskResponse {

        log.info("Updating task with id: {}", taskId)

        // Fetch existing task
        val task = taskPersistence.findById(taskId)
            .orElseThrow {
                log.error("Task not found with id: {}", taskId)
                NotFoundException("Task not found with id $taskId")
            }

        // Date validation (if dates are being changed)
        if (request.startDate.isAfter(request.dueDate)) {
            log.error(
                "Invalid date range: startDate {} is after dueDate {}",
                request.startDate,
                request.dueDate
            )
            throw IllegalArgumentException("Start date must be before due date")
        }

        // Cannot move to DONE if existing status is BLOCKED
        if (task.status == Status.BLOCKED &&
            request.status == Status.DONE
        ) {
            log.warn(
                "Invalid status transition BLOCKED -> DONE for task id {}",
                taskId
            )
            throw IllegalStateException(
                "Task cannot be changed to DONE while it is BLOCKED"
            )
        }




        task.priority = request.priority
        task.dueDate = request.dueDate
        task.startDate = request.startDate
        task.status = request.status
        task.isActive = request.isActive
        task.updatedDate = LocalDateTime.now()

        val updatedTask = taskPersistence.save(task)

        log.info("Task updated successfully with id: {}", updatedTask.id)

        return updatedTask.toResponse()
    }

    @Transactional
    fun deleteTask(id: Long) {

        val task = taskPersistence.findById(id)
            .orElseThrow { RuntimeException("Task not found with id $id") }

        if (task.status == Status.COMPLETED) {
            throw IllegalStateException("Completed tasks cannot be deleted")
        }

        taskPersistence.delete(task)
    }

    @Transactional(readOnly = true)
    fun getCompletedTasksPercentageByUserId(id: Long): Double {
        val tasks = taskPersistence.findAllByUsersId(id)
        if (tasks.isEmpty()) return 0.0

        val completed = tasks.count { it.status == Status.DONE }
        return (completed.toDouble() / tasks.size.toDouble()) * 100.0
    }

    @Transactional(readOnly = true)
    fun getOverdueTasksByUserId(id: Long): List<TaskResponse> {
        val tasks = taskPersistence.findAllByUsersId(id)
        if (tasks.isEmpty()) return emptyList()

        val today = LocalDate.now()
        val overdue = tasks.filter { it.dueDate.isBefore(today) && it.status != Status.DONE }

        return overdue.map { it.toResponseDto() }
    }



}






