package com.example.android.architecture.blueprints.todoapp

import android.content.Context
import androidx.room.Room
import com.example.android.architecture.blueprints.todoapp.data.source.DefaultTasksRepository
import com.example.android.architecture.blueprints.todoapp.data.source.TasksDataSource
import com.example.android.architecture.blueprints.todoapp.data.source.TasksRepository
import com.example.android.architecture.blueprints.todoapp.data.source.local.TasksLocalDataSource
import com.example.android.architecture.blueprints.todoapp.data.source.local.ToDoDatabase
import com.example.android.architecture.blueprints.todoapp.data.source.remote.TasksRemoteDataSource

object ServiceLocator {
    @Volatile
    private var tasksRepository: TasksRepository? = null

    private var database: ToDoDatabase? = null

    fun provideTasksRepository(context: Context): TasksRepository =
        synchronized(this) {
            tasksRepository ?: createTasksRepository(context).also { tasksRepository = it }
        }

    private fun createTasksRepository(context: Context): TasksRepository =
        DefaultTasksRepository(
            TasksRemoteDataSource,
            createTasksLocalDataSource(context),
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
}