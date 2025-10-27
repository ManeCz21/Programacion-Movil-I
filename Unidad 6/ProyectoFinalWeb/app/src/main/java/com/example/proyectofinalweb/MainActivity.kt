package com.example.proyectofinalweb

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.material3.*
import androidx.navigation.compose.rememberNavController
import com.example.proyectofinalweb.data.NoteRepository
import com.example.proyectofinalweb.data.TaskDatabase
import com.example.proyectofinalweb.navigation.AppNavHost
import com.example.proyectofinalweb.ui.theme.ProyectoFinalWebTheme
import com.example.proyectofinalweb.viewmodel.NotaViewModel
import com.example.proyectofinalweb.viewmodel.NotaViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ProyectoFinalWebTheme {
                val navController = rememberNavController()

                // Crear base de datos Room
                val db = TaskDatabase.getDatabase(this)
                val noteRepository = NoteRepository(db.noteDao())

                // Crear ViewModel con factory
                val notaViewModel: NotaViewModel = viewModel(
                    factory = NotaViewModelFactory(noteRepository)
                )

                // Pasar el ViewModel al NavHost
                AppNavHost(
                    navController = navController,
                    notaViewModel = notaViewModel
                )
            }
        }
    }
}
