package com.example.proyectofinalweb.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.proyectofinalweb.model.Note
import com.example.proyectofinalweb.components.NoteItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    val sampleNotes = listOf(
        Note(1, "Comprar víveres", "Ir al supermercado por leche y pan."),
        Note(2, "Estudiar", "Revisar los temas del examen final de programación."),
        Note(3, "Proyecto Android", "Terminar la pantalla principal del proyecto.")
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Notas y Tareas") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                navController.navigate("add")
            }) {
                Icon(Icons.Default.Add, contentDescription = "Agregar Nota")
            }
        }
    ) { paddingValues ->
        LazyColumn(
            contentPadding = paddingValues,
            modifier = Modifier.fillMaxSize()
        ) {
            items(sampleNotes) { note ->
                NoteItem(note = note) {
                    navController.navigate("detail/${note.id}")
                }
            }
        }
    }
}