package com.example.proyectofinalweb.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyectofinalweb.ui.AppViewModelProvider
import com.example.proyectofinalweb.ui.note.NoteDetailsViewModel
import com.example.proyectofinalweb.ui.note.NoteUiState
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteDetailsScreen(
    navigateToEditNote: (Int) -> Unit,
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: NoteDetailsViewModel = viewModel(factory = AppViewModelProvider.Factory),
    noteId: Int? = null
) {
    if (noteId != null) {
        viewModel.setNoteId(noteId)
    }

    val uiState by viewModel.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    var showDeleteConfirmation by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Detalles de la Nota") },
                navigationIcon = {
                    IconButton(onClick = navigateBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                },
                actions = {
                    IconButton(onClick = { navigateToEditNote(uiState.id) }) {
                        Icon(Icons.Filled.Edit, contentDescription = "Editar")
                    }
                    IconButton(onClick = { showDeleteConfirmation = true }) {
                        Icon(Icons.Filled.Delete, contentDescription = "Eliminar")
                    }
                }
            )
        }
    ) { innerPadding ->
        NoteDetailsBody(
            noteUiState = uiState,
            modifier = Modifier.padding(innerPadding)
        )

        if (showDeleteConfirmation) {
            AlertDialog(
                onDismissRequest = { showDeleteConfirmation = false },
                title = { Text("Confirmar eliminación") },
                text = { Text("¿Estás seguro de que quieres eliminar esta nota?") },
                confirmButton = {
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                viewModel.deleteNote()
                                navigateBack()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text("Eliminar")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteConfirmation = false }) {
                        Text("Cancelar")
                    }
                }
            )
        }
    }
}

@Composable
private fun NoteDetailsBody(
    noteUiState: NoteUiState,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OutlinedCard(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Título",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = noteUiState.title,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold)
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Divider()

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Descripción",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = noteUiState.description,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}
