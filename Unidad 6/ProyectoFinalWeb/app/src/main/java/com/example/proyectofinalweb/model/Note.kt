package com.example.proyectofinalweb.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
data class Note(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String,
    val date: Long = System.currentTimeMillis(),
    // Lista de URIs en JSON (puede ser null)
    val mediaUrisJson: String? = null,
    // timestamp epoch ms para notificación (nullable)
    val notificationTime: Long? = null,
    val done: Boolean = false
)
