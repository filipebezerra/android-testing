package com.example.android.architecture.blueprints.todoapp.tasks

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.android.architecture.blueprints.todoapp.Event
import org.hamcrest.CoreMatchers.not
import org.hamcrest.CoreMatchers.nullValue
import org.junit.Test

import org.junit.Assert.*
import org.junit.Rule
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TasksViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Test
    fun given_user_when_clicked_on_fab_then_navigate_to_add_edit_task_screen() {
        val tasksViewModel = TasksViewModel(ApplicationProvider.getApplicationContext())

        val observer = Observer<Event<Unit>> {}
        try {
            tasksViewModel.newTaskEvent.observeForever(observer)
            tasksViewModel.addNewTask()

            val eventValue = tasksViewModel.newTaskEvent.value
            assertThat(eventValue?.getContentIfNotHandled(), not(nullValue()))
        } finally {
            tasksViewModel.newTaskEvent.removeObserver(observer)
        }
    }
}