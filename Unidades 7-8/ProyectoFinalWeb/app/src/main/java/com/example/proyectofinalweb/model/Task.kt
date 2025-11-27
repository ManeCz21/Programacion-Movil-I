package com.example.proyectofinalweb.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.proyectofinalweb.db.Converters

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val description: String,
    val date: String, // Vuelve a tener una sola fecha
    val time: String, // Vuelve a tener una sola hora
    val isCompleted: Boolean = false,
    @TypeConverters(Converters::class) // Mantenemos el conversor para los adjuntos
    val attachments: List<Attachment> = emptyList()
)
