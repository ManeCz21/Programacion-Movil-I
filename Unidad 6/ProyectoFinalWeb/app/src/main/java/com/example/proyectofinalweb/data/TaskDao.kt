package com.example.proyectofinalweb.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.proyectofinalweb.model.Task

@Dao
interface TaskDao {

    @Insert
    suspend fun insertTask(task: Task)

    @Query("SELECT * FROM tasks")
    suspend fun getAllTasks(): List<Task>

    @Query("DELETE FROM tasks WHERE id = :taskId")
    suspend fun deleteTask(taskId: Int)
}
