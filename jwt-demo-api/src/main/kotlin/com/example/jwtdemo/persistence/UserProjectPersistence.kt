package com.example.jwtdemo.persistence

import com.example.jwtdemo.model.UserProject
import org.springframework.data.jpa.repository.JpaRepository

interface UserProjectPersistence : JpaRepository<UserProject, Long>
