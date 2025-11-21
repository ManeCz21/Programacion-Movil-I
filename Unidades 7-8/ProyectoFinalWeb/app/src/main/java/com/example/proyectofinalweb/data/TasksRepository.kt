package com.example.proyectofinalweb.data

import com.example.proyectofinalweb.db.TaskDao
import com.example.proyectofinalweb.model.Task
import kotlinx.coroutines.flow.Flow

interface TasksRepository {
    fun getAllTasksStream(): Flow<List<Task>>
    fun getTaskStream(id: Int): Flow<Task?>
    suspend fun insertTask(task: Task)
    suspend fun deleteTask(task: Task)
    suspend fun updateTask(task: Task)
}

class OfflineTasksRepository(private val taskDao: TaskDao) : TasksRepository {
    override fun getAllTasksStream(): Flow<List<Task>> = taskDao.getAllTasks()
    override fun getTaskStream(id: Int): Flow<Task?> = taskDao.getTask(id)
    override suspend fun insertTask(task: Task) = taskDao.insertTask(task)
    override suspend fun deleteTask(task: Task) = taskDao.deleteTask(task)
    override suspend fun updateTask(task: Task) = taskDao.updateTask(task)
}
