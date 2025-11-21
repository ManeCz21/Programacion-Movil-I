package com.example.proyectofinalweb.model


data class Attachment(
    val uri: String,
    val type: AttachmentType
)

enum class AttachmentType {
    IMAGE, VIDEO, AUDIO, FILE
}
