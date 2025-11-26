package com.example.proyectofinalweb.screens

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File

fun createImageUri(context: Context): Uri {
    val file = File(context.filesDir, "images/${System.currentTimeMillis()}.jpg")
    file.parentFile?.mkdirs()
    return FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
}

fun createVideoUri(context: Context): Uri {
    val file = File(context.filesDir, "videos/${System.currentTimeMillis()}.mp4")
    file.parentFile?.mkdirs()
    return FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
}
