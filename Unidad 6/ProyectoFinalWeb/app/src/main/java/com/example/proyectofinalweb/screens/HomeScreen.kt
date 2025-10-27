package com.example.proyectofinalweb.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.proyectofinalweb.R
import com.example.proyectofinalweb.components.NoteItem
import com.example.proyectofinalweb.ui.rememberScreenType
import com.example.proyectofinalweb.ui.ScreenType
import com.example.proyectofinalweb.viewmodel.NotaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: NotaViewModel
) {
    // 🔹 Obtenemos las notas desde el ViewModel
    val notes by viewModel.notes.collectAsState(initial = emptyList())

    // 🔹 Detectamos tipo de pantalla
    val screenType = rememberScreenType()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.notes_and_tasks)) }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("add") },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar nota")
            }
        }
    ) { paddingValues ->

        when (screenType) {
            ScreenType.Compact -> {
                // 🔸 Diseño para pantallas pequeñas
                LazyColumn(
                    contentPadding = paddingValues,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 8.dp)
                ) {
                    items(notes) { note ->
                        NoteItem(note = note) {
                            // Navega con el ID (correcto)
                            navController.navigate("detail/${note.id}")
                        }
                    }
                }
            }

            ScreenType.Medium, ScreenType.Expanded -> {
                // 🔸 Diseño para tablets: lista + detalle lado a lado
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    LazyColumn(
                        modifier = Modifier
                            .weight(0.4f)
                            .fillMaxHeight()
                            .padding(end = 8.dp)
                    ) {
                        items(notes) { note ->
                            NoteItem(note = note) {
                                // También navega con ID
                                navController.navigate("detail/${note.id}")
                            }
                        }
                    }

                    Box(
                        modifier = Modifier
                            .weight(0.6f)
                            .fillMaxHeight(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Selecciona una nota o crea una nueva",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }
    }
}
