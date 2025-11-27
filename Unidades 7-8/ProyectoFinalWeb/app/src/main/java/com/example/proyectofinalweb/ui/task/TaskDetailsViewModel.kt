package com.example.proyectofinalweb.ui.task

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectofinalweb.data.TasksRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class TaskDetailsViewModel(
    savedStateHandle: SavedStateHandle,
    private val tasksRepository: TasksRepository
) : ViewModel() {

    private val _taskId = MutableStateFlow<Int?>(savedStateHandle[TaskDetailsDestination.TASK_ID_ARG])

    val uiState: StateFlow<TaskUiState> = _taskId
        .filterNotNull()
        .flatMapLatest { taskId ->
            tasksRepository.getTaskStream(taskId)
                .filterNotNull()
                .map { it.toTaskUiState() }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = TaskUiState() // Vuelve al estado inicial simple
        )

    suspend fun deleteTask() {
        tasksRepository.deleteTask(uiState.value.toTask())
    }

    fun setTaskId(taskId: Int) {
        _taskId.value = taskId
    }
}
