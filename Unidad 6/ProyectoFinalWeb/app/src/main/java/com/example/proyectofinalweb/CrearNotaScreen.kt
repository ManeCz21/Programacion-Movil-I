package com.example.proyectofinalweb.screens

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.util.*

@Composable
fun CrearNotaScreen() {
    // State variables
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var isTask by remember { mutableStateOf(false) } // Nota o Tarea
    var taskTime by remember { mutableStateOf("00:00") }
    var taskDate by remember { mutableStateOf("") }

    // Time Picker Dialog
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    // Mostrar selector de hora
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

    // Mostrar selector de fecha
    fun showDatePickerDialog() {
        val datePickerDialog = DatePickerDialog(
            context,
            { _, year, monthOfYear, dayOfMonth ->
                taskDate = "$dayOfMonth/${monthOfYear + 1}/$year" // Formato: dd/MM/yyyy
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {

        // Title and Description Fields
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

        // Selector de tipo: Nota o Tarea (Radio Buttons)
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

        // Si es Tarea, agregar los campos de fecha y hora
        if (isTask) {
            Spacer(modifier = Modifier.height(20.dp))

            // Selección de la fecha de la tarea
            Text(
                text = if (taskDate.isEmpty()) "Selecciona la fecha" else taskDate,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showDatePickerDialog() }
                    .padding(16.dp),
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Hora de la tarea (Time Picker)
            Text(
                text = "Hora: $taskTime",
                modifier = Modifier
                    .clickable { showTimePickerDialog() }
                    .padding(16.dp),
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Botón de guardar
        Button(onClick = {
            // Guardar nota (o tarea) aquí
            // Este es el lugar para guardar los datos en tu ViewModel
        }) {
            Text("Guardar Nota o Tarea")
        }
    }
}
