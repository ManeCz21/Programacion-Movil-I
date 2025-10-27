package com.example.proyectofinalweb.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import androidx.lifecycle.ViewModel
import java.util.*

class CrearNotaViewModel : ViewModel() {

    // Variables de estado
    private val _title = mutableStateOf("")
    val title: State<String> = _title

    private val _description = mutableStateOf("")
    val description: State<String> = _description

    private val _isTask = mutableStateOf(false)
    val isTask: State<Boolean> = _isTask

    private val _taskTime = mutableStateOf("00:00")
    val taskTime: State<String> = _taskTime

    private val _taskDate = mutableStateOf("")
    val taskDate: State<String> = _taskDate

    // Métodos para actualizar el estado
    fun onTitleChange(newTitle: String) {
        _title.value = newTitle
    }

    fun onDescriptionChange(newDescription: String) {
        _description.value = newDescription
    }

    fun onTaskTypeChange(isTask: Boolean) {
        _isTask.value = isTask
    }

    fun onTaskTimeChange(newTime: String) {
        _taskTime.value = newTime
    }

    fun onTaskDateChange(newDate: String) {
        _taskDate.value = newDate
    }

    // Aquí puedes agregar más métodos si fuera necesario (como guardar la tarea, etc.)
}
