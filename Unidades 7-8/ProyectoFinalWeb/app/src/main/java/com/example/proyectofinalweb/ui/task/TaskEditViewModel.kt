package com.example.proyectofinalweb.ui.task

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectofinalweb.data.TasksRepository
import com.example.proyectofinalweb.model.Attachment
import com.example.proyectofinalweb.model.MediaType
import com.example.proyectofinalweb.util.AudioRecorder
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.File

class TaskEditViewModel(
    savedStateHandle: SavedStateHandle,
    private val tasksRepository: TasksRepository,
    private val audioRecorder: AudioRecorder
) : ViewModel() {

    var taskUiState by mutableStateOf(TaskUiState())
        private set

    private var taskId: Int? = savedStateHandle[TaskEditDestination.TASK_ID_ARG]
    private var audioFile: File? = null

    init {
        taskId?.let { loadTask(it) }
    }

    fun initialize(id: Int) {
        if (taskId == null || taskId != id) {
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
        if (taskUiState.title.isNotBlank() || taskUiState.description.isNotBlank() || taskUiState.attachments.isNotEmpty()) {
            tasksRepository.updateTask(taskUiState.toTask())
        }
    }
}
