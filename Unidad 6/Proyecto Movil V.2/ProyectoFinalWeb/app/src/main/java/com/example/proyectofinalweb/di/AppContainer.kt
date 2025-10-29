package com.example.proyectofinalweb.di

import android.content.Context
import com.example.proyectofinalweb.data.NotesRepository
import com.example.proyectofinalweb.data.OfflineNotesRepository
import com.example.proyectofinalweb.data.OfflineTasksRepository
import com.example.proyectofinalweb.data.TasksRepository
import com.example.proyectofinalweb.db.NoteDatabase

interface AppContainer {
    val notesRepository: NotesRepository
    val tasksRepository: TasksRepository
}

class AppDataContainer(private val context: Context) : AppContainer {
    override val notesRepository: NotesRepository by lazy {
        OfflineNotesRepository(NoteDatabase.getDatabase(context).noteDao())
    }
    override val tasksRepository: TasksRepository by lazy {
        OfflineTasksRepository(NoteDatabase.getDatabase(context).taskDao())
    }
}
