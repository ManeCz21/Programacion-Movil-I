package com.example.proyectofinalweb.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.proyectofinalweb.model.Note
import com.example.proyectofinalweb.model.Task

@Database(entities = [Note::class, Task::class], version = 3, exportSchema = false)
abstract class NoteDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao
    abstract fun taskDao(): TaskDao
}
