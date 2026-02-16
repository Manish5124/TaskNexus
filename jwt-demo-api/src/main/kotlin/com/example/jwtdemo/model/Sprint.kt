package com.example.jwtdemo.model

import jakarta.persistence.CascadeType
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import java.time.LocalDate
import java.time.LocalDateTime


@Entity
@Table(name = "sprint")
class Sprint(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    val name: String,

    val startDate: LocalDate,

    val endDate: LocalDate,

    @ManyToOne
    @JoinColumn(name = "project_id")
    val project: Project,

    @OneToMany(mappedBy = "sprint", cascade = [CascadeType.ALL])
    val tasks: List<Task> = mutableListOf(),

    val createdDate: LocalDateTime = LocalDateTime.now(),

    val updatedDate: LocalDateTime = LocalDateTime.now()
)