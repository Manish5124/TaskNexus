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
open class TaskService(
    private val taskPersistence: TaskPersistence,
    private val userPersistence: UserPersistence,
    private val sprintPersistence: SprintPersistence,
    private val projectPersistence: ProjectPersistence
) {

    private val log = LoggerFactory.getLogger(TaskService::class.java)

    @Transactional
    open fun createTask(request: TaskRequest): TaskResponse {

        log.info("Creating task with title: {}", request.title)

        if (request.startDate.isAfter(request.dueDate)) {
            log.error("Invalid date range: startDate {} is after dueDate {}", request.startDate, request.dueDate)
            throw IllegalArgumentException("Start date must be before due date")
        }

        val user = userPersistence.findById(request.userId)
            .orElseThrow {
                log.error("User not found with id: {}", request.userId)
                NotFoundException("User not found with id ${request.userId}")
            }

        val project = projectPersistence.findById(request.projectId)
            .orElseThrow {
                log.error("Project not found with id: {}", request.projectId)
                NotFoundException("Project not found with id ${request.projectId}")
            }

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

        return savedTask.toResponseDto()
    }

    @Transactional(readOnly = true)
    open fun getTaskByUserId(id: Long): List<TaskResponse> {

        log.info("Fetching tasks for user id: {}", id)

        val tasks = taskPersistence.findAllByUsersId(id)

        if (tasks.isEmpty()) {
            log.warn("No tasks found for user id: {}", id)
            throw NotFoundException("No tasks found for user id $id")
        }

        log.info("Found {} tasks for user id: {}", tasks.size, id)

        return tasks.map { it.toResponseDto() }
    }

    @Transactional(readOnly = true)
    open fun getTaskBySprintId(id: Long): List<TaskResponse> {

        log.info("Fetching tasks for sprint id: {}", id)

        val tasks = taskPersistence.findAllBySprintId(id)

        if (tasks.isEmpty()) {
            log.warn("No tasks found for sprint id: {}", id)
            throw NotFoundException("No tasks found for sprint id $id")
        }

        log.info("Found {} tasks for sprint id: {}", tasks.size, id)

        return tasks.map { it.toResponseDto() }
    }

    @Transactional
    open fun updateTask(taskId: Long, request: TaskRequest): TaskResponse {

        log.info("Updating task with id: {}", taskId)

        val task = taskPersistence.findById(taskId)
            .orElseThrow {
                log.error("Task not found with id: {}", taskId)
                NotFoundException("Task not found with id $taskId")
            }

        if (request.startDate.isAfter(request.dueDate)) {
            log.error("Invalid date range: startDate {} is after dueDate {}", request.startDate, request.dueDate)
            throw IllegalArgumentException("Start date must be before due date")
        }

        if (task.status == Status.BLOCKED && request.status == Status.DONE) {
            log.warn("Invalid status transition BLOCKED -> DONE for task id {}", taskId)
            throw IllegalStateException("Task cannot be changed to DONE while it is BLOCKED")
        }

        task.priority = request.priority
        task.dueDate = request.dueDate
        task.startDate = request.startDate
        task.status = request.status
        task.isActive = request.isActive
        task.updatedDate = LocalDateTime.now()

        val updatedTask = taskPersistence.save(task)

        log.info("Task updated successfully with id: {}", updatedTask.id)

        return updatedTask.toResponseDto()
    }

    @Transactional
    fun deleteTask(id: Long) {

        log.info("Deleting task with id: {}", id)

        val task = taskPersistence.findById(id)
            .orElseThrow {
                log.error("Task not found with id: {}", id)
                RuntimeException("Task not found with id $id")
            }

        if (task.status == Status.COMPLETED) {
            log.warn("Attempt to delete completed task with id: {}", id)
            throw IllegalStateException("Completed tasks cannot be deleted")
        }

        taskPersistence.delete(task)

        log.info("Task deleted successfully with id: {}", id)
    }

    @Transactional(readOnly = true)
    fun getCompletedTasksPercentageByUserId(id: Long): Double {

        log.info("Calculating completed task percentage for user id: {}", id)

        val tasks = taskPersistence.findAllByUsersId(id)

        if (tasks.isEmpty()) {
            log.warn("No tasks found for user id: {}", id)
            return 0.0
        }

        val completed = tasks.count { it.status == Status.DONE }
        val percentage = (completed.toDouble() / tasks.size.toDouble()) * 100.0

        log.info("User id {} has {}% completed tasks", id, percentage)

        return percentage
    }

    @Transactional(readOnly = true)
    fun getOverdueTasksByUserId(id: Long): List<TaskResponse> {

        log.info("Fetching overdue tasks for user id: {}", id)

        val tasks = taskPersistence.findAllByUsersId(id)

        if (tasks.isEmpty()) {
            log.warn("No tasks found for user id: {}", id)
            return emptyList()
        }

        val today = LocalDate.now()
        val overdue = tasks.filter {
            it.dueDate.isBefore(today) && it.status != Status.DONE
        }

        log.info("Found {} overdue tasks for user id: {}", overdue.size, id)

        return overdue.map { it.toResponseDto() }
    }
}





