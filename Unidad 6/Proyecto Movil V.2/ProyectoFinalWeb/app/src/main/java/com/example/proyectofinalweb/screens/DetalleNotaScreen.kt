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
import com.example.proyectofinalweb.db.NoteDao
import com.example.proyectofinalweb.model.Note
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleNotaScreen(noteId: Int, noteDao: NoteDao, navController: NavController) {
    var note by remember { mutableStateOf<Note?>(null) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(noteId) {
        noteDao.getAllNotes().collect { notes ->
            note = notes.find { it.id == noteId }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle de la Nota") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Regresar")
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate("edit/${note?.id}") }) {
                        Icon(Icons.Default.Edit, contentDescription = "Editar")
                    }
                    IconButton(onClick = {
                        coroutineScope.launch {
                            note?.let { noteDao.deleteNote(it) }
                            navController.popBackStack()
                        }
                    }) {
                        Icon(Icons.Default.Delete, contentDescription = "Eliminar")
                    }
                }
            )
        }
    ) { paddingValues ->
        note?.let { currentNote ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                Text(text = currentNote.title, style = MaterialTheme.typography.headlineLarge)
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = currentNote.description, style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}
