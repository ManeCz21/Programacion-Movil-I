package com.example.proyectofinalweb.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectofinalweb.data.NotesRepository
import com.example.proyectofinalweb.data.TasksRepository
import com.example.proyectofinalweb.model.Note
import com.example.proyectofinalweb.model.Task
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class HomeViewModel(
    notesRepository: NotesRepository,
    private val tasksRepository: TasksRepository
) : ViewModel() {

    val homeUiState: StateFlow<HomeUiState> = combine(
        notesRepository.getAllNotesStream(),
        tasksRepository.getAllTasksStream()
    ) { notes, tasks ->
        HomeUiState(notes, tasks)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = HomeUiState()
    )

    suspend fun completeTask(task: Task, isCompleted: Boolean) {
        tasksRepository.updateTask(task.copy(isCompleted = isCompleted))
    }
}

data class HomeUiState(
    val noteList: List<Note> = listOf(),
    val taskList: List<Task> = listOf()
)
