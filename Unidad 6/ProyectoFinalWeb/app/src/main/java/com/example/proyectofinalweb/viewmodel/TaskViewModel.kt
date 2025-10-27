package com.example.proyectofinalweb.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectofinalweb.data.TaskDatabase
import com.example.proyectofinalweb.model.Task
import com.example.proyectofinalweb.data.TaskRepository
import kotlinx.coroutines.launch

class TaskViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TaskRepository

    init {
        // Obtener DAO a partir de la base de datos
        val taskDao = TaskDatabase.getDatabase(application).taskDao()
        repository = TaskRepository(taskDao)
    }

    // Función para agregar la tarea
    fun addTask(task: Task) {
        viewModelScope.launch {
            repository.saveTask(task)
        }
    }
}
