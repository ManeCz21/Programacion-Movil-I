package com.example.proyectofinalweb.ui.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyectofinalweb.components.NoteItem
import com.example.proyectofinalweb.components.TaskItem
import com.example.proyectofinalweb.ui.AppViewModelProvider
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    navigateToNoteEntry: () -> Unit,
    navigateToNoteUpdate: (Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val homeUiState by viewModel.homeUiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(title = { Text("Notas y Tareas") })
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = navigateToNoteEntry,
                containerColor = MaterialTheme.colorScheme.primary,
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar")
            }
        }
    ) { paddingValues ->
        LazyColumn(
            contentPadding = paddingValues,
            modifier = Modifier.fillMaxSize()
        ) {
            stickyHeader {
                Text(
                    text = "Tareas",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(16.dp)
                )
            }
            if (homeUiState.taskList.isEmpty()) {
                item { Text(text = "No hay tareas.", modifier = Modifier.padding(16.dp)) }
            } else {
                items(homeUiState.taskList) { task ->
                    TaskItem(
                        task = task,
                        onTaskCompletedChange = { coroutineScope.launch { viewModel.completeTask(task, it.isCompleted) } },
                        onTaskClick = { /* TODO */ }
                    )
                }
            }

            stickyHeader {
                Text(
                    text = "Notas",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(16.dp)
                )
            }
            if (homeUiState.noteList.isEmpty()) {
                item { Text(text = "No hay notas.", modifier = Modifier.padding(16.dp)) }
            } else {
                items(homeUiState.noteList) { note ->
                    NoteItem(note = note, onClick = { navigateToNoteUpdate(note.id) })
                }
            }
        }
    }
}
