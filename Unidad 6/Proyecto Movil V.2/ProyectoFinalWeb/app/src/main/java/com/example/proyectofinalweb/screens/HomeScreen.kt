package com.example.proyectofinalweb.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyectofinalweb.R
import com.example.proyectofinalweb.components.NoteItem
import com.example.proyectofinalweb.components.TaskItem
import com.example.proyectofinalweb.model.Task
import com.example.proyectofinalweb.ui.AppViewModelProvider
import com.example.proyectofinalweb.ui.home.HomeUiState
import com.example.proyectofinalweb.ui.home.HomeViewModel
import com.example.proyectofinalweb.util.AppContentType
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    navigateToNoteEntry: () -> Unit,
    navigateToTaskEntry: () -> Unit,
    navigateToNoteUpdate: (Int) -> Unit,
    navigateToTaskUpdate: (Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory),
    contentType: AppContentType
) {
    val homeUiState by viewModel.homeUiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    if (contentType == AppContentType.LIST_AND_DETAIL) {
        Row(modifier = modifier.fillMaxSize()) {
            Box(modifier = Modifier.weight(1f)) {
                HomeContent(
                    navigateToNoteEntry = navigateToNoteEntry,
                    navigateToTaskEntry = navigateToTaskEntry,
                    homeUiState = homeUiState,
                    onNoteClick = { viewModel.setSelectedNote(it) },
                    onTaskClick = { viewModel.setSelectedTask(it) },
                    onTaskCompletedChange = { task, completed ->
                        coroutineScope.launch { viewModel.completeTask(task, completed) }
                    }
                )
            }
            Box(modifier = Modifier.weight(1f)) {
                if (homeUiState.isEditingNote && homeUiState.selectedNote != null) {
                    NoteEditScreen(
                        navigateBack = { viewModel.onBackFromEdit() },
                        noteId = homeUiState.selectedNote!!.id
                    )
                } else if (homeUiState.isEditingTask && homeUiState.selectedTask != null) {
                    TaskEditScreen(
                        navigateBack = { viewModel.onBackFromEdit() },
                        taskId = homeUiState.selectedTask!!.id
                    )
                } else if (homeUiState.selectedNote != null) {
                    NoteDetailsScreen(
                        navigateToEditNote = { viewModel.onEditNote() },
                        navigateBack = { viewModel.closeDetailScreen() },
                        noteId = homeUiState.selectedNote!!.id
                    )
                } else if (homeUiState.selectedTask != null) {
                    TaskDetailsScreen(
                        navigateToEditTask = { viewModel.onEditTask() },
                        navigateBack = { viewModel.closeDetailScreen() },
                        taskId = homeUiState.selectedTask!!.id
                    )
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(R.string.select_message),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
                BackHandler {
                    if (homeUiState.isEditingNote || homeUiState.isEditingTask) {
                        viewModel.onBackFromEdit()
                    } else {
                        viewModel.closeDetailScreen()
                    }
                }
            }
        }
    } else {
        HomeContent(
            navigateToNoteEntry = navigateToNoteEntry,
            navigateToTaskEntry = navigateToTaskEntry,
            homeUiState = homeUiState,
            onNoteClick = navigateToNoteUpdate,
            onTaskClick = navigateToTaskUpdate,
            onTaskCompletedChange = { task, completed ->
                coroutineScope.launch { viewModel.completeTask(task, completed) }
            },
            modifier = modifier
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
private fun HomeContent(
    navigateToNoteEntry: () -> Unit,
    navigateToTaskEntry: () -> Unit,
    homeUiState: HomeUiState,
    onNoteClick: (Int) -> Unit,
    onTaskClick: (Int) -> Unit,
    onTaskCompletedChange: (Task, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    var showMenu by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(title = { Text(stringResource(R.string.home_title)) })
        },
        floatingActionButton = {
            Box {
                FloatingActionButton(
                    onClick = { showMenu = !showMenu },
                    containerColor = MaterialTheme.colorScheme.primary,
                ) {
                    Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.add_button_description))
                }
                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.create_note)) },
                        onClick = {
                            showMenu = false
                            navigateToNoteEntry()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.create_task)) },
                        onClick = {
                            showMenu = false
                            navigateToTaskEntry()
                        }
                    )
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            contentPadding = paddingValues,
            modifier = Modifier.fillMaxSize()
        ) {
            stickyHeader { Text(stringResource(R.string.tasks_header), style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(16.dp)) }
            if (homeUiState.taskList.isEmpty()) {
                item { Text(text = stringResource(R.string.no_tasks), modifier = Modifier.padding(16.dp)) }
            } else {
                items(homeUiState.taskList) { task ->
                    TaskItem(
                        task = task,
                        onTaskCompletedChange = { onTaskCompletedChange(task, it.isCompleted) },
                        onTaskClick = { onTaskClick(task.id) }
                    )
                }
            }

            stickyHeader { Text(stringResource(R.string.notes_header), style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(16.dp)) }
            if (homeUiState.noteList.isEmpty()) {
                item { Text(text = stringResource(R.string.no_notes), modifier = Modifier.padding(16.dp)) }
            } else {
                items(homeUiState.noteList) { note ->
                    NoteItem(note = note, onClick = { onNoteClick(note.id) })
                }
            }
        }
    }
}
