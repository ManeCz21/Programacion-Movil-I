package com.example.proyectofinalweb.db

import androidx.room.TypeConverter
import com.example.proyectofinalweb.model.NoteType

class Converters {
    @TypeConverter
    fun fromNoteType(value: NoteType): String {
        return value.name
    }

    @TypeConverter
    fun toNoteType(value: String): NoteType {
        return NoteType.valueOf(value)
    }
}