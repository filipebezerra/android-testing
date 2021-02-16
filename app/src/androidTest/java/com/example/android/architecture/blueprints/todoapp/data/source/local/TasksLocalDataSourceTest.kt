package com.example.android.architecture.blueprints.todoapp.data.source.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.example.android.architecture.blueprints.todoapp.AndroidMainCoroutineRule
import com.example.android.architecture.blueprints.todoapp.data.Result
import com.example.android.architecture.blueprints.todoapp.data.Task
import com.example.android.architecture.blueprints.todoapp.data.succeeded
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@MediumTest
class TasksLocalDataSourceTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainCoroutineRule = AndroidMainCoroutineRule()

    private lateinit var tasksLocalDataSource: TasksLocalDataSource
    private lateinit var database: ToDoDatabase

    @Before
    fun setupDb() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            ToDoDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()
        tasksLocalDataSource = TasksLocalDataSource(database.taskDao(), Dispatchers.Main)
    }

    @After
    fun closeDb() = database.close()

    @Test
    fun saveTask_retrievesTask() = mainCoroutineRule.runBlockingTest {
        // GIVEN -
        val task = Task("Task 1", "Do something fun")
        tasksLocalDataSource.saveTask(task)

        // WHEN -
        val found = tasksLocalDataSource.getTask(task.id)

        // THEN -
        assertThat(found.succeeded, `is`(true))
        found as Result.Success
        assertThat(found.data.title, `is`("Task 1"))
        assertThat(found.data.description, `is`("Do something fun"))
        assertThat(found.data.isCompleted, `is`(false))
    }

    @Test
    fun completeTask_retrievedTaskIsComplete() = mainCoroutineRule.runBlockingTest {
        // 1. Save a new active task in the local data source.
        val task = Task("Task 2", "Do something new")
        tasksLocalDataSource.saveTask(task)

        // 2. Mark it as complete.
        tasksLocalDataSource.completeTask(task)

        // 3. Check that the task can be retrieved from the local data source and is complete.
        val found = tasksLocalDataSource.getTask(task.id)
        assertThat(found.succeeded, `is`(true))
        found as Result.Success
        assertThat(found.data.isCompleted, `is`(true))
    }
}