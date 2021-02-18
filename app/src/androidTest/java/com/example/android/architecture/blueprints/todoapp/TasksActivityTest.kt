package com.example.android.architecture.blueprints.todoapp

import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.example.android.architecture.blueprints.todoapp.data.Task
import com.example.android.architecture.blueprints.todoapp.data.source.TasksRepository
import com.example.android.architecture.blueprints.todoapp.tasks.TasksActivity
import com.example.android.architecture.blueprints.todoapp.util.ext.RecyclerViewAssertions.hasItemsCount
import com.example.android.architecture.blueprints.todoapp.util.ext.RecyclerViewAssertions.hasViewWithTextAtPosition
import com.example.android.architecture.blueprints.todoapp.util.ext.RecyclerViewAssertions.isEmptyList
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.not
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class TasksActivityTest {
    private lateinit var repository: TasksRepository

    @get:Rule
    val activityScenarioRule = ActivityScenarioRule(TasksActivity::class.java)

    @Before
    fun initRepository() {
        repository = ServiceLocator.provideTasksRepository(getApplicationContext())
        runBlocking {
            repository.deleteAllTasks()
        }
    }

    @After
    fun resetRepository() {
        ServiceLocator.resetRepository()
    }

    @Test
    fun noTasks_addTask() {
        // GIVEN - empty tasks list
        onView(withId(R.id.tasks_list)).check(isEmptyList())

        // WHEN - click and add task
        onView(withId(R.id.add_task_fab)).perform(click())
        onView(withId(R.id.add_task_title_edit_text)).perform(replaceText("Task 1"))
        onView(withId(R.id.add_task_description_edit_text)).perform(replaceText("Some task description"))
        onView(withId(R.id.save_task_fab)).perform(click())

        // THEN - check title is displayed
        onView(withText("Task 1")).check(matches(isDisplayed()))
    }

    @Test
    fun existingTasks_addTask(): Unit = runBlocking {
        // GIVEN -
        listOf(
            Task(title = "Task 1", description = "Some task 1 description"),
            Task(title = "Task 2", description = "Some task 2 description"),
            Task(title = "Task 3", description = "Some task 3 description"),
        ).onEach { repository.saveTask(it) }

        // WHEN - click and add task
        onView(withId(R.id.tasks_list)).check(hasItemsCount(3))
        onView(withId(R.id.add_task_fab)).perform(click())
        onView(withId(R.id.add_task_title_edit_text)).perform(replaceText("Task 4"))
        onView(withId(R.id.add_task_description_edit_text)).perform(replaceText("Some task 4 description"))
        onView(withId(R.id.save_task_fab)).perform(click())

        // THEN -
        onView(withId(R.id.tasks_list)).check(hasItemsCount(4))
        onView(withId(R.id.tasks_list)).check(hasViewWithTextAtPosition(0, "Task 1"))
        onView(withId(R.id.tasks_list)).check(hasViewWithTextAtPosition(1, "Task 2"))
        onView(withId(R.id.tasks_list)).check(hasViewWithTextAtPosition(2, "Task 3"))
        onView(withId(R.id.tasks_list)).check(hasViewWithTextAtPosition(3, "Task 4"))
    }

//
//    @Test
//    fun noTasks_addAndEditTask() {
//
//    }
//
//    @Test
//    fun noTasks_addAndDeleteTask() {
//
//    }

    @Test
    fun existingTask_editTask(): Unit = runBlocking {
        // GIVEN -
        repository.saveTask(Task(title = "Task title", description = "Task description"))

        // WHEN -
        onView(withId(R.id.tasks_list)).check(hasItemsCount(1))
        onView(withText("Task title")).perform(click())
        // THEN -
        onView(withId(R.id.task_detail_title_text)).check(matches(withText("Task title")))
        onView(withId(R.id.task_detail_description_text)).check(matches(withText("Task description")))
        onView(withId(R.id.task_detail_complete_checkbox)).check(matches(not(isChecked())))

        // WHEN -
        onView(withId(R.id.edit_task_fab)).perform(click())
        onView(withId(R.id.add_task_title_edit_text)).perform(replaceText("New task title"))
        onView(withId(R.id.add_task_description_edit_text)).perform(replaceText("New task description"))
        onView(withId(R.id.save_task_fab)).perform(click())
        // THEN -
        onView(withId(R.id.tasks_list)).check(hasItemsCount(1))
        onView(withText("Task title")).check(doesNotExist())
        onView(withText("New task title")).check(matches(isDisplayed()))
    }
}