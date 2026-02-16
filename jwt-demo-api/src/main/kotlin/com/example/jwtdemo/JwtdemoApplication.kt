package com.example.jwtdemo

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class JwtdemoApplication

fun main(args: Array<String>) {
	runApplication<JwtdemoApplication>(*args)
}
