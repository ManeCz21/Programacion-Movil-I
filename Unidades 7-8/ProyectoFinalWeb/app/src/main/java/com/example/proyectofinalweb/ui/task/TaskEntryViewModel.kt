package com.example.proyectofinalweb.ui.task

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.proyectofinalweb.data.TasksRepository
import com.example.proyectofinalweb.model.Attachment
import com.example.proyectofinalweb.model.MediaType
import com.example.proyectofinalweb.util.AudioRecorder
import com.example.proyectofinalweb.util.setAlarm
import java.io.File

class TaskEntryViewModel(
    private val application: Application,
    private val tasksRepository: TasksRepository,
    private val audioRecorder: AudioRecorder
) : ViewModel() {
    var taskUiState by mutableStateOf(TaskUiState())
        private set

    private var audioFile: File? = null

    fun updateUiState(newTaskUiState: TaskUiState) {
        taskUiState = newTaskUiState
    }

    fun addAttachment(attachment: Attachment) {
        taskUiState = taskUiState.copy(attachments = taskUiState.attachments + attachment)
    }

    fun removeAttachment(attachment: Attachment) {
        taskUiState = taskUiState.copy(attachments = taskUiState.attachments - attachment)
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

    suspend fun saveTask() {
        if (taskUiState.title.isNotBlank() || taskUiState.description.isNotBlank() || taskUiState.attachments.isNotEmpty()) {
            val taskToInsert = taskUiState.toTask()
            val newId = tasksRepository.insertTask(taskToInsert)

            if (newId > 0) {
                val taskWithId = taskToInsert.copy(id = newId.toInt())
                taskWithId.setAlarm(application)
            }
        }
    }
}
