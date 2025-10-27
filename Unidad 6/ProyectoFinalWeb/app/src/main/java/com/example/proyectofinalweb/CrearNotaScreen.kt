package com.example.proyectofinalweb

import android.app.TimePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import java.util.*

import com.example.proyectofinalweb.viewmodel.CrearNotaViewModel

@Composable
fun CrearNotaScreen() {
    // Obtener el ViewModel
    val viewModel: CrearNotaViewModel = viewModel()

    // State variables desde el ViewModel
    val title by viewModel.title
    val description by viewModel.description
    val isTask by viewModel.isTask
    val taskTime by viewModel.taskTime
    val taskDate by viewModel.taskDate

    // Time Picker Dialog
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    fun showTimePickerDialog() {
        TimePickerDialog(
            context,
            { _, hourOfDay, minute ->
                val formattedTime = String.format("%02d:%02d", hourOfDay, minute)
                viewModel.onTaskTimeChange(formattedTime)
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        ).show()
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {

        // Título y Descripción
        Text(
            text = "Crear Nueva Nota o Tarea",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = title,
            onValueChange = { viewModel.onTitleChange(it) },
            label = { Text("Título") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = description,
            onValueChange = { viewModel.onDescriptionChange(it) },
            label = { Text("Descripción") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Selector de tipo: Nota o Tarea (Radio Buttons)
        Text("Tipo de nota:")
        Row {
            RadioButton(
                selected = !isTask,
                onClick = { viewModel.onTaskTypeChange(false) }
            )
            Text("Nota", modifier = Modifier.padding(start = 4.dp))

            Spacer(modifier = Modifier.width(16.dp))

            RadioButton(
                selected = isTask,
                onClick = { viewModel.onTaskTypeChange(true) }
            )
            Text("Tarea", modifier = Modifier.padding(start = 4.dp))
        }

        // Si es Tarea, mostrar los campos de fecha y hora
        if (isTask) {
            Spacer(modifier = Modifier.height(20.dp))

            // Fecha de la tarea
            OutlinedTextField(
                value = taskDate,
                onValueChange = { viewModel.onTaskDateChange(it) },
                label = { Text("Fecha de Tarea") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Hora de la tarea (Time Picker)
            Text(text = "Hora: $taskTime", modifier = Modifier.clickable { showTimePickerDialog() })

            Spacer(modifier = Modifier.height(10.dp))
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Botón de guardar
        Button(onClick = {
            // Aquí puedes agregar la lógica para guardar la tarea o nota en el repositorio
            // Para esta fase, solo mostramos un mensaje
            println("Nota o Tarea guardada")
        }) {
            Text("Guardar Nota o Tarea")
        }
    }
}
