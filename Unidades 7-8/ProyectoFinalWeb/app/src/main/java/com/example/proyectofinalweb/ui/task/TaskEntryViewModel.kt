package com.example.proyectofinalweb.ui.task

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.proyectofinalweb.data.TasksRepository

class TaskEntryViewModel(private val tasksRepository: TasksRepository) : ViewModel() {
    var taskUiState by mutableStateOf(TaskUiState())
        private set

    fun updateUiState(newTaskUiState: TaskUiState) {
        taskUiState = newTaskUiState
    }

    suspend fun saveTask() {
        if (taskUiState.title.isNotBlank() || taskUiState.description.isNotBlank()) {
            tasksRepository.insertTask(taskUiState.toTask())
        }
    }
}
