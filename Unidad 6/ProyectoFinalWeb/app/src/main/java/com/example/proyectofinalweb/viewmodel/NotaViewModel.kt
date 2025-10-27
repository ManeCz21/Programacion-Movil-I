package com.example.proyectofinalweb.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectofinalweb.model.Task
import com.example.proyectofinalweb.data.TaskRepository
import kotlinx.coroutines.launch

class NotaViewModel(private val repository: TaskRepository) : ViewModel() {

    fun saveTask(task: Task) {
        viewModelScope.launch {
            repository.saveTask(task)
        }
    }

    // Si en el futuro agregas más funcionalidades, puedes ir añadiendo métodos al ViewModel
}
