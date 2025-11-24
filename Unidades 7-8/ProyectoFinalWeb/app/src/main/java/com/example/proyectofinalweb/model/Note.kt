package com.example.proyectofinalweb.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class NoteType {
    NOTE,
    TASK
}

@Entity(tableName = "note")
data class Note(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val description: String,
    val type: NoteType
)
