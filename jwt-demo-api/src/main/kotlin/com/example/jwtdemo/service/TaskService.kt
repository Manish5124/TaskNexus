package com.example.jwtdemo.service

import com.example.jwtdemo.model.Task
import com.example.jwtdemo.persistence.TaskPersistence
import org.springframework.stereotype.Service

@Service
class TaskService(
    private val TaskPersistence: TaskPersistence,
) {


}