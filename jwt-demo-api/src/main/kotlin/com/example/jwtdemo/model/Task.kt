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
    var id: Long = 0,

    var title: String,

    var description: String,

    @Enumerated(EnumType.STRING)
    var status: Status = Status.TODO,

    @Enumerated(EnumType.STRING)
    var priority: Priority,

    var dueDate: LocalDate,

    var startDate: LocalDate,

    var isActive: Boolean = true,

    @ManyToOne
    @JoinColumn(name = "users_id")
    var users: User,

    @ManyToOne
    @JoinColumn(name = "project_id")
    var project: Project,

    @ManyToOne
    @JoinColumn(name = "sprint_id")
    var sprint: Sprint,

    var createdDate: LocalDateTime = LocalDateTime.now(),

    var updatedDate: LocalDateTime = LocalDateTime.now()
)