package com.example.android.architecture.blueprints.todoapp.tasks

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.MediumTest
import com.example.android.architecture.blueprints.todoapp.R
import com.example.android.architecture.blueprints.todoapp.ServiceLocator
import com.example.android.architecture.blueprints.todoapp.data.Task
import com.example.android.architecture.blueprints.todoapp.data.source.FakeAndroidTasksRepository
import com.example.android.architecture.blueprints.todoapp.data.source.TasksRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

@MediumTest
@ExperimentalCoroutinesApi
class TasksFragmentTest {
    private lateinit var tasksRepository: TasksRepository

    @Before
    fun createRepository() {
        tasksRepository = FakeAndroidTasksRepository()
        ServiceLocator.tasksRepository = tasksRepository
    }

    @After
    fun cleanupDb() = runBlockingTest {
        ServiceLocator.resetRepository()
    }

    @Test
    fun clickFirstTask_navigateToTaskDetailFragment() = runBlockingTest {
        tasksRepository.saveTask(Task("Task 1", "Do something fun", true, "id1"))
        tasksRepository.saveTask(Task("Task 2", "Do something new", false, "id2"))
        tasksRepository.saveTask(Task("Task 3", "Do something repeated", true, "id3"))

        // GIVEN - On the home screen
        val scenario = launchFragmentInContainer<TasksFragment>(Bundle(), R.style.AppTheme)
        val navController = mock(NavController::class.java)
            .also { navController ->
                scenario.onFragment { it ->
                    Navigation.setViewNavController(it.view!!, navController)
                }
            }

        // WHEN - Click on the first list item
        onView(withId(R.id.tasks_list))
            .perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                0,
                click()
            ))

        // THEN - Verify that we navigate to the first detail screen
        verify(navController).navigate(
            TasksFragmentDirections.actionTasksFragmentToTaskDetailFragment("id1")
        )
    }

    @Test
    fun clickTaskThree_navigateToTaskDetailFragment() = runBlockingTest {
        tasksRepository.saveTask(Task("Task 1", "Do something fun", true, "id1"))
        tasksRepository.saveTask(Task("Task 2", "Do something new", false, "id2"))
        tasksRepository.saveTask(Task("Task 3", "Do something repeated", true, "id3"))

        // GIVEN - On the home screen
        val scenario = launchFragmentInContainer<TasksFragment>(Bundle(), R.style.AppTheme)
        val navController = mock(NavController::class.java)
            .also { navController ->
                scenario.onFragment { it ->
                    Navigation.setViewNavController(it.view!!, navController)
                }
            }

        // WHEN - Click on the Task 3 item
        onView(withId(R.id.tasks_list))
            .perform(RecyclerViewActions.actionOnItem<RecyclerView.ViewHolder>(
                hasDescendant(withText("Task 3")),
                click()
            ))

        // THEN - Verify that we navigate to the Task 3 detail screen
        verify(navController).navigate(
            TasksFragmentDirections.actionTasksFragmentToTaskDetailFragment("id3")
        )
    }

    @Test
    fun clickAddTaskButton_navigateToAddEditFragment() {
        // GIVEN - On the home screen
        val scenario = launchFragmentInContainer<TasksFragment>(Bundle(), R.style.AppTheme)
        val navController = mock(NavController::class.java)
            .also { navController ->
                scenario.onFragment {
                    Navigation.setViewNavController(it.view!!, navController)
                }
            }

        // WHEN - Click on Add task button
        onView(withId(R.id.add_task_fab)).perform(click())

        // THEN - Verify that we navigate to the Add or edit screen
        verify(navController).navigate(
            TasksFragmentDirections.actionTasksFragmentToAddEditTaskFragment(
                null,
                getApplicationContext<Context>().getString(R.string.add_task)
            )
        )
    }
}