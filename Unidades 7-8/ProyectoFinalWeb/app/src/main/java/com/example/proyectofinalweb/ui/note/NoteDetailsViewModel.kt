package com.example.proyectofinalweb.ui.note

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectofinalweb.data.NotesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class NoteDetailsViewModel(
    savedStateHandle: SavedStateHandle,
    private val notesRepository: NotesRepository
) : ViewModel() {

    private val _noteId = MutableStateFlow<Int?>(savedStateHandle[NoteDetailsDestination.NOTE_ID_ARG])

    val uiState: StateFlow<NoteUiState> = _noteId
        .filterNotNull()
        .flatMapLatest { noteId ->
            notesRepository.getNoteStream(noteId)
                .filterNotNull()
                .map { it.toNoteUiState() }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = NoteUiState()
        )

    suspend fun deleteNote() {
        notesRepository.deleteNote(uiState.value.toNote())
    }

    fun setNoteId(noteId: Int) {
        _noteId.value = noteId
    }
}
