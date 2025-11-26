package com.example.proyectofinalweb.ui.note

import com.example.proyectofinalweb.model.Attachment
import com.example.proyectofinalweb.model.Note
import com.example.proyectofinalweb.model.NoteType

data class NoteUiState(
    val id: Int = 0,
    val title: String = "",
    val description: String = "",
    val attachments: List<Attachment> = emptyList(),
    val isRecordingAudio: Boolean = false
) {
    fun toNote(): Note = Note(
        id = id,
        title = title,
        description = description,
        type = NoteType.NOTE,
        attachments = attachments
    )
}

fun Note.toNoteUiState(): NoteUiState = NoteUiState(
    id = id,
    title = title,
    description = description,
    attachments = attachments
)
