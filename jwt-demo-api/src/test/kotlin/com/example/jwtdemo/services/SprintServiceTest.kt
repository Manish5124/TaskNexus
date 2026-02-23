package com.example.jwtdemo.services

import com.example.jwtdemo.dto.SprintRequest
import com.example.jwtdemo.dto.UpdateSprintRequest
import com.example.jwtdemo.exception.ConflictException
import com.example.jwtdemo.exception.NotFoundException
import com.example.jwtdemo.model.Project
import com.example.jwtdemo.model.Sprint
import com.example.jwtdemo.persistence.ProjectPersistence
import com.example.jwtdemo.persistence.SprintPersistence
import com.example.jwtdemo.service.SprintService
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK

import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*
import kotlin.collections.emptyList

@ExtendWith(MockKExtension::class)
class SprintServiceTest {

    @MockK
    lateinit var sprintPersistence: SprintPersistence

    @MockK
    lateinit var projectPersistence: ProjectPersistence

    @InjectMockKs
    lateinit var sprintService: SprintService

    private lateinit var project: Project
    private lateinit var sprint: Sprint

    @BeforeEach
    fun setup() {
        project = Project(
            id = 1,
            name = "Test Project",
            description = "A project for testing",
            createdDate = LocalDateTime.now(),
            updated_date = LocalDateTime.now(),
        )

        sprint = Sprint(
            id = 1,
            name = "Sprint 1",
            startDate = LocalDate.now(),
            endDate = LocalDate.now().plusDays(10),
            project = project,
            createdDate = LocalDateTime.now(),
            updatedDate = LocalDateTime.now()
        )
    }

    // =========================
    // CREATE SPRINT
    // =========================

    @Test
    fun `should create sprint successfully`() {

        val request = SprintRequest(
            name = "Sprint 1",
            startDate = LocalDate.now(),
            endDate = LocalDate.now().plusDays(5),
            projectId = 1
        )

        every { projectPersistence.findById(1) } returns Optional.of(project)
        every {
            sprintPersistence.findOverlappingSprints(any(), any(), any())
        } returns emptyList()
        every { sprintPersistence.save(any()) } returns sprint

        val result = sprintService.createSprint(request)

        assertNotNull(result)
        assertEquals("Sprint 1", result.name)

        verify { sprintPersistence.save(any()) }
    }

    @Test
    fun `should throw ConflictException when startDate after endDate`() {

        val request = SprintRequest(
            name = "Sprint 1",
            startDate = LocalDate.now().plusDays(5),
            endDate = LocalDate.now(),
            projectId = 1
        )

        assertThrows<ConflictException> {
            sprintService.createSprint(request)
        }
    }

    @Test
    fun `should throw NotFoundException when project not found`() {

        val request = SprintRequest(
            name = "Sprint 1",
            startDate = LocalDate.now(),
            endDate = LocalDate.now().plusDays(5),
            projectId = 1
        )

        every { projectPersistence.findById(1) } returns Optional.empty()

        assertThrows<NotFoundException> {
            sprintService.createSprint(request)
        }
    }

    @Test
    fun `should throw ConflictException when sprint overlaps`() {

        val request = SprintRequest(
            name = "Sprint 1",
            startDate = LocalDate.now(),
            endDate = LocalDate.now().plusDays(5),
            projectId = 1
        )

        every { projectPersistence.findById(1) } returns Optional.of(project)
        every {
            sprintPersistence.findOverlappingSprints(any(), any(), any())
        } returns listOf(sprint)

        assertThrows<ConflictException> {
            sprintService.createSprint(request)
        }
    }

    // =========================
    // GET BY ID
    // =========================

    @Test
    fun `should return sprint response when found`() {

        sprint.tasks = emptyList()

        every { sprintPersistence.findById(1) } returns Optional.of(sprint)

        val result = sprintService.getSprintsById(1)

        assertNotNull(result)
        assertEquals(1, result!!.id)
    }

    @Test
    fun `should return null when sprint not found`() {

        every { sprintPersistence.findById(1) } returns Optional.empty()

        val result = sprintService.getSprintsById(1)

        assertNull(result)
    }

    // =========================
    // COUNT
    // =========================

    @Test
    fun `should return sprint size by project id`() {

        every { sprintPersistence.countByProjectId(1) } returns 3

        val result = sprintService.getSprintSizeByProjectId(1)

        assertEquals(3, result)
    }

    // =========================
    // GET ALL
    // =========================

    @Test
    fun `should return all sprints`() {

        every { sprintPersistence.findAll() } returns listOf(sprint)

        val result = sprintService.getAllSprints()

        assertEquals(1, result.size)
    }

    // =========================
    // UPDATE
    // =========================

    @Test
    fun `should update sprint successfully`() {

        val request = UpdateSprintRequest(
            name = "Updated Sprint",
            startDate = LocalDate.now(),
            endDate = LocalDate.now().plusDays(7)
        )

        every { sprintPersistence.findById(1) } returns Optional.of(sprint)
        every { sprintPersistence.save(any()) } returns sprint

        val result = sprintService.updateSprint(1, request)

        assertEquals("Updated Sprint", result.name)
    }

    @Test
    fun `should throw exception when update dates invalid`() {

        val request = UpdateSprintRequest(
            name = "Updated Sprint",
            startDate = LocalDate.now().plusDays(5),
            endDate = LocalDate.now()
        )

        every { sprintPersistence.findById(1) } returns Optional.of(sprint)

        assertThrows<IllegalArgumentException> {
            sprintService.updateSprint(1, request)
        }
    }

    // =========================
    // DELETE
    // =========================

    @Test
    fun `should delete sprint successfully`() {

        every { sprintPersistence.findById(1) } returns Optional.of(sprint)
        every { sprintPersistence.delete(sprint) } just Runs

        sprintService.deleteSprintById(1)

        verify { sprintPersistence.delete(sprint) }
    }
}