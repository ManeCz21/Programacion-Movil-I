package com.example.proyectofinalweb.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String,
    val isTask: Boolean,
    val taskTime: String,
    val taskDate: String
)
