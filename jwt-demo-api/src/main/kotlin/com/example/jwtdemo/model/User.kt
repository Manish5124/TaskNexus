package com.example.jwtdemo.model

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "users")
class User(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    val username: String,

    val password: String,

    @Column(unique = true)
    val email: String,

    @Enumerated(EnumType.STRING)
    val role: Role,

    val isActive: Boolean = true,

    @OneToMany(mappedBy = "users", cascade = [CascadeType.ALL])
    @JsonIgnore
    val tasks: List<Task> = listOf(),

    @OneToMany(mappedBy = "users", cascade = [CascadeType.ALL])
    @JsonIgnore
    val userProjects: List<UserProject> = mutableListOf(),

    val createdDate: LocalDateTime = LocalDateTime.now(),

    val updatedDate: LocalDateTime = LocalDateTime.now()
)