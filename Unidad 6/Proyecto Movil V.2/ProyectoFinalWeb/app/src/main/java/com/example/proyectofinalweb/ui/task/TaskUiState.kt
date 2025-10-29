package com.example.proyectofinalweb.ui.task

import com.example.proyectofinalweb.model.Task

data class TaskUiState(
    val id: Int = 0,
    val title: String = "",
    val description: String = "",
    val date: String = "",
    val time: String = "",
    val isCompleted: Boolean = false
) {
    fun toTask(): Task = Task(
        id = id,
        title = title,
        description = description,
        date = date,
        time = time,
        isCompleted = isCompleted
    )
}

fun Task.toTaskUiState(): TaskUiState = TaskUiState(
    id = id,
    title = title,
    description = description,
    date = date,
    time = time,
    isCompleted = isCompleted
)
