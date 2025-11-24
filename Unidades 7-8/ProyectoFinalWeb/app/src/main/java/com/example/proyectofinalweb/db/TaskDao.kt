package com.example.proyectofinalweb.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.proyectofinalweb.model.Task
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks ORDER BY date ASC, time ASC")
    fun getAllTasks(): Flow<List<Task>>

    @Query("SELECT * from tasks WHERE id = :id")
    fun getTask(id: Int): Flow<Task?>

    @Insert
    suspend fun insertTask(task: Task)

    @Update
    suspend fun updateTask(task: Task)

    @Delete
    suspend fun deleteTask(task: Task)

    @Query("SELECT * FROM tasks WHERE title LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%' ORDER BY date ASC, time ASC")
    fun searchTasks(query: String): Flow<List<Task>>
}
