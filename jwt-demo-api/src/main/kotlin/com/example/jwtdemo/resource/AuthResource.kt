package com.example.jwtdemo.resource

import com.example.jwtdemo.dto.AuthRequest
import com.example.jwtdemo.dto.AuthResponse
import com.example.jwtdemo.dto.RegisterResponse
import com.example.jwtdemo.model.Role
import com.example.jwtdemo.model.User
import com.example.jwtdemo.persistence.UserPersistence
import com.example.jwtdemo.service.JwtService
import jakarta.annotation.PostConstruct
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/api/auth")
class AuthResource(
    private val userPersistence: UserPersistence,
    private val jwtService: JwtService,
    private val encoder: PasswordEncoder
) {

    @PostConstruct
    fun init() {
        if (userPersistence.count() == 0L) {
            userPersistence.save(
                User(
                    username = "admin",
                    password = encoder.encode("admin123"),
                    role = Role.ADMIN,
                    email = "admin@gmail.com"
                )
            )
            userPersistence.save(
                User(
                    username = "teammember",
                    password = encoder.encode("teammember123"),
                    role = Role.TEAM_MEMBER,
                    email = "teammember@gmail.com"
                )
            )
            userPersistence.save(
                User(
                    username = "projectmanager",
                    password = encoder.encode("projectmanager123"),
                    role = Role.PROJECT_MANAGER,
                    email = "projectmanager@gmail.com"
                )
            )
        }
    }

        @PostMapping("/login")
        fun login(@RequestBody request: AuthRequest,
                  response: HttpServletResponse): AuthResponse{
            val user = userPersistence.findByUsername(request.username)
                ?: throw RuntimeException("Invalid credentials")

            if(!encoder.matches(request.password, user.password)){
                throw RuntimeException("Invalid credentials")
            }

            val accessToken = jwtService.generateAccessToken(user.username, user.role.name)
            val refreshToken = jwtService.generateRefreshToken(user.username)

            jwtService.addRefreshTokenCookie(response, refreshToken)

            return AuthResponse(
                accessToken = accessToken,
                role = user.role.name
            )
        }

    @PostMapping("/refresh")
    fun refresh(request: HttpServletRequest,
              response: HttpServletResponse): AuthResponse{

        val refreshToken = request.cookies
            ?.firstOrNull { it.name == "refreshToken"}
            ?.value
            ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED)
        val username = jwtService.extractUsername(refreshToken);

        val user = userPersistence.findByUsername(username)
            ?: throw RuntimeException("Invalid credentials")

        val newAccessToken = jwtService.generateAccessToken(user.username, user.role.name)
        val newRefreshToken = jwtService.generateRefreshToken(user.username)
        jwtService.addRefreshTokenCookie(response, newRefreshToken)
        return AuthResponse(
            accessToken = newAccessToken,
            role = user.role.name
        )
    }

    @PostMapping("/logout")
    fun logout(response: HttpServletResponse){
        jwtService.deleteRefreshTokenCookie(response)
    }

        @PostMapping("/register")
        fun register(@RequestBody request: AuthRequest): ResponseEntity<RegisterResponse>{
            if (userPersistence.existsByUsername(request.username)) {
                throw RuntimeException("Username already exists")
            }
            userPersistence.save(User(
                username = request.username,
                password = encoder.encode(request.password),
                role = if (request.role == Role.ADMIN.name) Role.ADMIN else Role.TEAM_MEMBER,
                email = request.email
                  ))
            return ResponseEntity
                        .status(HttpStatus.CREATED)
                        .body(RegisterResponse("User registered"))
        }
    }