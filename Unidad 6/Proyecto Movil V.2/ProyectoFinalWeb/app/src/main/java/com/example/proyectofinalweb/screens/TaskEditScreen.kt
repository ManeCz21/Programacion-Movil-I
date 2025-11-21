package com.example.proyectofinalweb.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyectofinalweb.R
import com.example.proyectofinalweb.ui.AppViewModelProvider
import com.example.proyectofinalweb.ui.task.TaskEditViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskEditScreen(
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TaskEditViewModel = viewModel(factory = AppViewModelProvider.Factory),
    taskId: Int? = null // Add taskId parameter
) {
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(taskId) {
        taskId?.let { viewModel.initialize(it) }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.edit_task_title)) },
                navigationIcon = {
                    IconButton(onClick = navigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back_button))
                    }
                },
                actions = {
                    IconButton(onClick = {
                        coroutineScope.launch {
                            viewModel.updateTask()
                            navigateBack()
                        }
                    }) {
                        Icon(Icons.Filled.Done, contentDescription = stringResource(R.string.save_button))
                    }
                }
            )
        }
    ) { innerPadding ->
        TaskEntryBody(
            taskUiState = viewModel.taskUiState,
            onTaskValueChange = viewModel::updateUiState,
            modifier = Modifier.padding(innerPadding)
        )
    }
}
