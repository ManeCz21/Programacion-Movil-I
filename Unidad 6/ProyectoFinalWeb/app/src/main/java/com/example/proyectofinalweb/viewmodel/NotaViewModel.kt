package com.example.proyectofinalweb.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectofinalweb.data.NoteRepository
import com.example.proyectofinalweb.model.Note
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class NotaViewModel(private val repo: NoteRepository) : ViewModel() {

    val notes = repo.getAllNotes()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun getNoteById(id: Int, onResult: (Note?) -> Unit) {
        viewModelScope.launch {
            val n = repo.getById(id)
            onResult(n)
        }
    }

    fun addNote(note: Note, onDone: (() -> Unit)? = null) = viewModelScope.launch {
        repo.insert(note)
        onDone?.invoke()
    }

    fun updateNote(note: Note, onDone: (() -> Unit)? = null) = viewModelScope.launch {
        repo.update(note)
        onDone?.invoke()
    }

    fun deleteNote(note: Note, onDone: (() -> Unit)? = null) = viewModelScope.launch {
        repo.delete(note)
        onDone?.invoke()
    }
}
