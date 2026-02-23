package com.example.jwtdemo.services
import com.example.jwtdemo.dto.TaskRequest
import com.example.jwtdemo.exception.NotFoundException
import com.example.jwtdemo.model.*
import com.example.jwtdemo.persistence.*
import com.example.jwtdemo.service.TaskService
import io.mockk.Runs
import io.mockk.every

import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.verify
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

@ExtendWith(MockKExtension::class)
class TaskServiceTest {

    @MockK
    lateinit var taskPersistence: TaskPersistence

    @MockK
    lateinit var userPersistence: UserPersistence

    @MockK
    lateinit var sprintPersistence: SprintPersistence

    @MockK
    lateinit var projectPersistence: ProjectPersistence

    @InjectMockKs
    lateinit var taskService: TaskService

    private lateinit var user: User
    private lateinit var project: Project
    private lateinit var sprint: Sprint
    private lateinit var task: Task

    @BeforeEach
    fun setup() {
        user = User(
            id = 1,
            username = "testuser",
            password = "password",
            role = Role.ADMIN,
            createdDate = LocalDateTime.now(),
            updatedDate = LocalDateTime.now(),
            email = "admin123@gmail.com"
        )
        project = Project(
            id = 1,
            name = "Project 1",
            description = "Project Desc",
            createdDate = LocalDateTime.now(),
            isActive = true
        )
        sprint = Sprint(
            id = 1,
            name = "Sprint 1",
            startDate = LocalDate.now(),
            endDate = LocalDate.now().plusDays(14),
            project = project,
            createdDate = LocalDateTime.now(),
            updatedDate = LocalDateTime.now()
        )

        task = Task(
            id = 1,
            title = "Task 1",
            description = "Desc",
            priority = Priority.HIGH,
            dueDate = LocalDate.now().plusDays(2),
            startDate = LocalDate.now(),
            status = Status.TODO,
            isActive = true,
            users = user,
            project = project,
            sprint = sprint,
            createdDate = LocalDateTime.now(),
            updatedDate = LocalDateTime.now()
        )
    }

    @Test
    fun `should create task successfully`() {

        val request = TaskRequest(
            title = "Task 1",
            description = "Desc",
            priority = Priority.HIGH,
            dueDate = LocalDate.now().plusDays(2),
            startDate = LocalDate.now(),
            userId = 1,
            projectId = 1,
            sprintId = 1,
            isActive = true,
            status = Status.TODO
        )

        every { userPersistence.findById(1) } returns Optional.of(user)
        every { projectPersistence.findById(1) } returns Optional.of(project)
        every { sprintPersistence.findById(1) } returns Optional.of(sprint)
        every { taskPersistence.save(any()) } returns task

        val response = taskService.createTask(request)

        assertNotNull(response)
        assertEquals("Task 1", response.title)

        verify(exactly = 1) { taskPersistence.save(any()) }
    }

    @Test
    fun `should throw exception when startDate is after dueDate`() {

        val request = TaskRequest(
            title = "Task 1",
            description = "Desc",
            priority = Priority.HIGH,
            dueDate = LocalDate.now(),
            startDate = LocalDate.now().plusDays(5),
            userId = 1,
            projectId = 1,
            sprintId = 1,
            isActive = true,
            status = Status.TODO
        )

        assertThrows<IllegalArgumentException> {
            taskService.createTask(request)
        }
    }

    @Test
    fun `should return tasks for user`() {

        every { taskPersistence.findAllByUsersId(1) } returns listOf(task)

        val result = taskService.getTaskByUserId(1)

        assertEquals(1, result.size)
    }

    @Test
    fun `should throw NotFoundException when no tasks found`() {

        every { taskPersistence.findAllByUsersId(1) } returns emptyList()

        assertThrows<NotFoundException> {
            taskService.getTaskByUserId(1)
        }
    }

    @Test
    fun `should update task successfully`() {

        val request = TaskRequest(
            title = "Task 1",
            description = "Desc",
            priority = Priority.LOW,
            dueDate = LocalDate.now().plusDays(5),
            startDate = LocalDate.now(),
            status = Status.IN_PROGRESS,
            userId = 1,
            projectId = 1,
            sprintId = 1,
            isActive = true
        )

        every { taskPersistence.findById(1) } returns Optional.of(task)
        every { taskPersistence.save(any()) } returns task

        val response = taskService.updateTask(1, request)

        assertEquals(Status.IN_PROGRESS, response.status)

        verify { taskPersistence.save(task) }
    }

    @Test
    fun `should throw exception when BLOCKED task changed to DONE`() {

        task.status = Status.BLOCKED

        val request = TaskRequest(
            title = "Task 1",
            description = "Desc",
            priority = Priority.HIGH,
            dueDate = LocalDate.now().plusDays(2),
            startDate = LocalDate.now(),
            status = Status.DONE,
            userId = 1,
            projectId = 1,
            sprintId = 1,
            isActive = true
        )

        every { taskPersistence.findById(1) } returns Optional.of(task)

        assertThrows<IllegalStateException> {
            taskService.updateTask(1, request)
        }
    }

    @Test
    fun `should delete task successfully`() {

        every { taskPersistence.findById(1) } returns Optional.of(task)
        every { taskPersistence.delete(task) } just Runs

        taskService.deleteTask(1)

        verify { taskPersistence.delete(task) }
    }

    @Test
    fun `should not delete completed task`() {

        task.status = Status.COMPLETED

        every { taskPersistence.findById(1) } returns Optional.of(task)

        assertThrows<IllegalStateException> {
            taskService.deleteTask(1)
        }
    }
}