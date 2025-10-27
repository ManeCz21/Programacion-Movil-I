package com.example.proyectofinalweb.screens

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
    var notificationDate by remember { mutableStateOf("") }
    var notificationTime by remember { mutableStateOf("00:00") }
    var selectedUris by remember { mutableStateOf<List<Uri>>(emptyList()) }

    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    val gson = Gson()

    // --- Date and Time pickers ---
    fun showDatePickerDialog() {
        DatePickerDialog(
            context,
            { _, year, month, day ->
                notificationDate = "$day/${month + 1}/$year"
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
                notificationTime = String.format("%02d:%02d", hourOfDay, minute)
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calendar.set(Calendar.MINUTE, minute)
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
            text = stringResource(id = R.string.create_note),
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

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

        // --- Select multimedia ---
        Button(onClick = { pickMediaLauncher.launch(arrayOf("image/*", "video/*")) }) {
            Text("Agregar multimedia")
        }

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

        // --- Date and Time for notification ---
        Text(
            text = if (notificationDate.isEmpty())
                stringResource(id = R.string.select_date)
            else
                "Fecha: $notificationDate",
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showDatePickerDialog() }
                .padding(8.dp)
        )

        Text(
            text = "${stringResource(id = R.string.select_time)}: $notificationTime",
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showTimePickerDialog() }
                .padding(8.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // --- Save button ---
        Button(
            onClick = {
                if (title.isNotBlank() && description.isNotBlank()) {
                    val notificationMillis = try {
                        if (notificationDate.isNotEmpty()) {
                            val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                            sdf.parse("$notificationDate $notificationTime")?.time
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

                    viewModel.addNote(newNote)
                    navController.popBackStack()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(id = R.string.save_note))
        }
    }
}
