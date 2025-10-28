package com.example.proyectofinalweb.screens

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.proyectofinalweb.db.NoteDao
import com.example.proyectofinalweb.db.TaskDao
import com.example.proyectofinalweb.model.Note
import com.example.proyectofinalweb.model.Task
import kotlinx.coroutines.launch
import java.util.*

@Composable
fun CrearNotaScreen(noteDao: NoteDao, taskDao: TaskDao, navController: NavController) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var isTask by remember { mutableStateOf(false) }
    var taskTime by remember { mutableStateOf("00:00") }
    var taskDate by remember { mutableStateOf("") }

    val coroutineScope = rememberCoroutineScope()

    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    fun showTimePickerDialog() {
        TimePickerDialog(
            context,
            { _, hourOfDay, minute ->
                taskTime = String.format("%02d:%02d", hourOfDay, minute)
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
                taskDate = "$dayOfMonth/${monthOfYear + 1}/$year"
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            text = "Crear Nueva Nota o Tarea",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Título") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Descripción") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text("Seleccionar Tipo:")
        Row {
            RadioButton(
                selected = !isTask,
                onClick = { isTask = false }
            )
            Text("Nota", modifier = Modifier.padding(start = 4.dp))

            Spacer(modifier = Modifier.width(16.dp))

            RadioButton(
                selected = isTask,
                onClick = { isTask = true }
            )
            Text("Tarea", modifier = Modifier.padding(start = 4.dp))
        }

        if (isTask) {
            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = if (taskDate.isEmpty()) "Selecciona la fecha" else taskDate,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showDatePickerDialog() }
                    .padding(16.dp),
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Hora: $taskTime",
                modifier = Modifier
                    .clickable { showTimePickerDialog() }
                    .padding(16.dp),
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(onClick = {
            coroutineScope.launch {
                if (isTask) {
                    val task = Task(
                        title = title,
                        description = description,
                        date = taskDate,
                        time = taskTime
                    )
                    taskDao.insertTask(task)
                } else {
                    val note = Note(
                        title = title,
                        description = description,
                        type = com.example.proyectofinalweb.model.NoteType.NOTE // This is not needed anymore, but let's keep it for now
                    )
                    noteDao.insertNote(note)
                }
                navController.popBackStack()
            }
        }) {
            Text("Guardar")
        }
    }
}
