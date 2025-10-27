package com.example.proyectofinalweb.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.proyectofinalweb.model.Note
import com.example.proyectofinalweb.viewmodel.NotaViewModel

@Composable
fun DetalleNotaScreen(noteId: Int, navController: NavController, viewModel: NotaViewModel) {
    var note by remember { mutableStateOf<Note?>(null) }
    var editing by remember { mutableStateOf(false) }
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    // 🔹 Carga la nota desde el ViewModel por ID
    LaunchedEffect(noteId) {
        viewModel.getNoteById(noteId) { n ->
            note = n
            n?.let {
                title = it.title
                description = it.description
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        note?.let { n ->
            if (!editing) {
                Text(text = n.title, style = MaterialTheme.typography.headlineSmall)
                Spacer(Modifier.height(8.dp))
                Text(text = n.description)
                Spacer(Modifier.height(12.dp))

                // Si la nota tiene imagen o multimedia, mostrarla
                n.mediaUrisJson?.let { uri ->
                    Spacer(modifier = Modifier.height(8.dp))
                    AsyncImage(
                        model = uri,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                    )
                }

                Spacer(Modifier.height(16.dp))
                Row {
                    Button(onClick = { editing = true }) {
                        Text("Editar")
                    }
                    Spacer(Modifier.width(8.dp))
                    Button(
                        onClick = {
                            viewModel.deleteNote(n) {
                                navController.popBackStack()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.error)
                    ) {
                        Text("Eliminar", color = MaterialTheme.colorScheme.onError)
                    }
                    Spacer(Modifier.width(8.dp))
                    Button(onClick = { navController.popBackStack() }) {
                        Text("Regresar")
                    }
                }
            } else {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Título") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descripción") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 6
                )
                Spacer(Modifier.height(12.dp))
                Row {
                    Button(onClick = {
                        val updated = n.copy(title = title, description = description)
                        viewModel.updateNote(updated) {
                            editing = false
                        }
                    }) {
                        Text("Guardar")
                    }
                    Spacer(Modifier.width(8.dp))
                    Button(onClick = { editing = false }) {
                        Text("Cancelar")
                    }
                }
            }
        } ?: run {
            Text("Cargando nota...")
        }
    }
}
