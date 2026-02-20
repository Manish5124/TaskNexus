package com.example.jwtdemo.resource

import com.example.jwtdemo.dto.ApiResponse
import com.example.jwtdemo.dto.TaskRequest
import com.example.jwtdemo.dto.TaskResponse
import com.example.jwtdemo.service.TaskService
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/task")
class TaskResouce(
    private val taskService: TaskService
) {

    private val log = LoggerFactory.getLogger(TaskResouce::class.java)

    @PostMapping("/createtask")
    fun createTask(@RequestBody request: TaskRequest): ResponseEntity<ApiResponse<TaskResponse>> {

        log.info("Create task request received. Title: {}", request.title)

        val response = taskService.createTask(request)

        log.info("Task created successfully with id: {}", response.id)

        val apiResponse = ApiResponse(
            status = 201,
            success = true,
            message = "Task created successfully",
            path = "",
            data = response
        )
        return ResponseEntity.status(201).body(apiResponse)
    }

    @GetMapping("/tasksbyuser/{id}")
    fun getTaskByUserId(@PathVariable id: Long): ResponseEntity<ApiResponse<List<TaskResponse>>> {

        log.info("Fetching tasks for user id: {}", id)

        val response = taskService.getTaskByUserId(id)

        log.info("Total tasks fetched for user {}: {}", id, response.size)

        val apiResponse = ApiResponse(
            status = HttpStatus.OK.value(),
            success = true,
            message = "Tasks fetched successfully",
            path = "",
            data = response
        )

        return ResponseEntity.ok(apiResponse)
    }

    @GetMapping("/tasksbysprintid/{id}")
    fun getTaskBySprintId(@PathVariable id: Long): ResponseEntity<ApiResponse<List<TaskResponse>>> {

        log.info("Fetching tasks for sprint id: {}", id)

        val response = taskService.getTaskBySprintId(id)

        log.info("Total tasks fetched for sprint {}: {}", id, response.size)

        val apiResponse = ApiResponse(
            status = HttpStatus.OK.value(),
            success = true,
            message = "Tasks fetched successfully",
            path = "",
            data = response
        )

        return ResponseEntity.ok(apiResponse)
    }

    @PutMapping("updatetask/{id}")
    fun updateTask(
        @PathVariable id: Long,
        @RequestBody request: TaskRequest
    ): ResponseEntity<ApiResponse<TaskResponse>> {

        log.info("Update task request received for id: {}", id)

        val response = taskService.updateTask(id, request)

        log.info("Task updated successfully with id: {}", id)

        val apiResponse = ApiResponse(
            status = 200,
            success = true,
            message = "Task updated successfully",
            path = "",
            data = response
        )

        return ResponseEntity.ok(apiResponse)
    }

    @DeleteMapping("/deletetask/{id}")
    fun deleteTask(@PathVariable id: Long): ResponseEntity<ApiResponse<Nothing>> {

        log.warn("Delete task request received for id: {}", id)

        taskService.deleteTask(id)

        log.info("Task deleted successfully with id: {}", id)

        val apiResponse = ApiResponse<Nothing>(
            status = 200,
            success = true,
            message = "Task deleted successfully",
            path = "",
            data = null
        )

        return ResponseEntity.ok(apiResponse)
    }

    @GetMapping("/getCompletedTasksPercentage/{id}")
    fun getCompletedTasksPercentage(@PathVariable id: Long): ResponseEntity<ApiResponse<Double>> {

        log.info("Fetching completed task percentage for user id: {}", id)

        val percentage = taskService.getCompletedTasksPercentageByUserId(id)

        log.info("Completed task percentage for user {}: {}", id, percentage)

        val apiResponse = ApiResponse(
            status = HttpStatus.OK.value(),
            success = true,
            message = "Completed tasks percentage fetched successfully",
            path = "",
            data = percentage
        )

        return ResponseEntity.ok(apiResponse)
    }

    @GetMapping("/getOverdueTasksByUser/{id}")
    fun getOverdueTasksByUser(@PathVariable id: Long): ResponseEntity<ApiResponse<List<TaskResponse>>> {

        log.info("Fetching overdue tasks for user id: {}", id)

        val response = taskService.getOverdueTasksByUserId(id)

        log.info("Total overdue tasks for user {}: {}", id, response.size)

        val apiResponse = ApiResponse(
            status = HttpStatus.OK.value(),
            success = true,
            message = "Overdue tasks fetched successfully",
            path = "",
            data = response
        )

        return ResponseEntity.ok(apiResponse)
    }
}