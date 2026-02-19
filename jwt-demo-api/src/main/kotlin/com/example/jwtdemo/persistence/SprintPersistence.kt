package com.example.jwtdemo.persistence

import com.example.jwtdemo.model.Sprint
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.time.LocalDate

interface SprintPersistence:JpaRepository<Sprint, Long> {

    @Query("""
        SELECT s FROM Sprint s
        WHERE s.project.id = :projectId
        AND s.startDate <= :endDate
        AND s.endDate >= :startDate
    """)
    fun findOverlappingSprints(
        projectId: Long,
        startDate: LocalDate,
        endDate: LocalDate
    ): List<Sprint>

}