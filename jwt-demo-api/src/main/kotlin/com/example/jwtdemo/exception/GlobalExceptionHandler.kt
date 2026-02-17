package com.example.jwtdemo.exception

import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.time.Instant

@RestControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler(NotFoundException::class)
    fun notFound(ex: NotFoundException, req: HttpServletRequest) =
        ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ApiError(
                Instant.now(),
                404,
                HttpStatus.NOT_FOUND.toString(),
                ex.message!!,
                req.requestURI
            ))

    @ExceptionHandler(ConflictException::class)
    fun conflict(ex: ConflictException, req: HttpServletRequest) =
        ResponseEntity.status(HttpStatus.CONFLICT)
            .body(ApiError(
                Instant.now(),
                409,
                HttpStatus.CONFLICT.toString(),
                ex.message!!,
                req.requestURI
            ))
}