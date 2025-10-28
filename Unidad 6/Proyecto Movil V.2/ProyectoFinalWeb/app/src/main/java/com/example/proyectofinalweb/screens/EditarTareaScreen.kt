package com.example.proyectofinalweb.screens

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.proyectofinalweb.db.TaskDao
import com.example.proyectofinalweb.model.Task
import kotlinx.coroutines.launch
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditarTareaScreen(taskId: Int, taskDao: TaskDao, navController: NavController) {
    var task by remember { mutableStateOf<Task?>(null) }
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    LaunchedEffect(taskId) {
        taskDao.getAllTasks().collect { tasks ->
            task = tasks.find { it.id == taskId }
            task?.let {
                title = it.title
                description = it.description
                date = it.date
                time = it.time
            }
        }
    }

    fun showTimePickerDialog() {
        TimePickerDialog(
            context,
            { _, hourOfDay, minute ->
                time = String.format("%02d:%02d", hourOfDay, minute)
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        ).show()
    }

    fun showDatePickerDialog() {
        val datePickerDialog = DatePickerDialog(
            context,
            { _, year, monthOfYear, dayOfMonth ->
                date = "$dayOfMonth/${monthOfYear + 1}/$year"
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Editar Tarea") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Regresar")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        coroutineScope.launch {
                            task?.let {
                                val updatedTask = it.copy(
                                    title = title,
                                    description = description,
                                    date = date,
                                    time = time
                                )
                                taskDao.updateTask(updatedTask)
                                navController.popBackStack() // Go back to details screen
                                navController.popBackStack() // Go back to home screen
                            }
                        }
                    }) {
                        Icon(Icons.Default.Done, contentDescription = "Guardar")
                    }
                }
            )
        }
    ) { paddingValues ->
        task?.let {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Título") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descripción") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = if (date.isEmpty()) "Selecciona la fecha" else date,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showDatePickerDialog() }
                        .padding(16.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Hora: $time",
                    modifier = Modifier
                        .clickable { showTimePickerDialog() }
                        .padding(16.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
