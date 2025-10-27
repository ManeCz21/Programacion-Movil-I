package com.example.proyectofinalweb.data

import com.example.proyectofinalweb.model.Note
import kotlinx.coroutines.flow.Flow

class NoteRepository(private val dao: NoteDao) {
    fun getAllNotes(): Flow<List<Note>> = dao.getAllNotes()
    suspend fun getById(id: Int): Note? = dao.getNoteById(id)
    suspend fun insert(note: Note): Long = dao.insert(note)
    suspend fun update(note: Note) = dao.update(note)
    suspend fun delete(note: Note) = dao.delete(note)
    suspend fun getNotesWithNotificationsUntil(until: Long) = dao.getNotesWithNotificationsUntil(until)
}
