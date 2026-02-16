package com.example.jwtdemo.model

import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.time.LocalDate
import java.time.LocalDateTime

@Entity
@Table(name = "task")
class Task(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    val title: String,

    val description: String,

    @Enumerated(EnumType.STRING)
    val status: Status,

    @Enumerated(EnumType.STRING)
    val priority: Priority,

    val dueDate: LocalDate,

    val startDate: LocalDate,

    val isActive: Boolean = true,

    @ManyToOne
    @JoinColumn(name = "users_id")
    val users: User,

    @ManyToOne
    @JoinColumn(name = "project_id")
    val project: Project,

    @ManyToOne
    @JoinColumn(name = "sprint_id")
    val sprint: Sprint,

    val createdDate: LocalDateTime = LocalDateTime.now(),

    val updatedDate: LocalDateTime = LocalDateTime.now()
)