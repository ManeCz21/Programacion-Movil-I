package com.example.proyectofinalweb.providers

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import com.example.proyectofinalweb.R
import java.io.File

class MiFileProviderMultimedia : FileProvider(R.xml.file_paths) {
    companion object {
        fun getImageUri(context: Context): Uri {
            val directory = File(context.cacheDir, "images")
            directory.mkdirs()
            val file = File.createTempFile(
                "img_",
                ".jpg",
                directory
            )
            val authority = "${context.packageName}.fileprovidermultimedia"
            return getUriForFile(context, authority, file)
        }

        fun getVideoUri(context: Context): Uri {
            val directory = File(context.cacheDir, "videos")
            directory.mkdirs()
            val file = File.createTempFile(
                "vid_",
                ".mp4",
                directory
            )
            val authority = "${context.packageName}.fileprovidermultimedia"
            return getUriForFile(context, authority, file)
        }
    }
}