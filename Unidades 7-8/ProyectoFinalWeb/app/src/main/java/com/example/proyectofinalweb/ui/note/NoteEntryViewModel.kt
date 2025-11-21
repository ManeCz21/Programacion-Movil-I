package com.example.proyectofinalweb.ui.note

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.proyectofinalweb.data.NotesRepository
import com.example.proyectofinalweb.model.Attachment
import com.example.proyectofinalweb.model.Note
import com.example.proyectofinalweb.model.NoteType

class NoteEntryViewModel(private val notesRepository: NotesRepository) : ViewModel() {
    var noteUiState by mutableStateOf(NoteUiState())
        private set

    fun updateUiState(newNoteUiState: NoteUiState) {
        noteUiState = newNoteUiState
    }

    suspend fun saveNote() {
        if (noteUiState.title.isNotBlank() || noteUiState.description.isNotBlank()) {
            notesRepository.insertNote(noteUiState.toNote())
        }
    }
}

data class NoteUiState(
    val id: Int = 0,
    val title: String = "",
    val description: String = "",
    val attachments: List<Attachment> = emptyList()
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
