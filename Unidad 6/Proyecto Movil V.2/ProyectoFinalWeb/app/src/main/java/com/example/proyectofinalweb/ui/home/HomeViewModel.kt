package com.example.proyectofinalweb.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectofinalweb.data.NotesRepository
import com.example.proyectofinalweb.data.TasksRepository
import com.example.proyectofinalweb.model.Note
import com.example.proyectofinalweb.model.Task
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class HomeViewModel(
    notesRepository: NotesRepository,
    private val tasksRepository: TasksRepository
) : ViewModel() {

    private val _selectionState = MutableStateFlow(SelectionState())

    val homeUiState: StateFlow<HomeUiState> = combine(
        notesRepository.getAllNotesStream(),
        tasksRepository.getAllTasksStream(),
        _selectionState
    ) { notes, tasks, selection ->
        HomeUiState(
            noteList = notes,
            taskList = tasks,
            selectedNote = notes.find { it.id == selection.selectedNoteId },
            selectedTask = tasks.find { it.id == selection.selectedTaskId }
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = HomeUiState()
    )

    suspend fun completeTask(task: Task, isCompleted: Boolean) {
        tasksRepository.updateTask(task.copy(isCompleted = isCompleted))
    }

    fun setSelectedNote(noteId: Int) {
        _selectionState.update { it.copy(selectedNoteId = noteId, selectedTaskId = null) }
    }

    fun setSelectedTask(taskId: Int) {
        _selectionState.update { it.copy(selectedNoteId = null, selectedTaskId = taskId) }
    }

    fun closeDetailScreen() {
        _selectionState.update { SelectionState() }
    }
}

private data class SelectionState(
    val selectedNoteId: Int? = null,
    val selectedTaskId: Int? = null,
)

data class HomeUiState(
    val noteList: List<Note> = listOf(),
    val taskList: List<Task> = listOf(),
    val selectedNote: Note? = null,
    val selectedTask: Task? = null,
)
