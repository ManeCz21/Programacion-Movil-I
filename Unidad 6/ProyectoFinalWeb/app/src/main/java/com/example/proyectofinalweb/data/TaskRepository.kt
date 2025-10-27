package com.example.proyectofinalweb.data

import com.example.proyectofinalweb.model.Task

class TaskRepository(private val taskDao: TaskDao) {

    suspend fun saveTask(task: Task) {
        taskDao.insertTask(task)
    }

    // Métodos adicionales (editar, eliminar, listar) se agregarían aquí si es necesario
}
