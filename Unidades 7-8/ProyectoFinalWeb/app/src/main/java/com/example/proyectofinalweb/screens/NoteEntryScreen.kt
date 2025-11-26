package com.example.proyectofinalweb.screens

import android.Manifest
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyectofinalweb.R
import com.example.proyectofinalweb.model.Attachment
import com.example.proyectofinalweb.model.MediaType
import com.example.proyectofinalweb.ui.AppViewModelProvider
import com.example.proyectofinalweb.ui.common.AttachmentGrid
import com.example.proyectofinalweb.ui.note.NoteEntryViewModel
import com.example.proyectofinalweb.ui.note.NoteUiState
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun NoteEntryScreen(
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: NoteEntryViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    val cameraPermissionState = rememberMultiplePermissionsState(
        listOf(Manifest.permission.CAMERA)
    )
    val recordAudioPermissionState = rememberMultiplePermissionsState(
        listOf(Manifest.permission.RECORD_AUDIO)
    )

    var imageUri: Uri? by remember { mutableStateOf(null) }
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->
            if (success) {
                val currentImageUri = imageUri
                currentImageUri?.let { viewModel.addAttachment(Attachment(uri = it.toString(), type = MediaType.IMAGE)) }
            }
        }
    )

    var videoUri: Uri? by remember { mutableStateOf(null) }
    val videoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CaptureVideo(),
        onResult = { success ->
            if (success) {
                val currentVideoUri = videoUri
                currentVideoUri?.let { viewModel.addAttachment(Attachment(uri = it.toString(), type = MediaType.VIDEO)) }
            }
        }
    )

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            uri?.let { viewModel.addAttachment(Attachment(uri = it.toString(), type = MediaType.FILE)) }
        }
    )

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.create_note)) },
                navigationIcon = {
                    IconButton(onClick = navigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back_button))
                    }
                },
                actions = {
                    IconButton(onClick = {
                        coroutineScope.launch {
                            viewModel.saveNote()
                            navigateBack()
                        }
                    }) {
                        Icon(Icons.Filled.Done, contentDescription = stringResource(R.string.save_button))
                    }
                }
            )
        }
    ) { innerPadding ->
        NoteEntryBody(
            noteUiState = viewModel.noteUiState,
            onNoteValueChange = viewModel::updateUiState,
            onAttachmentAdd = { mediaType ->
                when (mediaType) {
                    MediaType.IMAGE -> {
                        if (cameraPermissionState.allPermissionsGranted) {
                            val newImageUri = createImageUri(context)
                            imageUri = newImageUri
                            imagePickerLauncher.launch(newImageUri)
                        } else {
                            cameraPermissionState.launchMultiplePermissionRequest()
                        }
                    }
                    MediaType.VIDEO -> {
                        if (cameraPermissionState.allPermissionsGranted) {
                            val newVideoUri = createVideoUri(context)
                            videoUri = newVideoUri
                            videoPickerLauncher.launch(newVideoUri)
                        } else {
                            cameraPermissionState.launchMultiplePermissionRequest()
                        }
                    }
                    MediaType.AUDIO -> {
                        if (recordAudioPermissionState.allPermissionsGranted) {
                            if (viewModel.noteUiState.isRecordingAudio) {
                                viewModel.stopAudioRecording()
                            } else {
                                viewModel.startAudioRecording()
                            }
                        } else {
                            recordAudioPermissionState.launchMultiplePermissionRequest()
                        }
                    }
                    MediaType.FILE -> {
                        filePickerLauncher.launch("*/*")
                    }
                }
            },
            onAttachmentRemove = viewModel::removeAttachment,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
fun NoteEntryBody(
    noteUiState: NoteUiState,
    onNoteValueChange: (NoteUiState) -> Unit,
    onAttachmentAdd: (MediaType) -> Unit,
    onAttachmentRemove: (Attachment) -> Unit,
    modifier: Modifier = Modifier
) {
    var showMenu by remember { mutableStateOf(false) }

    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OutlinedTextField(
            value = noteUiState.title,
            onValueChange = { onNoteValueChange(noteUiState.copy(title = it)) },
            label = { Text(stringResource(R.string.title_label)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        OutlinedTextField(
            value = noteUiState.description,
            onValueChange = { onNoteValueChange(noteUiState.copy(description = it)) },
            label = { Text(stringResource(R.string.description_label)) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { showMenu = true }) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.add_attachment_button))
                Text(text = stringResource(R.string.add_attachment_button))
            }
            DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                DropdownMenuItem(text = { Text(stringResource(R.string.take_photo)) }, onClick = { onAttachmentAdd(MediaType.IMAGE); showMenu = false })
                DropdownMenuItem(text = { Text(stringResource(R.string.record_video)) }, onClick = { onAttachmentAdd(MediaType.VIDEO); showMenu = false })
                DropdownMenuItem(
                    text = { Text(if (noteUiState.isRecordingAudio) stringResource(R.string.stop_recording) else stringResource(R.string.record_audio)) },
                    onClick = { onAttachmentAdd(MediaType.AUDIO); showMenu = false }
                )
                DropdownMenuItem(text = { Text(stringResource(R.string.attach_file)) }, onClick = { onAttachmentAdd(MediaType.FILE); showMenu = false })
            }
        }

        AttachmentGrid(attachments = noteUiState.attachments, onAttachmentClick = onAttachmentRemove)
    }
}
