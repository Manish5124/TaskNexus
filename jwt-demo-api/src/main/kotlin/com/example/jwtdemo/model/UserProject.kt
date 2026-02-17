package com.example.jwtdemo.model

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "user_project")
class UserProject(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long=0,

    @ManyToOne
    @JoinColumn(name = "users_id")
    @JsonIgnore
    val users: User,

    @ManyToOne
    @JoinColumn(name = "project_id")
    val project: Project,

    val createdDate: LocalDateTime = LocalDateTime.now(),

    val updatedDate: LocalDateTime = LocalDateTime.now()
)