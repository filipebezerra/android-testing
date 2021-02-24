package com.example.android.architecture.blueprints.todoapp

import android.content.Context
import androidx.annotation.VisibleForTesting
import androidx.room.Room
import com.example.android.architecture.blueprints.todoapp.data.source.DefaultTasksRepository
import com.example.android.architecture.blueprints.todoapp.data.source.TasksDataSource
import com.example.android.architecture.blueprints.todoapp.data.source.TasksRepository
import com.example.android.architecture.blueprints.todoapp.data.source.local.TasksLocalDataSource
import com.example.android.architecture.blueprints.todoapp.data.source.local.ToDoDatabase
import com.example.android.architecture.blueprints.todoapp.data.source.remote.TasksRemoteDataSource
import kotlinx.coroutines.runBlocking

object ServiceLocator {
    private val lock = Any()

    @Volatile
    var tasksRepository: TasksRepository? = null
        @VisibleForTesting set

    private var database: ToDoDatabase? = null

    fun provideTasksRepository(context: Context): TasksRepository =
        synchronized(lock) {
            tasksRepository ?: createTasksRepository(context).also { tasksRepository = it }
        }

    private fun createTasksRepository(context: Context): TasksRepository =
        DefaultTasksRepository(
            TasksRemoteDataSource,
            createTasksLocalDataSource(context)
        ).also { tasksRepository = it }

    private fun createTasksLocalDataSource(context: Context): TasksDataSource =
        (database ?: createDatabase(context))
            .run { TasksLocalDataSource(taskDao()) }

    private fun createDatabase(context: Context): ToDoDatabase =
        Room.databaseBuilder(
            context.applicationContext,
            ToDoDatabase::class.java, "Tasks.db"
        )
            .build()
            .also {
                database = it
            }

    @VisibleForTesting
    // https://developer.android.com/reference/kotlin/androidx/annotation/VisibleForTesting
    fun resetRepository() {
        synchronized(lock) {
            runBlocking { TasksRemoteDataSource.deleteAllTasks() }
            // Clear all data to avoid test pollution.
            database?.apply {
                clearAllTables()
                close()
            }
            database = null
            tasksRepository = null
        }
    }
}