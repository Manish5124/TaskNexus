package com.example.jwtdemo.resource

import com.example.jwtdemo.dto.ApiResponse
import com.example.jwtdemo.dto.TaskRequest
import com.example.jwtdemo.dto.TaskResponse
import com.example.jwtdemo.service.TaskService
import com.example.jwtdemo.service.TaskServiceImpl
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/admin")
class TaskResouce(
    private val taskService: TaskServiceImpl
) {

    @PostMapping("/createTask")
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

     @GetMapping("/getTasksByUser/{id}")
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
}