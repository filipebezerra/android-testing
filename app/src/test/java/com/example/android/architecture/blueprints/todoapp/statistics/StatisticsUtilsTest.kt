package com.example.android.architecture.blueprints.todoapp.statistics

import com.example.android.architecture.blueprints.todoapp.data.Task
import org.junit.Assert.*
import org.junit.Test

class StatisticsUtilsTest {
    @Test
    fun given_no_completed_tasks_when_get_active_and_completed_then_zero_completed_and_hundred_active() {
        val tasks = listOf(
            Task("task 1", "description taks 1", isCompleted = false)
        )
        val result = getActiveAndCompletedStats(tasks)
        assertEquals(0f, result.completedTasksPercent)
        assertEquals(100f, result.activeTasksPercent)
    }

    @Test
    fun given_fourty_completed_and_sixty_active_tasks_when_get_active_and_completed_then_fourty_completed_and_sixty_active() {
        val tasks = listOf(
            Task("task 1", "description taks 1", isCompleted = true),
            Task("task 2", "description taks 2", isCompleted = true),
            Task("task 3", "description taks 3", isCompleted = false),
            Task("task 4", "description taks 4", isCompleted = false),
            Task("task 5", "description taks 5", isCompleted = false),
        )
        val result = getActiveAndCompletedStats(tasks)
        assertEquals(40f, result.completedTasksPercent)
        assertEquals(60f, result.activeTasksPercent)
    }

    @Test
    fun given_empty_task_list_when_get_active_and_completed_then_active_and_completed_should_be_zero() {
        val tasks = emptyList<Task>()
        val result = getActiveAndCompletedStats(tasks)
        assertEquals(0f, result.completedTasksPercent)
        assertEquals(0f, result.activeTasksPercent)
    }

    @Test
    fun given_null_task_list_when_get_active_and_completed_then_active_and_completed_should_be_zero() {
        val tasks = null
        val result = getActiveAndCompletedStats(tasks)
        assertEquals(0f, result.completedTasksPercent)
        assertEquals(0f, result.activeTasksPercent)
    }
}