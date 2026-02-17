package com.example.jwtdemo.model

import jakarta.persistence.CascadeType
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "project")
class Project(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    val name: String,
    val description: String,
    var isActive: Boolean =true,

    @OneToMany(mappedBy = "project", cascade = [CascadeType.ALL], orphanRemoval = true)
    var sprints: MutableList<Sprint> = mutableListOf(),

    @OneToMany(mappedBy = "project", cascade = [CascadeType.ALL], orphanRemoval = true)
    var tasks: MutableList<Task> = mutableListOf(),

    @OneToMany(mappedBy = "project", cascade = [CascadeType.ALL])
    var userProjects: MutableList<UserProject> = mutableListOf(),

    val createdDate: LocalDateTime =  LocalDateTime.now(),
    val updated_date: LocalDateTime = LocalDateTime.now()


)