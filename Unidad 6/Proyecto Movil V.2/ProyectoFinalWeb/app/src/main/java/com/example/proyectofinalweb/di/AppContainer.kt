package com.example.proyectofinalweb.di

import android.content.Context
import androidx.room.Room
import com.example.proyectofinalweb.db.NoteDatabase

class AppContainer(private val context: Context) {

    private val db by lazy {
        Room.databaseBuilder(
            context,
            NoteDatabase::class.java, "database-name"
        ).build()
    }

    val noteDao by lazy {
        db.noteDao()
    }

    val taskDao by lazy {
        db.taskDao()
    }
}
