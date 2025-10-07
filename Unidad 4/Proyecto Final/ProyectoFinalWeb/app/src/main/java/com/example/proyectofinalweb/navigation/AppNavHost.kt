package com.example.proyectofinalweb.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.proyectofinalweb.screens.CrearNotaScreen
import com.example.proyectofinalweb.screens.HomeScreen

@Composable
fun AppNavHost(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomeScreen(navController)
        }
        composable("add") {
            CrearNotaScreen()
        }
        composable("crearNota") {
            CrearNotaScreen()
        }
        composable("detail/{noteId}", arguments = listOf(
            navArgument("noteId") { type = NavType.IntType }
        )) {
            val noteId = it.arguments?.getInt("noteId") ?: 0
            // Placeholder: NoteDetailScreen(noteId, navController)
        }
    }
}