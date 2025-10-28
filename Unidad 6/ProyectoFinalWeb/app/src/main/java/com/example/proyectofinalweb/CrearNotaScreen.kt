package com.example.proyectofinalweb

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.proyectofinalweb.R
import com.example.proyectofinalweb.model.Note
import com.example.proyectofinalweb.viewmodel.NotaViewModel
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun CrearNotaScreen(navController: NavController, viewModel: NotaViewModel) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var isTask by remember { mutableStateOf(false) } // Nota o Tarea
    var taskTime by remember { mutableStateOf("00:00") }
    var taskDate by remember { mutableStateOf("") }
    var selectedUris by remember { mutableStateOf<List<Uri>>(emptyList()) }

    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    val gson = Gson()

    // --- Date and Time pickers ---
    fun showDatePickerDialog() {
        DatePickerDialog(
            context,
            { _, year, month, day ->
                taskDate = "$day/${month + 1}/$year"
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, day)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

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

    // --- Multimedia picker ---
    val pickMediaLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenMultipleDocuments()
    ) { uris: List<Uri> ->
        if (uris.isNotEmpty()) {
            uris.forEach { uri ->
                context.contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            }
            selectedUris = uris
        }
    }

    // --- UI Layout ---
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = stringResource(id = R.string.create_task),
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Título y descripción
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
            modifier = Modifier.fillMaxWidth(),
            maxLines = 4
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Selector de tipo: Nota o Tarea (Radio Buttons)
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

        // Si es Tarea, agregar los campos de fecha y hora
        if (isTask) {
            Spacer(modifier = Modifier.height(20.dp))

            // Selección de la fecha de la tarea
            Text(
                text = if (taskDate.isEmpty()) stringResource(id = R.string.select_date) else taskDate,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showDatePickerDialog() }
                    .padding(16.dp),
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Hora de la tarea (Time Picker)
            Text(
                text = "${stringResource(id = R.string.select_time)}: $taskTime",
                modifier = Modifier
                    .clickable { showTimePickerDialog() }
                    .padding(16.dp),
                style = MaterialTheme.typography.bodyMedium
            )
        }

        // --- Selección de multimedia (imágenes o videos) ---
        Spacer(modifier = Modifier.height(20.dp))
        // --- Select multimedia ---
        Row(
            modifier = Modifier
                .fillMaxWidth()  // Toma el ancho completo
                .padding(16.dp),  // Añade un padding adicional
            horizontalArrangement = Arrangement.Center  // Centra el botón dentro del Row
        ) {
            Button(
                onClick = { pickMediaLauncher.launch(arrayOf("image/", "video/")) }
            ) {
                Text(stringResource(id = R.string.add_multimedia))
            }
        }

        // Si hay archivos seleccionados, mostrar las miniaturas
        if (selectedUris.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(selectedUris) { uri ->
                    Image(
                        painter = rememberAsyncImagePainter(uri),
                        contentDescription = null,
                        modifier = Modifier.size(100.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Botón de guardar
        Button(
            onClick = {
                if (title.isNotBlank() && description.isNotBlank()) {
                    val notificationMillis = try {
                        if (taskDate.isNotEmpty()) {
                            val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                            sdf.parse("$taskDate $taskTime")?.time
                        } else null
                    } catch (e: Exception) {
                        null
                    }

                    val mediaJson = gson.toJson(selectedUris.map { it.toString() })

                    val newNote = Note(
                        title = title,
                        description = description,
                        date = System.currentTimeMillis(),
                        mediaUrisJson = mediaJson,
                        notificationTime = notificationMillis
                    )

                    // Llamar al ViewModel para agregar la nota o tarea
                    viewModel.addNote(newNote)
                    navController.popBackStack() // Regresar a la pantalla anterior
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(id = R.string.save_task))
        }
    }
}
