package com.example.proyectofinalweb.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.proyectofinalweb.db.NoteDao
import com.example.proyectofinalweb.screens.CrearNotaScreen
import com.example.proyectofinalweb.screens.DetalleNotaScreen
import com.example.proyectofinalweb.screens.EditarNotaScreen
import com.example.proyectofinalweb.screens.HomeScreen

@Composable
fun AppNavHost(navController: NavHostController, noteDao: NoteDao) {
    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomeScreen(navController, noteDao)
        }
        composable("add") {
            CrearNotaScreen(noteDao = noteDao, navController = navController)
        }
        composable("crearNota") {
            CrearNotaScreen(noteDao = noteDao, navController = navController)
        }
        composable(
            "detail/{noteId}",
            arguments = listOf(navArgument("noteId") { type = NavType.IntType })
        ) { backStackEntry ->
            val noteId = backStackEntry.arguments?.getInt("noteId") ?: 0
            DetalleNotaScreen(noteId = noteId, noteDao = noteDao, navController = navController)
        }
        composable(
            "edit/{noteId}",
            arguments = listOf(navArgument("noteId") { type = NavType.IntType })
        ) { backStackEntry ->
            val noteId = backStackEntry.arguments?.getInt("noteId") ?: 0
            EditarNotaScreen(noteId = noteId, noteDao = noteDao, navController = navController)
        }
    }
}