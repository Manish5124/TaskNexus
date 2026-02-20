package com.example.jwtdemo.resource

import com.example.jwtdemo.dto.ApiResponse
import com.example.jwtdemo.dto.TaskRequest
import com.example.jwtdemo.dto.TaskResponse
import com.example.jwtdemo.service.TaskService
import com.example.jwtdemo.service.TaskServiceImpl
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/task")
class TaskResouce(
    private val taskService: TaskServiceImpl
) {

    @PostMapping("/createtask")
    fun createTask(@RequestBody request: TaskRequest): ResponseEntity<ApiResponse<TaskResponse>> {

        val response = taskService.createTask(request)
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
             val response = taskService.getTaskByUserId(id)

         val Apiresponse = ApiResponse(
             status = HttpStatus.OK.value(),
             success = true,
             message = "Tasks fetched successfully",
             path = "",
             data = response
         )

         return ResponseEntity.ok(Apiresponse)
     }


    @GetMapping("/tasksbysprintid/{id}")
    fun getTaskBySprintId(@PathVariable id: Long): ResponseEntity<ApiResponse<List<TaskResponse>>> {
        val response = taskService.getTaskBySprintId(id)

        val Apiresponse = ApiResponse(
            status = HttpStatus.OK.value(),
            success = true,
            message = "Tasks fetched successfully",
            path = "",
            data = response
        )

        return ResponseEntity.ok(Apiresponse)
    }

    @PutMapping("updatetask/{id}")
    fun updateTask(
        @PathVariable id: Long,
        @RequestBody request: TaskRequest
    ): ResponseEntity<ApiResponse<TaskResponse>> {

        val response = taskService.updateTask(id, request)

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

        taskService.deleteTask(id)

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
        val percentage = taskService.getCompletedTasksPercentageByUserId(id)

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
        val response = taskService.getOverdueTasksByUserId(id)
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