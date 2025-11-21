package com.example.proyectofinalweb.data

import androidx.room.TypeConverter
import com.example.proyectofinalweb.model.Attachment
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromAttachmentList(value: List<Attachment>?): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toAttachmentList(value: String): List<Attachment>? {
        val type = object : TypeToken<List<Attachment>?>() {}.type
        return gson.fromJson(value, type)
    }
}
