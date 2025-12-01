package com.example.proyectofinalweb.screens

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyectofinalweb.R
import com.example.proyectofinalweb.model.Attachment
import com.example.proyectofinalweb.model.MediaType
import com.example.proyectofinalweb.providers.MiFileProviderMultimedia
import com.example.proyectofinalweb.ui.AppViewModelProvider
import com.example.proyectofinalweb.ui.common.AttachmentGrid
import com.example.proyectofinalweb.ui.note.NoteEntryViewModel
import com.example.proyectofinalweb.ui.note.NoteUiState
import com.google.accompanist.permissions.*
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
    val contentResolver = context.contentResolver

    var cameraPermissionRequested by rememberSaveable { mutableStateOf(false) }
    var audioPermissionRequested by rememberSaveable { mutableStateOf(false) }
    var videoPermissionRequested by rememberSaveable { mutableStateOf(false) }

    var showPermissionDeniedDialog by remember { mutableStateOf(false) }
    var permissionDialogText by remember { mutableStateOf("") }

    var pendingMediaType by remember { mutableStateOf<MediaType?>(null) }

    fun getMediaType(uri: Uri): MediaType {
        val mimeType = contentResolver.getType(uri)
        return when {
            mimeType?.startsWith("image") == true -> MediaType.IMAGE
            mimeType?.startsWith("video") == true -> MediaType.VIDEO
            mimeType?.startsWith("audio") == true -> MediaType.AUDIO
            else -> MediaType.FILE
        }
    }

    val cameraPermissionState = rememberMultiplePermissionsState(listOf(Manifest.permission.CAMERA)) { cameraPermissionRequested = true }
    val recordAudioPermissionState = rememberMultiplePermissionsState(listOf(Manifest.permission.RECORD_AUDIO)) { audioPermissionRequested = true }
    val cameraAndAudioPermissionState = rememberMultiplePermissionsState(listOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)) { videoPermissionRequested = true }

    var imageUri: Uri? by remember { mutableStateOf(null) }
    val imagePickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) {
        if (it) imageUri?.let { uri -> viewModel.addAttachment(Attachment(uri = uri.toString(), type = MediaType.IMAGE)) }
    }

    var videoUri: Uri? by remember { mutableStateOf(null) }
    val videoPickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.CaptureVideo()) {
        if (it) videoUri?.let { uri -> viewModel.addAttachment(Attachment(uri = uri.toString(), type = MediaType.VIDEO)) }
    }

    val filePickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) {
        it?.let {
            try {
                val takeFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                contentResolver.takePersistableUriPermission(it, takeFlags)
                viewModel.addAttachment(Attachment(uri = it.toString(), type = getMediaType(it)))
            } catch (e: SecurityException) {
                e.printStackTrace()
            }
        }
    }

    LaunchedEffect(cameraPermissionState.allPermissionsGranted) {
        if (cameraPermissionState.allPermissionsGranted && pendingMediaType == MediaType.IMAGE) {
            val newImageUri = MiFileProviderMultimedia.getImageUri(context)
            imageUri = newImageUri
            imagePickerLauncher.launch(newImageUri)
            pendingMediaType = null
        }
    }

    LaunchedEffect(recordAudioPermissionState.allPermissionsGranted) {
        if (recordAudioPermissionState.allPermissionsGranted && pendingMediaType == MediaType.AUDIO) {
            if (viewModel.noteUiState.isRecordingAudio) viewModel.stopAudioRecording()
            else viewModel.startAudioRecording()
            pendingMediaType = null
        }
    }

    LaunchedEffect(cameraAndAudioPermissionState.allPermissionsGranted) {
        if (cameraAndAudioPermissionState.allPermissionsGranted && pendingMediaType == MediaType.VIDEO) {
            val newVideoUri = MiFileProviderMultimedia.getVideoUri(context)
            videoUri = newVideoUri
            videoPickerLauncher.launch(newVideoUri)
            pendingMediaType = null
        }
    }

    fun openAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uriValue = Uri.fromParts("package", context.packageName, null)
        intent.data = uriValue
        context.startActivity(intent)
    }

    if (showPermissionDeniedDialog) {
        AlertDialog(
            onDismissRequest = { showPermissionDeniedDialog = false },
            title = { Text("Permiso Requerido") },
            text = { Text(permissionDialogText) },
            confirmButton = {
                Button(onClick = {
                    showPermissionDeniedDialog = false
                    openAppSettings()
                }) { Text("Ir a Ajustes") }
            },
            dismissButton = {
                Button(onClick = { showPermissionDeniedDialog = false }) { Text("Cancelar") }
            }
        )
    }

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
                            val newImageUri = MiFileProviderMultimedia.getImageUri(context)
                            imageUri = newImageUri
                            imagePickerLauncher.launch(newImageUri)
                        } else {
                            if (cameraPermissionRequested && !cameraPermissionState.shouldShowRationale) {
                                permissionDialogText = "Para tomar una foto, la app necesita permiso para usar la cámara. Por favor, activa el permiso en los ajustes."
                                showPermissionDeniedDialog = true
                            } else {
                                pendingMediaType = MediaType.IMAGE
                                cameraPermissionState.launchMultiplePermissionRequest()
                            }
                        }
                    }
                    MediaType.VIDEO -> {
                        if (cameraAndAudioPermissionState.allPermissionsGranted) {
                            val newVideoUri = MiFileProviderMultimedia.getVideoUri(context)
                            videoUri = newVideoUri
                            videoPickerLauncher.launch(newVideoUri)
                        } else {
                            if (videoPermissionRequested && !cameraAndAudioPermissionState.shouldShowRationale) {
                                permissionDialogText = "Para grabar un video, la app necesita permiso para usar la cámara y el micrófono. Por favor, activa los permisos en los ajustes."
                                showPermissionDeniedDialog = true
                            } else {
                                pendingMediaType = MediaType.VIDEO
                                cameraAndAudioPermissionState.launchMultiplePermissionRequest()
                            }
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
                            if (audioPermissionRequested && !recordAudioPermissionState.shouldShowRationale) {
                                permissionDialogText = "Para grabar audio, la app necesita permiso para usar el micrófono. Por favor, activa el permiso en los ajustes."
                                showPermissionDeniedDialog = true
                            } else {
                                pendingMediaType = MediaType.AUDIO
                                recordAudioPermissionState.launchMultiplePermissionRequest()
                            }
                        }
                    }
                    MediaType.FILE -> {
                        filePickerLauncher.launch(arrayOf("*/*"))
                    }
                }
            },
            onAttachmentRemove = viewModel::removeAttachment,
            onAttachmentDescriptionChange = viewModel::updateAttachmentDescription,
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
    onAttachmentDescriptionChange: (Attachment, String) -> Unit,
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
            if (noteUiState.isRecordingAudio) {
                Button(onClick = { onAttachmentAdd(MediaType.AUDIO) }) {
                    Text(text = stringResource(R.string.stop_recording))
                }
            }
        }

        AttachmentGrid(
            attachments = noteUiState.attachments, 
            onAttachmentClick = onAttachmentRemove, 
            onAttachmentDescriptionChange = onAttachmentDescriptionChange,
            isEditing = true
        )
    }
}
