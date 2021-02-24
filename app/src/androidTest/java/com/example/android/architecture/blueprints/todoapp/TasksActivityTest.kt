package com.example.android.architecture.blueprints.todoapp

import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.example.android.architecture.blueprints.todoapp.data.Task
import com.example.android.architecture.blueprints.todoapp.data.source.TasksRepository
import com.example.android.architecture.blueprints.todoapp.tasks.TasksActivity
import com.example.android.architecture.blueprints.todoapp.util.DataBindingIdlingResource
import com.example.android.architecture.blueprints.todoapp.util.EspressoIdlingResource
import com.example.android.architecture.blueprints.todoapp.util.ext.RecyclerViewAssertions.hasItemsCount
import com.example.android.architecture.blueprints.todoapp.util.ext.RecyclerViewAssertions.hasViewWithTextAtPosition
import com.example.android.architecture.blueprints.todoapp.util.ext.RecyclerViewAssertions.isEmptyList
import com.example.android.architecture.blueprints.todoapp.util.ext.deleteAllTasksBlocking
import com.example.android.architecture.blueprints.todoapp.util.ext.saveTaskBlocking
import com.example.android.architecture.blueprints.todoapp.util.monitorActivity
import org.hamcrest.CoreMatchers.not
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class TasksActivityTest {
    private lateinit var repository: TasksRepository

    private val dataBindingIdlingResource = DataBindingIdlingResource()

    @Before
    fun initRepository() {
        repository = ServiceLocator.provideTasksRepository(getApplicationContext())
        repository.deleteAllTasksBlocking()
    }

    @After
    fun resetRepository() {
        ServiceLocator.resetRepository()
    }

    /**
     * Idling resources tell Espresso that the app is idle or busy. This is needed when operations
     * are not scheduled in the main Looper (for example when executed on a different thread).
     */
    @Before
    fun registerIdlingResource() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
        IdlingRegistry.getInstance().register(dataBindingIdlingResource)
    }

    /**
     * Unregister your Idling Resource so it can be garbage collected and does not leak any memory.
     */
    @After
    fun unregisterIdlingResource() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
        IdlingRegistry.getInstance().unregister(dataBindingIdlingResource)
    }

    // Any setup of the data state, such as adding tasks to the repository, must happen before
    // ActivityScenario.launch() is called. Calling such additional setup code, such as saving
    // tasks to the repository, is not currently supported by ActivityScenarioRule. Therefore,
    // we choose not to use ActivityScenarioRule and instead manually call launch and close.

    @Test
    fun editTask() {
        // Set initial state.
        repository.saveTaskBlocking(Task("TITLE1", "DESCRIPTION"))

        // Start up Tasks screen.
        val activityScenario = ActivityScenario.launch(TasksActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        // Click on the task on the list and verify that all the data is correct.
        onView(withText("TITLE1")).perform(click())
        onView(withId(R.id.task_detail_title_text)).check(matches(withText("TITLE1")))
        onView(withId(R.id.task_detail_description_text)).check(matches(withText("DESCRIPTION")))
        onView(withId(R.id.task_detail_complete_checkbox)).check(matches(not(isChecked())))

        // Click on the edit button, edit, and save.
        onView(withId(R.id.edit_task_fab)).perform(click())
        onView(withId(R.id.add_task_title_edit_text)).perform(replaceText("NEW TITLE"))
        onView(withId(R.id.add_task_description_edit_text)).perform(replaceText("NEW DESCRIPTION"))
        onView(withId(R.id.save_task_fab)).perform(click())

        // Verify task is displayed on screen in the task list.
        onView(withText("NEW TITLE")).check(matches(isDisplayed()))
        // Verify previous task is not displayed.
        onView(withText("TITLE1")).check(doesNotExist())
        // Make sure the activity is closed before resetting the db.
        activityScenario.close()
    }
    
    @Test
    fun noTasks_addTask() {
        val activityScenario = ActivityScenario.launch(TasksActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        // GIVEN - empty tasks list
        onView(withId(R.id.tasks_list)).check(isEmptyList())

        // WHEN - click and add task
        onView(withId(R.id.add_task_fab)).perform(click())
        onView(withId(R.id.add_task_title_edit_text)).perform(replaceText("Task 1"))
        onView(withId(R.id.add_task_description_edit_text)).perform(replaceText("Some task description"))
        onView(withId(R.id.save_task_fab)).perform(click())

        // THEN - check title is displayed
        onView(withText("Task 1")).check(matches(isDisplayed()))

        activityScenario.close()
    }

    @Test
    fun existingTasks_addTask() {
        // GIVEN -
        listOf(
            Task(title = "Task 1", description = "Some task 1 description"),
            Task(title = "Task 2", description = "Some task 2 description"),
            Task(title = "Task 3", description = "Some task 3 description")
        ).onEach { repository.saveTaskBlocking(it) }

        val activityScenario = ActivityScenario.launch(TasksActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

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

        activityScenario.close()
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
    fun existingTask_editTask() {
        // GIVEN -
        repository.saveTaskBlocking(Task(title = "Task title", description = "Task description"))

        val activityScenario = ActivityScenario.launch(TasksActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

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

        activityScenario.close()
    }
}