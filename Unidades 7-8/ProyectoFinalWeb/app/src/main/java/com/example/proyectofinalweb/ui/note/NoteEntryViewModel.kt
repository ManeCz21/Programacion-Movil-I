package com.example.proyectofinalweb.ui.note

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.proyectofinalweb.data.NotesRepository
import com.example.proyectofinalweb.model.Attachment
import com.example.proyectofinalweb.model.MediaType
import com.example.proyectofinalweb.util.AudioRecorder
import java.io.File

class NoteEntryViewModel(
    private val notesRepository: NotesRepository,
    private val audioRecorder: AudioRecorder
) : ViewModel() {
    var noteUiState by mutableStateOf(NoteUiState())
        private set

    private var audioFile: File? = null

    fun updateUiState(newNoteUiState: NoteUiState) {
        noteUiState = newNoteUiState
    }

    fun addAttachment(attachment: Attachment) {
        noteUiState = noteUiState.copy(attachments = noteUiState.attachments + attachment)
    }

    fun removeAttachment(attachment: Attachment) {
        noteUiState = noteUiState.copy(attachments = noteUiState.attachments - attachment)
    }

    fun updateAttachmentDescription(attachment: Attachment, description: String) {
        val updatedAttachments = noteUiState.attachments.map {
            if (it.uri == attachment.uri) {
                it.copy(description = description)
            } else {
                it
            }
        }
        noteUiState = noteUiState.copy(attachments = updatedAttachments)
    }

    fun startAudioRecording() {
        audioFile = File.createTempFile("audio", ".mp3")
        audioRecorder.start(audioFile!!)
        noteUiState = noteUiState.copy(isRecordingAudio = true)
    }

    fun stopAudioRecording() {
        audioRecorder.stop()
        audioFile?.let {
            addAttachment(Attachment(uri = it.toURI().toString(), type = MediaType.AUDIO))
        }
        audioFile = null
        noteUiState = noteUiState.copy(isRecordingAudio = false)
    }

    suspend fun saveNote() {
        if (noteUiState.title.isNotBlank() || noteUiState.description.isNotBlank() || noteUiState.attachments.isNotEmpty()) {
            notesRepository.insertNote(noteUiState.toNote())
        }
    }
}
