package com.example.proyectofinalweb.ui.task

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectofinalweb.data.TasksRepository
import com.example.proyectofinalweb.model.Attachment
import com.example.proyectofinalweb.model.MediaType
import com.example.proyectofinalweb.model.ReminderOption
import com.example.proyectofinalweb.util.AudioRecorder
import com.example.proyectofinalweb.util.cancelAlarm
import com.example.proyectofinalweb.util.setAlarm
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.File

class TaskEditViewModel(
    savedStateHandle: SavedStateHandle,
    private val application: Application,
    private val tasksRepository: TasksRepository,
    private val audioRecorder: AudioRecorder
) : ViewModel() {

    var taskUiState by mutableStateOf(TaskUiState())
        private set

    private var taskId: Int = savedStateHandle[TaskEditDestination.TASK_ID_ARG]!!
    private var audioFile: File? = null

    init {
        loadTask(taskId)
    }

    fun initialize(id: Int) {
        if (taskId != id) {
            taskId = id
            loadTask(id)
        }
    }

    private fun loadTask(id: Int) {
        viewModelScope.launch {
            taskUiState = tasksRepository.getTaskStream(id)
                .filterNotNull()
                .first()
                .toTaskUiState()
        }
    }

    fun updateUiState(newTaskUiState: TaskUiState) {
        taskUiState = newTaskUiState
    }

    fun addAttachment(attachment: Attachment) {
        taskUiState = taskUiState.copy(attachments = taskUiState.attachments + attachment)
    }

    fun removeAttachment(attachment: Attachment) {
        taskUiState = taskUiState.copy(attachments = taskUiState.attachments - attachment)
    }

    fun addReminder(reminder: ReminderOption) {
        if (!taskUiState.reminders.contains(reminder)) {
            taskUiState = taskUiState.copy(reminders = (taskUiState.reminders + reminder).sorted())
        }
    }

    fun removeReminder(reminder: ReminderOption) {
        taskUiState = taskUiState.copy(reminders = taskUiState.reminders - reminder)
    }

    fun updateAttachmentDescription(attachment: Attachment, description: String) {
        val updatedAttachments = taskUiState.attachments.map {
            if (it.uri == attachment.uri) {
                it.copy(description = description)
            } else {
                it
            }
        }
        taskUiState = taskUiState.copy(attachments = updatedAttachments)
    }

    fun startAudioRecording() {
        audioFile = File.createTempFile("audio", ".mp3")
        audioRecorder.start(audioFile!!)
        taskUiState = taskUiState.copy(isRecordingAudio = true)
    }

    fun stopAudioRecording() {
        audioRecorder.stop()
        audioFile?.let {
            addAttachment(Attachment(uri = it.toURI().toString(), type = MediaType.AUDIO))
        }
        audioFile = null
        taskUiState = taskUiState.copy(isRecordingAudio = false)
    }

    suspend fun updateTask() {
        val updatedTask = taskUiState.toTask()
        updatedTask.cancelAlarm(application)
        tasksRepository.updateTask(updatedTask)
        updatedTask.setAlarm(application)
    }
}
