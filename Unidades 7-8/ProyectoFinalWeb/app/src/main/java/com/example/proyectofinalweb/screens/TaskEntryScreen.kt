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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyectofinalweb.R
import com.example.proyectofinalweb.model.Attachment
import com.example.proyectofinalweb.model.MediaType
import com.example.proyectofinalweb.model.ReminderOption
import com.example.proyectofinalweb.providers.MiFileProviderMultimedia
import com.example.proyectofinalweb.ui.AppViewModelProvider
import com.example.proyectofinalweb.ui.common.AttachmentGrid
import com.example.proyectofinalweb.ui.task.TaskEntryViewModel
import com.example.proyectofinalweb.ui.task.TaskUiState
import com.google.accompanist.permissions.*
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
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

    var cameraPermissionRequested by rememberSaveable { mutableStateOf(false) }
    var audioPermissionRequested by rememberSaveable { mutableStateOf(false) }
    var videoPermissionRequested by rememberSaveable { mutableStateOf(false) }
    var notificationPermissionRequested by rememberSaveable { mutableStateOf(false) }

    var showPermissionDeniedDialog by remember { mutableStateOf(false) }
    var permissionDialogText by remember { mutableStateOf("") }
    var pendingReminderOption by remember { mutableStateOf<ReminderOption?>(null) }

    val notificationPermissionState = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        rememberPermissionState(permission = Manifest.permission.POST_NOTIFICATIONS) { notificationPermissionRequested = true }
    } else {
        null
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

    var pendingMediaType by remember { mutableStateOf<MediaType?>(null) }

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
            if (viewModel.taskUiState.isRecordingAudio) viewModel.stopAudioRecording()
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

    notificationPermissionState?.status?.let {
        LaunchedEffect(it) {
            if (it.isGranted && pendingReminderOption != null) {
                viewModel.addReminder(pendingReminderOption!!)
                pendingReminderOption = null
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
                            if (viewModel.taskUiState.isRecordingAudio) viewModel.stopAudioRecording()
                            else viewModel.startAudioRecording()
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
                    MediaType.FILE -> filePickerLauncher.launch(arrayOf("*/*"))
                }
            },
            onAttachmentRemove = viewModel::removeAttachment,
            onAttachmentDescriptionChange = viewModel::updateAttachmentDescription,
            onAddReminder = { reminderOption ->
                notificationPermissionState?.let {
                    if (it.status.isGranted) {
                        viewModel.addReminder(reminderOption)
                    } else {
                        when (val status = it.status) {
                            is PermissionStatus.Denied -> {
                                if (notificationPermissionRequested && !status.shouldShowRationale) {
                                    permissionDialogText = "Para recibir notificaciones, la app necesita permiso. Por favor, activa el permiso en los ajustes."
                                    showPermissionDeniedDialog = true
                                } else {
                                    pendingReminderOption = reminderOption
                                    it.launchPermissionRequest()
                                }
                            }
                            PermissionStatus.Granted -> {}
                        }
                    }
                } ?: viewModel.addReminder(reminderOption)
            },
            onRemoveReminder = viewModel::removeReminder,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskEntryBody(
    taskUiState: TaskUiState,
    onTaskValueChange: (TaskUiState) -> Unit,
    onAttachmentAdd: (MediaType) -> Unit,
    onAttachmentRemove: (Attachment) -> Unit,
    onAttachmentDescriptionChange: (Attachment, String) -> Unit,
    onAddReminder: (ReminderOption) -> Unit,
    onRemoveReminder: (ReminderOption) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showMenu by remember { mutableStateOf(false) }
    var showReminderMenu by remember { mutableStateOf(false) }

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

    fun calculateReminderDateTime(taskDate: String, taskTime: String, reminderOption: ReminderOption): LocalDateTime? {
        if (taskDate.isBlank() || taskTime.isBlank()) return null
        return try {
            val dateTimeFormatter = DateTimeFormatter.ofPattern("d/M/yyyy H:m")
            val taskDateTime = LocalDateTime.parse("$taskDate $taskTime", dateTimeFormatter)
            when (reminderOption) {
                ReminderOption.AT_TIME -> taskDateTime
                ReminderOption.FIVE_MINUTES_BEFORE -> taskDateTime.minusMinutes(5)
                ReminderOption.TEN_MINUTES_BEFORE -> taskDateTime.minusMinutes(10)
                ReminderOption.THIRTY_MINUTES_BEFORE -> taskDateTime.minusMinutes(30)
                ReminderOption.ONE_HOUR_BEFORE -> taskDateTime.minusHours(1)
                ReminderOption.ONE_DAY_BEFORE -> taskDateTime.minusDays(1)
            }
        } catch (e: Exception) {
            null
        }
    }

    Column(modifier = modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        OutlinedTextField(value = taskUiState.title, onValueChange = { onTaskValueChange(taskUiState.copy(title = it)) }, label = { Text("Título") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
        OutlinedTextField(value = taskUiState.description, onValueChange = { onTaskValueChange(taskUiState.copy(description = it)) }, label = { Text("Descripción") }, modifier = Modifier.fillMaxWidth())

        Text(text = if (taskUiState.date.isEmpty()) "Seleccionar fecha" else taskUiState.date, modifier = Modifier.fillMaxWidth().clickable { showDatePickerDialog() })
        Text(text = if (taskUiState.time.isEmpty()) "Seleccionar hora" else taskUiState.time, modifier = Modifier.fillMaxWidth().clickable { showTimePickerDialog() })

        ExposedDropdownMenuBox(
            expanded = showReminderMenu,
            onExpandedChange = { showReminderMenu = !showReminderMenu }
        ) {
            OutlinedTextField(
                value = "Ninguno",
                onValueChange = {},
                readOnly = true,
                label = { Text("Recordatorio") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showReminderMenu) },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(expanded = showReminderMenu, onDismissRequest = { showReminderMenu = false }) {
                ReminderOption.values().forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option.displayName) },
                        onClick = {
                            onAddReminder(option)
                            showReminderMenu = false
                        }
                    )
                }
            }
        }

        if (taskUiState.reminders.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text("Recordatorios a guardar:")
            taskUiState.reminders.forEach { reminder ->
                val reminderDateTime = calculateReminderDateTime(taskUiState.date, taskUiState.time, reminder)
                val timeString = reminderDateTime?.format(DateTimeFormatter.ofPattern("dd/MM HH:mm")) ?: ""

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "${reminder.displayName} ($timeString)")
                    IconButton(onClick = { onRemoveReminder(reminder) }) {
                        Icon(Icons.Default.Close, contentDescription = "Eliminar recordatorio")
                    }
                }
            }
        }

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
