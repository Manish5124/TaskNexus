package com.example.jwtdemo.config

import com.example.jwtdemo.service.CustomUserDetailsService
import com.example.jwtdemo.service.JwtService
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthFilter(
    private val jwtService: JwtService,
    private val userDetailsService: CustomUserDetailsService
): OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val authHeader = request.getHeader("Authorization")

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response)
            return
        }

        try {
            val token = authHeader.substring(7)

            // This will throw exception if token is invalid
            val username = jwtService.extractUsername(token)

            if (username != null &&
                SecurityContextHolder.getContext().authentication == null
            ) {

                val userDetails =
                    userDetailsService.loadUserByUsername(username)

                val authentication = UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.authorities
                )

                SecurityContextHolder.getContext().authentication =
                    authentication
            }

        } catch (ex: Exception) {
            // If token invalid or expired → ignore
            SecurityContextHolder.clearContext()
        }

        filterChain.doFilter(request, response)
    }
}