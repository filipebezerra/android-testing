package com.example.android.architecture.blueprints.todoapp.taskdetail

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.filters.MediumTest
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.android.architecture.blueprints.todoapp.R
import com.example.android.architecture.blueprints.todoapp.data.Task
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@MediumTest
class TaskDetailFragmentTest {
    @Test
    fun activeTaskDetails_DisplayedInUi() {
        // GIVEN - Add active (incomplete) task to the database
        val activeTask = Task(title = "Do something", isCompleted = false)
        val bundle = TaskDetailFragmentArgs(activeTask.id).toBundle()

        // WHEN - Details fragment launched to display task
        launchFragmentInContainer<TaskDetailFragment>(bundle, R.style.AppTheme)
        Thread.sleep(2000)
    }
}