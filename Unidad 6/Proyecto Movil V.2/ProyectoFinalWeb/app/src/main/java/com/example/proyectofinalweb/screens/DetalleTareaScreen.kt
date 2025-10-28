package com.example.proyectofinalweb.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.proyectofinalweb.db.TaskDao
import com.example.proyectofinalweb.model.Task
import kotlinx.coroutines.launch

@Composable
fun DetalleTareaScreen(taskId: Int, taskDao: TaskDao, navController: NavController) {
    var task by remember { mutableStateOf<Task?>(null) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(taskId) {
        taskDao.getAllTasks().collect { tasks ->
            task = tasks.find { it.id == taskId }
        }
    }

    task?.let { currentTask ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(text = currentTask.title, style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = currentTask.description, style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Fecha: ${currentTask.date}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Hora: ${currentTask.time}", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = if (currentTask.isCompleted) "Estado: Completada" else "Estado: Pendiente",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                // Navigate to edit screen
            }) {
                Text("Editar")
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = {
                    coroutineScope.launch {
                        taskDao.deleteTask(currentTask)
                        navController.popBackStack()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Eliminar")
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = {
                navController.popBackStack()
            }) {
                Text("Regresar")
            }
        }
    }
}
