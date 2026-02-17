package com.example.jwtdemo.persistence

import com.example.jwtdemo.model.Project
import com.example.jwtdemo.model.User
import com.example.jwtdemo.model.UserProject
import org.springframework.data.jpa.repository.JpaRepository

interface UserProjectPersistence : JpaRepository<UserProject, Long>{
    fun existsByUsersAndProject(user: User, project: Project): Boolean
    fun findByUsersAndProject(user: User, project: Project): UserProject?
}
