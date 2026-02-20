package com.example.jwtdemo.resource

import com.example.jwtdemo.dto.AuthRequest
import com.example.jwtdemo.dto.AuthResponse
import com.example.jwtdemo.dto.LoginRequest
import com.example.jwtdemo.dto.RegisterResponse
import com.example.jwtdemo.exception.ConflictException
import com.example.jwtdemo.model.Role
import com.example.jwtdemo.model.User
import com.example.jwtdemo.persistence.UserPersistence
import com.example.jwtdemo.service.JwtService
import jakarta.annotation.PostConstruct
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/api/auth")
class AuthResource(
    private val userPersistence: UserPersistence,
    private val jwtService: JwtService,
    private val encoder: PasswordEncoder
) {

    private val log = LoggerFactory.getLogger(AuthResource::class.java)

    @PostConstruct
    fun init() {
        log.info("Initializing default users if database is empty")

        if (userPersistence.count() == 0L) {
            log.info("No users found. Creating default users")

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

            log.info("Default users created successfully")
        }
    }

    @PostMapping("/login")
    fun login(
        @RequestBody request: LoginRequest,
        response: HttpServletResponse
    ): AuthResponse {

        log.info("Login attempt for username: {}", request.username)

        val user = userPersistence.findByUsername(request.username)
            ?: run {
                log.warn("Login failed. User not found: {}", request.username)
                throw RuntimeException("Invalid credentials")
            }

        if (!encoder.matches(request.password, user.password)) {
            log.warn("Login failed. Invalid password for username: {}", request.username)
            throw RuntimeException("Invalid credentials")
        }

        val accessToken = jwtService.generateAccessToken(user.username, user.role.name)
        val refreshToken = jwtService.generateRefreshToken(user.username)

        jwtService.addRefreshTokenCookie(response, refreshToken)

        log.info("Login successful for username: {}", request.username)

        return AuthResponse(
            accessToken = accessToken,
            role = user.role.name
        )
    }

    @PostMapping("/refresh")
    fun refresh(
        request: HttpServletRequest,
        response: HttpServletResponse
    ): AuthResponse {

        log.info("Refresh token request received")

        val refreshToken = request.cookies
            ?.firstOrNull { it.name == "refreshToken" }
            ?.value
            ?: run {
                log.warn("Refresh failed. No refresh token cookie found")
                throw ResponseStatusException(HttpStatus.UNAUTHORIZED)
            }

        val username = jwtService.extractUsername(refreshToken)

        val user = userPersistence.findByUsername(username)
            ?: run {
                log.warn("Refresh failed. User not found: {}", username)
                throw RuntimeException("Invalid credentials")
            }

        val newAccessToken = jwtService.generateAccessToken(user.username, user.role.name)
        val newRefreshToken = jwtService.generateRefreshToken(user.username)

        jwtService.addRefreshTokenCookie(response, newRefreshToken)

        log.info("Token refreshed successfully for username: {}", username)

        return AuthResponse(
            accessToken = newAccessToken,
            role = user.role.name
        )
    }

    @PostMapping("/logout")
    fun logout(response: HttpServletResponse) {

        log.info("Logout request received")

        jwtService.deleteRefreshTokenCookie(response)

        log.info("User logged out successfully (refresh token cookie deleted)")
    }

    @PostMapping("/register")
    fun register(@RequestBody request: AuthRequest): ResponseEntity<RegisterResponse> {

        log.info("Register request received for username: {}", request.username)

        if (userPersistence.existsByUsername(request.username)) {
            log.warn("Registration failed. Username already exists: {}", request.username)
            throw ConflictException("Username already exists")
        }

        val role = when (request.role.replace(" ", "").replace("-", "").uppercase()) {
            "ADMIN" -> Role.ADMIN
            "PROJECTMANAGER" -> Role.PROJECT_MANAGER
            "TEAMMEMBER" -> Role.TEAM_MEMBER
            else -> {
                log.error("Invalid role provided: {}", request.role)
                throw IllegalArgumentException("Invalid role: ${request.role}")
            }
        }

        userPersistence.save(
            User(
                username = request.username,
                password = encoder.encode(request.password),
                role = role,
                email = request.email
            )
        )

        log.info("User registered successfully with username: {}", request.username)

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(RegisterResponse("User registered successfully"))
    }
}