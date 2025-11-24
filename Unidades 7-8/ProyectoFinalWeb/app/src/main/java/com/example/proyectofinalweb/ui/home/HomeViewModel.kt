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
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class HomeViewModel(
    private val notesRepository: NotesRepository,
    private val tasksRepository: TasksRepository
) : ViewModel() {

    private val _selectionState = MutableStateFlow(SelectionState())
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    @OptIn(kotlinx.coroutines.FlowPreview::class)
    val homeUiState: StateFlow<HomeUiState> =
        searchQuery
            .debounce(300L)
            .flatMapLatest { query ->
                val notesFlow = if (query.isBlank()) {
                    notesRepository.getAllNotesStream()
                } else {
                    notesRepository.searchNotesStream(query)
                }

                val tasksFlow = if (query.isBlank()) {
                    tasksRepository.getAllTasksStream()
                } else {
                    tasksRepository.searchTasksStream(query)
                }

                combine(
                    notesFlow,
                    tasksFlow,
                    _selectionState
                ) { notes, tasks, selection ->
                    HomeUiState(
                        noteList = notes,
                        taskList = tasks,
                        selectedNote = notes.find { it.id == selection.selectedNoteId },
                        selectedTask = tasks.find { it.id == selection.selectedTaskId },
                        isEditingNote = selection.isEditingNote,
                        isEditingTask = selection.isEditingTask
                    )
                }
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000L),
                initialValue = HomeUiState()
            )


    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    suspend fun completeTask(task: Task, isCompleted: Boolean) {
        tasksRepository.updateTask(task.copy(isCompleted = isCompleted))
    }

    fun setSelectedNote(noteId: Int) {
        _selectionState.update { it.copy(selectedNoteId = noteId, selectedTaskId = null, isEditingNote = false, isEditingTask = false) }
    }

    fun setSelectedTask(taskId: Int) {
        _selectionState.update { it.copy(selectedNoteId = null, selectedTaskId = taskId, isEditingNote = false, isEditingTask = false) }
    }

    fun closeDetailScreen() {
        _selectionState.update { SelectionState() }
    }

    fun onEditNote() {
        _selectionState.update { it.copy(isEditingNote = true) }
    }

    fun onEditTask() {
        _selectionState.update { it.copy(isEditingTask = true) }
    }

    fun onBackFromEdit() {
        _selectionState.update { it.copy(isEditingNote = false, isEditingTask = false) }
    }
}

private data class SelectionState(
    val selectedNoteId: Int? = null,
    val selectedTaskId: Int? = null,
    val isEditingNote: Boolean = false,
    val isEditingTask: Boolean = false
)

data class HomeUiState(
    val noteList: List<Note> = listOf(),
    val taskList: List<Task> = listOf(),
    val selectedNote: Note? = null,
    val selectedTask: Task? = null,
    val isEditingNote: Boolean = false,
    val isEditingTask: Boolean = false
)
