package com.example.jwtdemo.service

import com.example.jwtdemo.persistence.UserPersistence
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class CustomUserDetailsService(
    private val userPersistence: UserPersistence
) : UserDetailsService{
    override fun loadUserByUsername(username: String): UserDetails {
        val user = userPersistence.findByUsername(username) ?:
                    throw UsernameNotFoundException("User not found")
        return User(
                user.username,
                user.password,
                listOf(SimpleGrantedAuthority("ROLE_${user.role}"))
            )
    }
}