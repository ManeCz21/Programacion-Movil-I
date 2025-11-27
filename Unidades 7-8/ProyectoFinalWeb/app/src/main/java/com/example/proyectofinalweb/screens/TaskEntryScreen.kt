package com.example.proyectofinalweb.screens

import android.Manifest
import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
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
import com.example.proyectofinalweb.providers.MiFileProviderMultimedia
import com.example.proyectofinalweb.ui.AppViewModelProvider
import com.example.proyectofinalweb.ui.common.AttachmentGrid
import com.example.proyectofinalweb.ui.task.TaskEntryViewModel
import com.example.proyectofinalweb.ui.task.TaskUiState
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.permissions.rememberPermissionState
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

    val notificationPermissionState = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        rememberPermissionState(permission = Manifest.permission.POST_NOTIFICATIONS)
    } else {
        null
    }

    LaunchedEffect(Unit) {
        notificationPermissionState?.let {
            if (!it.status.isGranted) {
                it.launchPermissionRequest()
            }
        }
    }

    fun getMediaType(uri: Uri): MediaType {
        val mimeType = contentResolver.getType(uri)
        return when {
            mimeType?.startsWith("image") == true -> MediaType.IMAGE
            mimeType?.startsWith("video") == true -> MediaType.VIDEO
            mimeType?.startsWith("audio") == true -> MediaType.AUDIO
            else -> MediaType.FILE
        }
    }

    val cameraPermissionState = rememberMultiplePermissionsState(listOf(Manifest.permission.CAMERA))
    val recordAudioPermissionState = rememberMultiplePermissionsState(listOf(Manifest.permission.RECORD_AUDIO))
    val cameraAndAudioPermissionState = rememberMultiplePermissionsState(listOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO))

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

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.create_task)) },
                navigationIcon = { IconButton(onClick = navigateBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") } },
                actions = {
                    IconButton(onClick = {
                        coroutineScope.launch {
                            viewModel.saveTask()
                            navigateBack()
                        }
                    }) { Icon(Icons.Filled.Done, "Save") }
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
                            val newImageUri = MiFileProviderMultimedia.getImageUri(context)
                            imageUri = newImageUri
                            imagePickerLauncher.launch(newImageUri)
                        } else cameraPermissionState.launchMultiplePermissionRequest()
                    }
                    MediaType.VIDEO -> {
                        if (cameraAndAudioPermissionState.allPermissionsGranted) {
                            val newVideoUri = MiFileProviderMultimedia.getVideoUri(context)
                            videoUri = newVideoUri
                            videoPickerLauncher.launch(newVideoUri)
                        } else cameraAndAudioPermissionState.launchMultiplePermissionRequest()
                    }
                    MediaType.AUDIO -> {
                        if (recordAudioPermissionState.allPermissionsGranted) {
                            if (viewModel.taskUiState.isRecordingAudio) viewModel.stopAudioRecording()
                            else viewModel.startAudioRecording()
                        } else recordAudioPermissionState.launchMultiplePermissionRequest()
                    }
                    MediaType.FILE -> filePickerLauncher.launch(arrayOf("*/*"))
                }
            },
            onAttachmentRemove = viewModel::removeAttachment,
            onAttachmentDescriptionChange = viewModel::updateAttachmentDescription,
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
    onAttachmentDescriptionChange: (Attachment, String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showMenu by remember { mutableStateOf(false) }

    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    var hasExactAlarmPermission by remember {
        mutableStateOf(if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) alarmManager.canScheduleExactAlarms() else true)
    }
    val settingsLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) hasExactAlarmPermission = alarmManager.canScheduleExactAlarms()
    }
    var showPermissionDialog by remember { mutableStateOf(false) }

    if (showPermissionDialog) {
        AlertDialog(
            onDismissRequest = { showPermissionDialog = false },
            title = { Text("Permiso Requerido") },
            text = { Text("Para que los recordatorios funcionen, la app necesita permiso para programar alarmas. Por favor, activa el permiso en los ajustes.") },
            confirmButton = { Button(onClick = { showPermissionDialog = false; settingsLauncher.launch(Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)) }) { Text("Ir a Ajustes") } },
            dismissButton = { Button(onClick = { showPermissionDialog = false }) { Text("Cancelar") } }
        )
    }

    fun showTimePickerDialog() {
        if (!hasExactAlarmPermission) {
            showPermissionDialog = true
            return
        }
        val calendar = Calendar.getInstance()
        TimePickerDialog(context, { _, hour, minute -> onTaskValueChange(taskUiState.copy(time = "$hour:$minute")) }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show()
    }

    fun showDatePickerDialog() {
        if (!hasExactAlarmPermission) {
            showPermissionDialog = true
            return
        }
        val calendar = Calendar.getInstance()
        DatePickerDialog(context, { _, year, month, day -> onTaskValueChange(taskUiState.copy(date = "$day/${month + 1}/$year")) }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
    }

    Column(modifier = modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        OutlinedTextField(value = taskUiState.title, onValueChange = { onTaskValueChange(taskUiState.copy(title = it)) }, label = { Text("Título") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
        OutlinedTextField(value = taskUiState.description, onValueChange = { onTaskValueChange(taskUiState.copy(description = it)) }, label = { Text("Descripción") }, modifier = Modifier.fillMaxWidth())

        Text(text = if (taskUiState.date.isEmpty()) "Seleccionar fecha" else taskUiState.date, modifier = Modifier.fillMaxWidth().clickable { showDatePickerDialog() })
        Text(text = if (taskUiState.time.isEmpty()) "Seleccionar hora" else taskUiState.time, modifier = Modifier.fillMaxWidth().clickable { showTimePickerDialog() })

        Spacer(modifier = Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { showMenu = true }) { Icon(Icons.Default.Add, "Adjuntar"); Text("Adjuntar") }
            DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                DropdownMenuItem(text = { Text("Tomar foto") }, onClick = { onAttachmentAdd(MediaType.IMAGE); showMenu = false })
                DropdownMenuItem(text = { Text("Grabar video") }, onClick = { onAttachmentAdd(MediaType.VIDEO); showMenu = false })
                DropdownMenuItem(text = { Text(if (taskUiState.isRecordingAudio) "Detener grabación" else "Grabar audio") }, onClick = { onAttachmentAdd(MediaType.AUDIO); showMenu = false })
                DropdownMenuItem(text = { Text("Adjuntar archivo") }, onClick = { onAttachmentAdd(MediaType.FILE); showMenu = false })
            }
            if (taskUiState.isRecordingAudio) {
                Button(onClick = { onAttachmentAdd(MediaType.AUDIO) }) { Text("Detener grabación") }
            }
        }

        AttachmentGrid(attachments = taskUiState.attachments, onAttachmentClick = onAttachmentRemove, onAttachmentDescriptionChange = onAttachmentDescriptionChange, isEditing = true)
    }
}
