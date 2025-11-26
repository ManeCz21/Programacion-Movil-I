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
    var selectedAttachment by remember { mutableStateOf<Attachment?>(null) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.note_details_title)) },
                navigationIcon = {
                    IconButton(onClick = navigateBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = stringResource(R.string.back_button))
                    }
                },
                actions = {
                    IconButton(onClick = { navigateToEditNote(uiState.id) }) {
                        Icon(Icons.Filled.Edit, contentDescription = stringResource(R.string.edit_button))
                    }
                    IconButton(onClick = { showDeleteConfirmation = true }) {
                        Icon(Icons.Filled.Delete, contentDescription = stringResource(R.string.delete_button))
                    }
                }
            )
        }
    ) { innerPadding ->
        NoteDetailsBody(
            noteUiState = uiState,
            onAttachmentClick = { selectedAttachment = it },
            modifier = Modifier.padding(innerPadding)
        )

        if (showDeleteConfirmation) {
            AlertDialog(
                onDismissRequest = { showDeleteConfirmation = false },
                title = { Text(stringResource(R.string.delete_confirmation_title)) },
                text = { Text(stringResource(R.string.delete_note_confirmation_message)) },
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
private fun NoteDetailsBody(
    noteUiState: NoteUiState,
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
                    text = noteUiState.title,
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
                    text = noteUiState.description,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }

        AttachmentGrid(attachments = noteUiState.attachments, onAttachmentClick = onAttachmentClick)
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