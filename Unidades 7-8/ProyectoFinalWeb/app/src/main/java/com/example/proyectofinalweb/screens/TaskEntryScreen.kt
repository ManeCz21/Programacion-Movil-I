package com.example.proyectofinalweb.screens

import android.Manifest
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.media.MediaRecorder
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyectofinalweb.R
import com.example.proyectofinalweb.model.Attachment
import com.example.proyectofinalweb.model.AttachmentType
import com.example.proyectofinalweb.ui.AppViewModelProvider
import com.example.proyectofinalweb.ui.task.TaskEntryViewModel
import com.example.proyectofinalweb.ui.task.TaskUiState
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import kotlinx.coroutines.launch
import java.io.File
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskEntryScreen(
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TaskEntryViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val coroutineScope = rememberCoroutineScope()
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
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun TaskEntryBody(
    taskUiState: TaskUiState,
    onTaskValueChange: (TaskUiState) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    var showMenu by remember { mutableStateOf(false) }
    val authority = "${context.packageName}.provider"

    var mediaRecorder by remember { mutableStateOf<MediaRecorder?>(null) }
    var audioFile by remember { mutableStateOf<File?>(null) }
    var isRecording by remember { mutableStateOf(false) }

    val permissionsState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO
        )
    )

    fun getUri(filePrefix: String, fileSuffix: String): Uri {
        val file = File.createTempFile(
            filePrefix,
            fileSuffix,
            context.externalCacheDir
        )
        return FileProvider.getUriForFile(
            context,
            authority,
            file
        )
    }

    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            val uri = getUri("temp_image", ".jpg")
            val newAttachments = taskUiState.attachments.toMutableList().apply {
                add(Attachment(uri.toString(), AttachmentType.IMAGE))
            }
            onTaskValueChange(taskUiState.copy(attachments = newAttachments))
        }
    }

    val recordVideoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CaptureVideo()
    ) { success ->
        if (success) {
            val uri = getUri("temp_video", ".mp4")
            val newAttachments = taskUiState.attachments.toMutableList().apply {
                add(Attachment(uri.toString(), AttachmentType.VIDEO))
            }
            onTaskValueChange(taskUiState.copy(attachments = newAttachments))
        }
    }

    val selectFileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val newAttachments = taskUiState.attachments.toMutableList().apply {
                add(Attachment(it.toString(), AttachmentType.FILE))
            }
            onTaskValueChange(taskUiState.copy(attachments = newAttachments))
        }
    }

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
        Box {
            Button(onClick = { showMenu = !showMenu }) {
                Text(stringResource(R.string.attach_button))
            }
            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = { showMenu = false }
            ) {
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.capture_image)) },
                    onClick = {
                        if (permissionsState.permissions[0].status.isGranted) {
                            takePictureLauncher.launch(getUri("temp_image", ".jpg"))
                        } else {
                            permissionsState.launchMultiplePermissionRequest()
                        }
                        showMenu = false
                    }
                )
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.record_video)) },
                    onClick = { 
                        if (permissionsState.permissions.all { it.status.isGranted }) {
                            recordVideoLauncher.launch(getUri("temp_video", ".mp4"))
                        } else {
                            permissionsState.launchMultiplePermissionRequest()
                        }
                        showMenu = false
                    }
                )
                DropdownMenuItem(
                    text = { Text(if (isRecording) stringResource(R.string.stop_recording) else stringResource(R.string.record_audio)) },
                    onClick = { 
                        if (isRecording) {
                            mediaRecorder?.stop()
                            mediaRecorder?.release()
                            mediaRecorder = null
                            isRecording = false
                            audioFile?.let { 
                                val newAttachments = taskUiState.attachments.toMutableList().apply {
                                    add(Attachment(it.toURI().toString(), AttachmentType.AUDIO))
                                }
                                onTaskValueChange(taskUiState.copy(attachments = newAttachments))
                            }
                        } else {
                            if (permissionsState.permissions[1].status.isGranted) {
                                val file = File(context.cacheDir, "audio.mp3")
                                audioFile = file
                                mediaRecorder = MediaRecorder(context).apply {
                                    setAudioSource(MediaRecorder.AudioSource.MIC)
                                    setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                                    setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                                    setOutputFile(file.absolutePath)
                                    prepare()
                                    start()
                                }
                                isRecording = true
                            } else {
                                permissionsState.launchMultiplePermissionRequest()
                            }
                        }
                        showMenu = false
                     }
                )
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.attach_file)) },
                    onClick = { 
                        selectFileLauncher.launch("*/*")
                        showMenu = false
                    }
                )
            }
        }

        if (taskUiState.attachments.isNotEmpty()) {
            Text(text = stringResource(id = R.string.attachments_header), style = MaterialTheme.typography.titleMedium)
            LazyRow {
                items(taskUiState.attachments) {
                    Text(text = it.uri)
                }
            }
        } else {
            Text(stringResource(id = R.string.no_attachments))
        }
    }
}
