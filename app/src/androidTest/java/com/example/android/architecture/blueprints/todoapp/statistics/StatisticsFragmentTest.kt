package com.example.android.architecture.blueprints.todoapp.statistics

import android.os.Bundle
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.MediumTest
import com.example.android.architecture.blueprints.todoapp.R
import com.example.android.architecture.blueprints.todoapp.ServiceLocator
import com.example.android.architecture.blueprints.todoapp.TodoApplication
import com.example.android.architecture.blueprints.todoapp.data.Task
import com.example.android.architecture.blueprints.todoapp.data.source.FakeAndroidTasksRepository
import com.example.android.architecture.blueprints.todoapp.data.source.TasksRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
@MediumTest
class StatisticsFragmentTest {
    private lateinit var tasksRepository: TasksRepository

    @Before
    fun createRepository() {
        tasksRepository = FakeAndroidTasksRepository()
        ServiceLocator.tasksRepository = tasksRepository
    }

    @After
    fun tearDown() = runBlockingTest {
        ServiceLocator.resetRepository()
    }

    @Test
    fun allCompletedTasks_DisplayOneHundredPercentCompletedInUi() = runBlockingTest {
        // GIVEN - Add a list of completed tasks to the DB
        listOf(
            Task("Task 1", "Do something fun", true, "id1"),
            Task("Task 2", "Do something new", true, "id2"),
            Task("Task 3", "Do something repeated", true, "id3"),
        ).forEach { tasksRepository.saveTask(it) }

        // WHEN - Statistics fragment launched to display statistics
        launchFragmentInContainer<StatisticsFragment>(Bundle(), R.style.AppTheme)

        // THEN - Task statistics are displayed on the screen
        onView(withId(R.id.stats_active_text)).check(matches(isDisplayed()))
        onView(withId(R.id.stats_active_text)).check(
            matches(
                withText(
                    getStatsText(
                        R.string.statistics_active_tasks,
                        0f
                    )
                )
            )
        )
        onView(withId(R.id.stats_completed_text)).check(matches(isDisplayed()))
        onView(withId(R.id.stats_completed_text)).check(
            matches(
                withText(
                    getStatsText(
                        R.string.statistics_completed_tasks,
                        100f
                    )
                )
            )
        )
    }

    @Test
    fun allActiveTasks_DisplayOneHundredPercentActiveInUi() = runBlockingTest {
        // GIVEN - Add a list of active tasks to the DB
        listOf(
            Task("Task 1", "Do something fun", false, "id1"),
            Task("Task 2", "Do something new", false, "id2"),
            Task("Task 3", "Do something repeated", false, "id3"),
        ).forEach { tasksRepository.saveTask(it) }

        // WHEN - Statistics fragment launched to display statistics
        launchFragmentInContainer<StatisticsFragment>(Bundle(), R.style.AppTheme)

        // THEN - Task statistics are displayed on the screen
        onView(withId(R.id.stats_active_text)).check(matches(isDisplayed()))
        onView(withId(R.id.stats_active_text)).check(
            matches(
                withText(
                    getStatsText(
                        R.string.statistics_active_tasks,
                        100f
                    )
                )
            )
        )
        onView(withId(R.id.stats_completed_text)).check(matches(isDisplayed()))
        onView(withId(R.id.stats_completed_text)).check(
            matches(
                withText(
                    getStatsText(
                        R.string.statistics_completed_tasks,
                        0f
                    )
                )
            )
        )
    }

    @Test
    fun halfActiveAndHalfCompletedTasks_DisplayFifthPercentActiveAndFifthPercentCompletedInUi() = runBlockingTest {
        // GIVEN - Add a list of active and completed tasks to the DB
        listOf(
            Task("Task 1", "Do something fun", true, "id1"),
            Task("Task 2", "Do something new", true, "id2"),
            Task("Task 3", "Do something repeated", false, "id3"),
            Task("Task 4", "Do something dangerous", false, "id4"),
        ).forEach { tasksRepository.saveTask(it) }

        // WHEN - Statistics fragment launched to display statistics
        launchFragmentInContainer<StatisticsFragment>(Bundle(), R.style.AppTheme)

        // THEN - Task statistics are displayed on the screen
        onView(withId(R.id.stats_active_text)).check(matches(isDisplayed()))
        onView(withId(R.id.stats_active_text)).check(
            matches(
                withText(
                    getStatsText(
                        R.string.statistics_active_tasks,
                        50f
                    )
                )
            )
        )
        onView(withId(R.id.stats_completed_text)).check(matches(isDisplayed()))
        onView(withId(R.id.stats_completed_text)).check(
            matches(
                withText(
                    getStatsText(
                        R.string.statistics_completed_tasks,
                        50f
                    )
                )
            )
        )
    }

    private fun getStatsText(stringId: Int, percent: Float): String =
        getApplicationContext<TodoApplication>().getString(stringId, percent)
}