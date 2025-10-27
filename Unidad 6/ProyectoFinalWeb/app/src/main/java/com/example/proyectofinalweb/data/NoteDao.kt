package com.example.proyectofinalweb.data

import androidx.room.*
import com.example.proyectofinalweb.model.Note
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {

    @Query("SELECT * FROM notes ORDER BY date DESC")
    fun getAllNotes(): Flow<List<Note>>

    @Query("SELECT * FROM notes WHERE id = :id LIMIT 1")
    suspend fun getNoteById(id: Int): Note?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(note: Note): Long

    @Update
    suspend fun update(note: Note)

    @Delete
    suspend fun delete(note: Note)

    // Para worker: notas con notificationTime >= now
    @Query("SELECT * FROM notes WHERE notificationTime IS NOT NULL AND notificationTime <= :until")
    suspend fun getNotesWithNotificationsUntil(until: Long): List<Note>
}
