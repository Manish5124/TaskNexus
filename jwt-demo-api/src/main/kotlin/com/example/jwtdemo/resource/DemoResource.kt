package com.example.jwtdemo.resource

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
class DemoResource {
    @GetMapping("/user/hello")
    fun user() = mapOf( "message" to "Hello User")

    @GetMapping("/admin/hello")
    fun admin()= mapOf( "message" to "Hello Admin")
}