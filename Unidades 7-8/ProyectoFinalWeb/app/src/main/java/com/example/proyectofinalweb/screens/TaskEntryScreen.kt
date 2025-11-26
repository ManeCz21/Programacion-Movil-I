package com.example.proyectofinalweb.screens

import android.Manifest
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
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
import com.example.proyectofinalweb.ui.task.TaskEntryViewModel
import com.example.proyectofinalweb.ui.task.TaskUiState
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import kotlinx.coroutines.launch
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun TaskEntryScreen(
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TaskEntryViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val contentResolver = context.contentResolver

    fun getMediaType(uri: Uri): MediaType {
        val mimeType = contentResolver.getType(uri)
        return when {
            mimeType?.startsWith("image") == true -> MediaType.IMAGE
            mimeType?.startsWith("video") == true -> MediaType.VIDEO
            mimeType?.startsWith("audio") == true -> MediaType.AUDIO
            else -> MediaType.FILE
        }
    }

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
        contract = ActivityResultContracts.OpenDocument(),
        onResult = { uri: Uri? ->
            uri?.let {
                try {
                    val takeFlags: Int = Intent.FLAG_GRANT_READ_URI_PERMISSION or
                            Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                    contentResolver.takePersistableUriPermission(it, takeFlags)
                    val mediaType = getMediaType(it)
                    viewModel.addAttachment(Attachment(uri = it.toString(), type = mediaType))
                } catch (e: SecurityException) {
                    e.printStackTrace()
                }
            }
        }
    )

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.create_task)) },
                navigationIcon = {
                    IconButton(onClick = navigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back_button))
                    }
                },
                actions = {
                    IconButton(onClick = {
                        coroutineScope.launch {
                            viewModel.saveTask()
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
                            if (viewModel.taskUiState.isRecordingAudio) {
                                viewModel.stopAudioRecording()
                            } else {
                                viewModel.startAudioRecording()
                            }
                        } else {
                            recordAudioPermissionState.launchMultiplePermissionRequest()
                        }
                    }
                    MediaType.FILE -> {
                        filePickerLauncher.launch(arrayOf("*/*"))
                    }
                }
            },
            onAttachmentRemove = viewModel::removeAttachment,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
fun TaskEntryBody(
    taskUiState: TaskUiState,
    onTaskValueChange: (TaskUiState) -> Unit,
    onAttachmentAdd: (MediaType) -> Unit,
    onAttachmentRemove: (Attachment) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    var showMenu by remember { mutableStateOf(false) }

    fun showTimePickerDialog() {
        TimePickerDialog(
            context,
            { _, hour, minute -> onTaskValueChange(taskUiState.copy(time = "$hour:$minute")) },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        ).show()
    }

    fun showDatePickerDialog() {
        DatePickerDialog(
            context,
            { _, year, month, day -> onTaskValueChange(taskUiState.copy(date = "$day/${month + 1}/$year")) },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OutlinedTextField(
            value = taskUiState.title,
            onValueChange = { onTaskValueChange(taskUiState.copy(title = it)) },
            label = { Text(stringResource(R.string.title_label)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        OutlinedTextField(
            value = taskUiState.description,
            onValueChange = { onTaskValueChange(taskUiState.copy(description = it)) },
            label = { Text(stringResource(R.string.description_label)) },
            modifier = Modifier.fillMaxWidth()
        )
        Text(
            text = if (taskUiState.date.isEmpty()) stringResource(R.string.select_date) else taskUiState.date,
            modifier = Modifier.fillMaxWidth().clickable { showDatePickerDialog() }
        )
        Text(
            text = if (taskUiState.time.isEmpty()) stringResource(R.string.select_time) else taskUiState.time,
            modifier = Modifier.fillMaxWidth().clickable { showTimePickerDialog() }
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
                    text = { Text(if (taskUiState.isRecordingAudio) stringResource(R.string.stop_recording) else stringResource(R.string.record_audio)) },
                    onClick = { onAttachmentAdd(MediaType.AUDIO); showMenu = false }
                )
                DropdownMenuItem(text = { Text(stringResource(R.string.attach_file)) }, onClick = { onAttachmentAdd(MediaType.FILE); showMenu = false })
            }
            if (taskUiState.isRecordingAudio) {
                Button(onClick = { onAttachmentAdd(MediaType.AUDIO) }) {
                    Text(text = stringResource(R.string.stop_recording))
                }
            }
        }

        AttachmentGrid(attachments = taskUiState.attachments, onAttachmentClick = onAttachmentRemove)
    }
}
