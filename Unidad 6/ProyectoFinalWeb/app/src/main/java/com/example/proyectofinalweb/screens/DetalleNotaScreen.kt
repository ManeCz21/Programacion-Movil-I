package com.example.proyectofinalweb.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.proyectofinalweb.viewmodel.NotaViewModel

@Composable
fun DetalleNotaScreen(
    notaTexto: String,
    navController: NavController,
    viewModel: NotaViewModel
) {
    var editing by remember { mutableStateOf(false) }
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf(notaTexto) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        if (!editing) {
            Text(text = title.ifEmpty { "Detalle de nota" }, style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(8.dp))
            Text(text = description)
            Spacer(Modifier.height(12.dp))

            // Si quisieras mostrar una imagen, aquí podría venir (placeholder)
            // AsyncImage(model = "uri", contentDescription = null)

            Spacer(Modifier.height(16.dp))
            Row {
                Button(onClick = { editing = true }) {
                    Text("Editar")
                }
                Spacer(Modifier.width(8.dp))
                Button(onClick = { navController.popBackStack() }) {
                    Text("Regresar")
                }
            }
        } else {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Título") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Descripción") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 6
            )
            Spacer(Modifier.height(12.dp))
            Row {
                Button(onClick = {
                    // Aquí podrías actualizar la nota en el ViewModel si quisieras
                    editing = false
                }) {
                    Text("Guardar")
                }
                Spacer(Modifier.width(8.dp))
                Button(onClick = { editing = false }) {
                    Text("Cancelar")
                }
            }
        }
    }
}
