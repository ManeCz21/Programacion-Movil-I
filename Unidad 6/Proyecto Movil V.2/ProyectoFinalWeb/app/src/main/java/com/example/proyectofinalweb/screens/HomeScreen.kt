package com.example.proyectofinalweb.screens

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
import androidx.navigation.NavController
import com.example.proyectofinalweb.components.NoteItem
import com.example.proyectofinalweb.components.TaskItem
import com.example.proyectofinalweb.db.NoteDao
import com.example.proyectofinalweb.db.TaskDao
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(navController: NavController, noteDao: NoteDao, taskDao: TaskDao) {
    val notes by noteDao.getAllNotes().collectAsState(initial = emptyList())
    val tasks by taskDao.getAllTasks().collectAsState(initial = emptyList())
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Notas y Tareas") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("add") },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
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
                    modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp)
                )
            }
            if (tasks.isEmpty()) {
                item {
                    Text(
                        text = "No hay tareas.",
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
            items(tasks) { task ->
                TaskItem(
                    task = task,
                    onTaskCompletedChange = {
                        coroutineScope.launch {
                            taskDao.updateTask(it)
                        }
                    },
                    onTaskClick = {
                        navController.navigate("taskDetail/${task.id}")
                    }
                )
            }

            stickyHeader {
                Text(
                    text = "Notas",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp)
                )
            }
            if (notes.isEmpty()) {
                item {
                    Text(
                        text = "No hay notas.",
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
            items(notes) { note ->
                NoteItem(note = note) {
                    navController.navigate("detail/${note.id}")
                }
            }
        }
    }
}
