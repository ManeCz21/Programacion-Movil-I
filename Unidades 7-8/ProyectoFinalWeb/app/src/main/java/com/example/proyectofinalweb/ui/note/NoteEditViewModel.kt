package com.example.proyectofinalweb.ui.note

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectofinalweb.data.NotesRepository
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class NoteEditViewModel(
    savedStateHandle: SavedStateHandle,
    private val notesRepository: NotesRepository
) : ViewModel() {

    var noteUiState by mutableStateOf(NoteUiState())
        private set

    private var noteId: Int? = savedStateHandle[NoteEditDestination.NOTE_ID_ARG]

    init {
        noteId?.let { loadNote(it) }
    }

    fun initialize(id: Int) {
        if (noteId == null || noteId != id) {
            noteId = id
            loadNote(id)
        }
    }

    private fun loadNote(id: Int) {
        viewModelScope.launch {
            noteUiState = notesRepository.getNoteStream(id)
                .filterNotNull()
                .first()
                .toNoteUiState()
        }
    }

    fun updateUiState(newNoteUiState: NoteUiState) {
        noteUiState = newNoteUiState
    }

    suspend fun updateNote() {
        if (noteUiState.title.isNotBlank() || noteUiState.description.isNotBlank()) {
            notesRepository.updateNote(noteUiState.toNote())
        }
    }
}
