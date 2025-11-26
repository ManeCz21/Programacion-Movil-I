package com.example.proyectofinalweb.db

import androidx.room.TypeConverter
import com.example.proyectofinalweb.model.Attachment
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    @TypeConverter
    fun fromAttachmentList(attachments: List<Attachment>): String {
        val gson = Gson()
        return gson.toJson(attachments)
    }

    @TypeConverter
    fun toAttachmentList(attachmentsString: String): List<Attachment> {
        val listType = object : TypeToken<List<Attachment>>() {}.type
        return Gson().fromJson(attachmentsString, listType)
    }
}
