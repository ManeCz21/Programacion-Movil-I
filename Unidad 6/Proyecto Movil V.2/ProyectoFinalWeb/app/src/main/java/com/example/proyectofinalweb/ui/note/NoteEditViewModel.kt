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

    private val noteId: Int = checkNotNull(savedStateHandle[NoteEditDestination.NOTE_ID_ARG])

    var noteUiState by mutableStateOf(NoteUiState())
        private set

    init {
        viewModelScope.launch {
            noteUiState = notesRepository.getNoteStream(noteId)
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
