package com.example.proyectofinalweb.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "attachment")
data class Attachment(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val uri: String,
    val type: MediaType
)
