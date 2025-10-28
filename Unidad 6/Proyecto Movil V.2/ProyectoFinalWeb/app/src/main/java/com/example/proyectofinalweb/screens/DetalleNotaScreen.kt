package com.example.proyectofinalweb.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.proyectofinalweb.db.NoteDao
import com.example.proyectofinalweb.model.Note
import kotlinx.coroutines.launch

@Composable
fun DetalleNotaScreen(noteId: Int, noteDao: NoteDao, navController: NavController) {
    var note by remember { mutableStateOf<Note?>(null) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(noteId) {
        // This is not efficient. A real app would have a better way to get a single note.
        noteDao.getAllNotes().collect { notes ->
            note = notes.find { it.id == noteId }
        }
    }

    note?.let { currentNote ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(text = currentNote.title, style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = currentNote.description, style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                navController.navigate("edit/${currentNote.id}")
            }) {
                Text("Editar")
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = {
                coroutineScope.launch {
                    noteDao.deleteNote(currentNote)
                    navController.popBackStack()
                }
            }) {
                Text("Eliminar")
            }
        }
    }
}
