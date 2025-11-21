package com.example.proyectofinalweb.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.proyectofinalweb.data.Converters

enum class NoteType {
    NOTE,
    TASK
}

@Entity(tableName = "note")
@TypeConverters(Converters::class)
data class Note(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val description: String,
    val type: NoteType,
    val attachments: List<Attachment> = emptyList()
)
