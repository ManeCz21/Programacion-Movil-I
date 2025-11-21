package com.example.proyectofinalweb.ui

import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.proyectofinalweb.ProjectApplication
import com.example.proyectofinalweb.ui.home.HomeViewModel
import com.example.proyectofinalweb.ui.note.NoteDetailsViewModel
import com.example.proyectofinalweb.ui.note.NoteEditViewModel
import com.example.proyectofinalweb.ui.note.NoteEntryViewModel
import com.example.proyectofinalweb.ui.task.TaskDetailsViewModel
import com.example.proyectofinalweb.ui.task.TaskEditViewModel
import com.example.proyectofinalweb.ui.task.TaskEntryViewModel

object AppViewModelProvider {
    val Factory = viewModelFactory {
        // Notes
        initializer {
            HomeViewModel(
                projectApplication().container.notesRepository,
                projectApplication().container.tasksRepository
            )
        }
        initializer {
            NoteEntryViewModel(projectApplication().container.notesRepository)
        }
        initializer {
            NoteDetailsViewModel(
                this.createSavedStateHandle(),
                projectApplication().container.notesRepository
            )
        }
        initializer {
            NoteEditViewModel(
                this.createSavedStateHandle(),
                projectApplication().container.notesRepository
            )
        }

        // Tasks
        initializer {
            TaskEntryViewModel(projectApplication().container.tasksRepository)
        }
        initializer {
            TaskDetailsViewModel(
                this.createSavedStateHandle(),
                projectApplication().container.tasksRepository
            )
        }
        initializer {
            TaskEditViewModel(
                this.createSavedStateHandle(),
                projectApplication().container.tasksRepository
            )
        }
    }
}

fun CreationExtras.projectApplication(): ProjectApplication = (this[AndroidViewModelFactory.APPLICATION_KEY] as ProjectApplication)
