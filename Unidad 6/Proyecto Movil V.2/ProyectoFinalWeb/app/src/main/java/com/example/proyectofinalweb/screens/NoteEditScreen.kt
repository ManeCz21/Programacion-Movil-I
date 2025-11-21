package com.example.proyectofinalweb.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyectofinalweb.R
import com.example.proyectofinalweb.ui.AppViewModelProvider
import com.example.proyectofinalweb.ui.note.NoteEditViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteEditScreen(
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: NoteEditViewModel = viewModel(factory = AppViewModelProvider.Factory),
    noteId: Int? = null // Add noteId parameter
) {
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(noteId) {
        noteId?.let { viewModel.initialize(it) }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.edit_note_title)) },
                navigationIcon = {
                    IconButton(onClick = navigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back_button))
                    }
                },
                actions = {
                    IconButton(onClick = {
                        coroutineScope.launch {
                            viewModel.updateNote()
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
            modifier = Modifier.padding(innerPadding)
        )
    }
}
