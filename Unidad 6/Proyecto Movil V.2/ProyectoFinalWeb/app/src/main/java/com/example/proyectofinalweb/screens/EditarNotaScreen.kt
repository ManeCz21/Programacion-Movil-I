package com.example.proyectofinalweb.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.proyectofinalweb.db.NoteDao
import com.example.proyectofinalweb.model.Note
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditarNotaScreen(noteId: Int, noteDao: NoteDao, navController: NavController) {
    var note by remember { mutableStateOf<Note?>(null) }
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(noteId) {
        noteDao.getAllNotes().collect { notes ->
            note = notes.find { it.id == noteId }
            note?.let {
                title = it.title
                description = it.description
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Editar Nota") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Regresar")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        coroutineScope.launch {
                            note?.let {
                                val updatedNote = it.copy(title = title, description = description)
                                noteDao.updateNote(updatedNote)
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
        note?.let {
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
            }
        }
    }
}
