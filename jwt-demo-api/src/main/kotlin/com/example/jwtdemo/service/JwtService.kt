package com.example.jwtdemo.service

import com.example.jwtdemo.dto.UserResponseDTO
import com.example.jwtdemo.model.Role
import com.example.jwtdemo.persistence.UserPersistence
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.time.format.DateTimeFormatter
import java.util.Date

@Service
class JwtService(
    @Value("\${jwt.secret}") secret: String,
    @Value("\${jwt.access.expiration}") val accessExpMs: Long,
    @Value("\${jwt.refresh.expiration}") val refreshExpMs: Long,
    private val userPersistence: UserPersistence
) {

    private val key = Keys.hmacShaKeyFor(secret.toByteArray())

    fun generateAccessToken(username: String, role: String) =
        Jwts.builder()
            .subject(username)
            .claim("role", role)
            .expiration(Date(System.currentTimeMillis() + accessExpMs))
            .signWith(key)
            .compact()

    fun generateRefreshToken(username: String) =
        Jwts.builder()
            .subject(username)
            .expiration(Date(System.currentTimeMillis() + refreshExpMs))
            .signWith(key)
            .compact()

    fun addRefreshTokenCookie(
        response: HttpServletResponse,
        token: String
    ){
        val cookie = Cookie("refreshToken", token)
        cookie.isHttpOnly = true
        cookie.path = "/api/auth"
        cookie.maxAge = refreshExpMs.toInt()
        response.addCookie(cookie)
    }

    fun deleteRefreshTokenCookie(
        response: HttpServletResponse
    ){
        val cookie = Cookie("refreshToken", "")
        cookie.isHttpOnly = true
        cookie.path = "/api/auth"
        cookie.maxAge = 0
        response.addCookie(cookie)
    }

    fun extractUsername(token: String) =
        Jwts.parser().verifyWith(key).build()
            .parseSignedClaims(token)
            .payload.subject


    fun getAllMembersByRole(roleName: String): List<UserResponseDTO> {

        val role = try {
            Role.valueOf(roleName.uppercase())
        } catch (ex: IllegalArgumentException) {
            throw IllegalArgumentException("Invalid role name: $roleName")
        }

        val users = userPersistence.findAllByRole(role)
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

        return users.map {
            UserResponseDTO(
                username = it.username,
                email = it.email,
                createdDate = it.createdDate.format(formatter),
                isActive = it.isActive
            )
        }
    }
}