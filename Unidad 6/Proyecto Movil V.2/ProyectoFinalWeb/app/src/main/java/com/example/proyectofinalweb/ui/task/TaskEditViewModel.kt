package com.example.proyectofinalweb.ui.task

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectofinalweb.data.TasksRepository
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class TaskEditViewModel(
    savedStateHandle: SavedStateHandle,
    private val tasksRepository: TasksRepository
) : ViewModel() {

    var taskUiState by mutableStateOf(TaskUiState())
        private set

    private var taskId: Int? = savedStateHandle[TaskEditDestination.TASK_ID_ARG]

    init {
        taskId?.let { loadTask(it) }
    }

    fun initialize(id: Int) {
        if (taskId == null || taskId != id) {
            taskId = id
            loadTask(id)
        }
    }

    private fun loadTask(id: Int) {
        viewModelScope.launch {
            taskUiState = tasksRepository.getTaskStream(id)
                .filterNotNull()
                .first()
                .toTaskUiState()
        }
    }

    fun updateUiState(newTaskUiState: TaskUiState) {
        taskUiState = newTaskUiState
    }

    suspend fun updateTask() {
        if (taskUiState.title.isNotBlank() || taskUiState.description.isNotBlank()) {
            tasksRepository.updateTask(taskUiState.toTask())
        }
    }
}
