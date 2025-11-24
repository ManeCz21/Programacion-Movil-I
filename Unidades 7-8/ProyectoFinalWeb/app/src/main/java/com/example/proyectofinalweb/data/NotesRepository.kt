package com.example.proyectofinalweb.data

import com.example.proyectofinalweb.db.NoteDao
import com.example.proyectofinalweb.model.Note
import kotlinx.coroutines.flow.Flow

interface NotesRepository {
    fun getAllNotesStream(): Flow<List<Note>>
    fun getNoteStream(id: Int): Flow<Note?>
    suspend fun insertNote(note: Note)
    suspend fun deleteNote(note: Note)
    suspend fun updateNote(note: Note)
    fun searchNotesStream(query: String): Flow<List<Note>>
}

class OfflineNotesRepository(private val noteDao: NoteDao) : NotesRepository {
    override fun getAllNotesStream(): Flow<List<Note>> = noteDao.getAllNotes()
    override fun getNoteStream(id: Int): Flow<Note?> = noteDao.getNote(id)
    override suspend fun insertNote(note: Note) = noteDao.insertNote(note)
    override suspend fun deleteNote(note: Note) = noteDao.deleteNote(note)
    override suspend fun updateNote(note: Note) = noteDao.updateNote(note)
    override fun searchNotesStream(query: String): Flow<List<Note>> = noteDao.searchNotes(query)
}
