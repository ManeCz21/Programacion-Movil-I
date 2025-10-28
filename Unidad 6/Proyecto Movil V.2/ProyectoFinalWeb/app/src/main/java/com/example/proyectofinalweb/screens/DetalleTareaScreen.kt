package com.example.proyectofinalweb.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.proyectofinalweb.db.TaskDao
import com.example.proyectofinalweb.model.Task
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleTareaScreen(taskId: Int, taskDao: TaskDao, navController: NavController) {
    var task by remember { mutableStateOf<Task?>(null) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(taskId) {
        taskDao.getAllTasks().collect { tasks ->
            task = tasks.find { it.id == taskId }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle de la Tarea") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Regresar")
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate("editTask/${task?.id}") }) {
                        Icon(Icons.Default.Edit, contentDescription = "Editar")
                    }
                    IconButton(onClick = {
                        coroutineScope.launch {
                            task?.let { taskDao.deleteTask(it) }
                            navController.popBackStack()
                        }
                    }) {
                        Icon(Icons.Default.Delete, contentDescription = "Eliminar")
                    }
                }
            )
        }
    ) { paddingValues ->
        task?.let { currentTask ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                Text(text = currentTask.title, style = MaterialTheme.typography.headlineLarge)
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = currentTask.description, style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "Fecha: ${currentTask.date}", style = MaterialTheme.typography.bodyMedium)
                Text(text = "Hora: ${currentTask.time}", style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = if (currentTask.isCompleted) "Estado: Completada" else "Estado: Pendiente",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
