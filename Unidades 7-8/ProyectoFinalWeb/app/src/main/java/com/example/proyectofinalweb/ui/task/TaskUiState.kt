package com.example.proyectofinalweb.ui.task

import com.example.proyectofinalweb.model.Attachment
import com.example.proyectofinalweb.model.Task

data class TaskUiState(
    val id: Int = 0,
    val title: String = "",
    val description: String = "",
    val date: String = "", // Vuelve a tener una sola fecha
    val time: String = "", // Vuelve a tener una sola hora
    val isCompleted: Boolean = false,
    val attachments: List<Attachment> = emptyList(),
    val isRecordingAudio: Boolean = false
) {
    fun toTask(): Task = Task(
        id = id,
        title = title,
        description = description,
        date = date,
        time = time,
        isCompleted = isCompleted,
        attachments = attachments
    )
}

fun Task.toTaskUiState(): TaskUiState = TaskUiState(
    id = id,
    title = title,
    description = description,
    date = date,
    time = time,
    isCompleted = isCompleted,
    attachments = attachments
)
