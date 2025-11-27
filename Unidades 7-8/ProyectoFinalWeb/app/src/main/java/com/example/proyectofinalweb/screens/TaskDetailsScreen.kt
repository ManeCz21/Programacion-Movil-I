package com.example.proyectofinalweb.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import coil.compose.rememberAsyncImagePainter
import com.example.proyectofinalweb.R
import com.example.proyectofinalweb.model.Attachment
import com.example.proyectofinalweb.model.MediaType
import com.example.proyectofinalweb.ui.AppViewModelProvider
import com.example.proyectofinalweb.ui.common.AttachmentGrid
import com.example.proyectofinalweb.ui.task.TaskDetailsViewModel
import com.example.proyectofinalweb.ui.task.TaskUiState
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDetailsScreen(
    navigateToEditTask: (Int) -> Unit,
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TaskDetailsViewModel = viewModel(factory = AppViewModelProvider.Factory),
    taskId: Int? = null
) {
    if (taskId != null) {
        viewModel.setTaskId(taskId)
    }

    val uiState by viewModel.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    var selectedAttachment by remember { mutableStateOf<Attachment?>(null) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.task_details_title)) },
                navigationIcon = {
                    IconButton(onClick = navigateBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = stringResource(R.string.back_button))
                    }
                },
                actions = {
                    IconButton(onClick = { navigateToEditTask(uiState.id) }) {
                        Icon(Icons.Filled.Edit, contentDescription = stringResource(R.string.edit_button))
                    }
                    IconButton(onClick = { showDeleteConfirmation = true }) {
                        Icon(Icons.Filled.Delete, contentDescription = stringResource(R.string.delete_button))
                    }
                }
            )
        }
    ) { innerPadding ->
        TaskDetailsBody(
            taskUiState = uiState,
            onAttachmentClick = { selectedAttachment = it },
            modifier = Modifier.padding(innerPadding)
        )

        if (showDeleteConfirmation) {
            AlertDialog(
                onDismissRequest = { showDeleteConfirmation = false },
                title = { Text(stringResource(R.string.delete_confirmation_title)) },
                text = { Text(stringResource(R.string.delete_task_confirmation_message)) },
                confirmButton = {
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                viewModel.deleteTask()
                                navigateBack()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text(stringResource(R.string.delete_button))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteConfirmation = false }) {
                        Text(stringResource(R.string.cancel_button))
                    }
                }
            )
        }

        selectedAttachment?.let {
            AttachmentViewer(attachment = it, onDismiss = { selectedAttachment = null })
        }
    }
}

@Composable
private fun TaskDetailsBody(
    taskUiState: TaskUiState,
    onAttachmentClick: (Attachment) -> Unit,
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
                    text = stringResource(R.string.title_label),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = taskUiState.title,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold)
                )

                Spacer(modifier = Modifier.height(8.dp))
                Divider()
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = stringResource(R.string.description_label),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = taskUiState.description,
                    style = MaterialTheme.typography.bodyLarge
                )

                if (taskUiState.date.isNotBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Divider()
                    Spacer(modifier = Modifier.height(8.dp))
                    InfoColumn(label = stringResource(R.string.date_label), value = taskUiState.date)
                }

                if (taskUiState.time.isNotBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    InfoColumn(label = stringResource(R.string.time_label), value = taskUiState.time)
                }

                Spacer(modifier = Modifier.height(8.dp))

                val statusText = if (taskUiState.isCompleted) stringResource(R.string.status_completed) else stringResource(R.string.status_pending)
                val statusColor = if (taskUiState.isCompleted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error

                InfoColumn(label = stringResource(R.string.status_label), value = statusText, valueColor = statusColor)
            }
        }

        AttachmentGrid(
            attachments = taskUiState.attachments, 
            onAttachmentClick = onAttachmentClick,
            onAttachmentDescriptionChange = { _, _ -> }
        )
    }
}

@Composable
private fun InfoColumn(label: String, value: String, valueColor: androidx.compose.ui.graphics.Color = Color.Unspecified) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = valueColor.takeIf { it != Color.Unspecified } ?: LocalContentColor.current
        )
    }
}

@Composable
private fun AttachmentViewer(
    attachment: Attachment,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(attachment.type.name) },
        text = {
            when (attachment.type) {
                MediaType.IMAGE -> {
                    Image(
                        painter = rememberAsyncImagePainter(model = attachment.uri),
                        contentDescription = null,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                MediaType.VIDEO, MediaType.AUDIO -> {
                    val context = androidx.compose.ui.platform.LocalContext.current
                    val exoPlayer = remember(context) {
                        ExoPlayer.Builder(context).build().apply {
                            val mediaItem = MediaItem.fromUri(attachment.uri)
                            setMediaItem(mediaItem)
                            prepare()
                            playWhenReady = true
                        }
                    }

                    DisposableEffect(Unit) {
                        onDispose {
                            exoPlayer.release()
                        }
                    }

                    AndroidView(
                        factory = { ctx ->
                            PlayerView(ctx).apply {
                                player = exoPlayer
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                else -> {}
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.close_button))
            }
        }
    )
}
