package com.example.proyectofinalweb

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyectofinalweb.model.Task
import com.example.proyectofinalweb.viewmodel.TaskViewModel
import java.util.*
import androidx.compose.ui.res.stringResource

@Composable
fun CrearNotaScreen() {
    val viewModel: TaskViewModel = viewModel()

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var isTask by remember { mutableStateOf(true) }
    var taskTime by remember { mutableStateOf("00:00") }
    var taskDate by remember { mutableStateOf("") }

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

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {

        // Título de la pantalla
        Text(
            text = stringResource(id = R.string.create_task),
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text(stringResource(id = R.string.task_title)) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text(stringResource(id = R.string.task_description)) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Selector de tipo: Nota o Tarea
        Text(stringResource(id = R.string.select_type))
        Row {
            RadioButton(
                selected = !isTask,
                onClick = { isTask = false }
            )
            Text(stringResource(id = R.string.note), modifier = Modifier.padding(start = 4.dp))

            Spacer(modifier = Modifier.width(16.dp))

            RadioButton(
                selected = isTask,
                onClick = { isTask = true }
            )
            Text(stringResource(id = R.string.task), modifier = Modifier.padding(start = 4.dp))
        }

        if (isTask) {
            Spacer(modifier = Modifier.height(20.dp))

            // Selector de fecha
            Text(
                text = if (taskDate.isEmpty()) stringResource(id = R.string.select_date) else taskDate,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showDatePickerDialog() }
                    .padding(16.dp),
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Selector de hora
            Text(
                text = "${stringResource(id = R.string.select_time)}: $taskTime",
                modifier = Modifier
                    .clickable { showTimePickerDialog() }
                    .padding(16.dp),
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(onClick = {
            val task = Task(
                title = title,
                description = description,
                isTask = isTask,
                taskTime = taskTime,
                taskDate = taskDate
            )
            viewModel.addTask(task)
        }) {
            Text(stringResource(id = R.string.save_task))
        }
    }
}

