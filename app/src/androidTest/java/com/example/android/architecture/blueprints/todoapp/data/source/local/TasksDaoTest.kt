package com.example.android.architecture.blueprints.todoapp.data.source.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.example.android.architecture.blueprints.todoapp.data.Task
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@SmallTest
class TasksDaoTest {
    @get:Rule
    val instantTaskExecutorRule =  InstantTaskExecutorRule()

    private lateinit var database: ToDoDatabase
    private lateinit var taskDao: TasksDao

    @Before
    fun setupDb() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            ToDoDatabase::class.java
        ).build()
        taskDao = database.taskDao()
    }

    @After
    fun closeDb() = database.close()

    @Test
    fun insertTaskAndGetById() = runBlockingTest {
        // GIVEN - Insert a task.
        val task = Task("Task 1", "Do something fun", true)
        taskDao.insertTask(task)

        // WHEN - Get the task by id from the database.
        val found = taskDao.getTaskById(task.id)

        // THEN - The loaded data contains the expected values.
        assertThat(found as Task, notNullValue())
        with(task) {
            assertThat(found.id, `is`(id))
            assertThat(found.title, `is`(title))
            assertThat(found.description, `is`(description))
            assertThat(found.isCompleted, `is`(isCompleted))
        }
    }

    @Test
    fun updateTaskAndGetById() = runBlockingTest {
        // GIVEN - Insert a task.
        val task = Task("Task 2", "Do something new", false)
        taskDao.insertTask(task)

        // GIVEN - Updated a task.
        val updated = task.copy(
            title = "Task 2 (2)",
            description = "Do something new (new)",
            isCompleted = true
        )
        taskDao.updateTask(updated)

        // WHEN - Get the task by id from the database.
        val found = taskDao.getTaskById(task.id)

        // THEN - The loaded data contains the expected values.
        assertThat(found as Task, notNullValue())
        assertThat(found.id, `is`(task.id))
        with(updated) {
            assertThat(found.title, `is`(title))
            assertThat(found.description, `is`(description))
            assertThat(found.isCompleted, `is`(isCompleted))
        }
    }
}