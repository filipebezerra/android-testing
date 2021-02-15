package com.example.android.architecture.blueprints.todoapp.statistics

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.android.architecture.blueprints.todoapp.MainCoroutineRule
import com.example.android.architecture.blueprints.todoapp.data.Task
import com.example.android.architecture.blueprints.todoapp.data.source.FakeTasksRepository
import com.example.android.architecture.blueprints.todoapp.data.source.TasksRepository
import com.example.android.architecture.blueprints.todoapp.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.Is.`is`
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class StatisticsViewModelTest {

    @ExperimentalCoroutinesApi
    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var statisticsViewModel: StatisticsViewModel
    private lateinit var tasksRepository: FakeTasksRepository

    @ExperimentalCoroutinesApi
    @Before
    fun setupViewModel() = mainCoroutineRule.runBlockingTest {
        tasksRepository = FakeTasksRepository()
        listOf(
            Task("Task 1", "Do something fun", true),
            Task("Task 2", "Do something new", false),
            Task("Task 3", "Do something repeated", true)
        ).onEach {
            tasksRepository.saveTask(it)
        }
        statisticsViewModel = StatisticsViewModel(tasksRepository)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun refreshTasks_loading() {
        mainCoroutineRule.pauseDispatcher()
        statisticsViewModel.refresh()

        assertThat(statisticsViewModel.dataLoading.getOrAwaitValue(), `is`(true))
        mainCoroutineRule.resumeDispatcher()

        assertThat(statisticsViewModel.dataLoading.getOrAwaitValue(), `is`(false))
    }

    @Test
    fun refreshStatisticsWhenTasksAreUnavailable_callErrorToDisplay() {
        tasksRepository.setReturnError(true)

        statisticsViewModel.refresh()

        assertThat(statisticsViewModel.error.getOrAwaitValue(), `is`(true))
        assertThat(statisticsViewModel.empty.getOrAwaitValue(), `is`(true))
    }
}