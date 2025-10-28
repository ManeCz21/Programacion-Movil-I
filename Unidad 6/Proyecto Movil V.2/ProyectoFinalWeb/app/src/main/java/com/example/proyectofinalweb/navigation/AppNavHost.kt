package com.example.proyectofinalweb.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.proyectofinalweb.db.NoteDao
import com.example.proyectofinalweb.db.TaskDao
import com.example.proyectofinalweb.screens.CrearNotaScreen
import com.example.proyectofinalweb.screens.DetalleNotaScreen
import com.example.proyectofinalweb.screens.DetalleTareaScreen
import com.example.proyectofinalweb.screens.EditarNotaScreen
import com.example.proyectofinalweb.screens.EditarTareaScreen
import com.example.proyectofinalweb.screens.HomeScreen

@Composable
fun AppNavHost(navController: NavHostController, noteDao: NoteDao, taskDao: TaskDao) {
    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomeScreen(navController, noteDao, taskDao)
        }
        composable("add") {
            CrearNotaScreen(noteDao = noteDao, taskDao = taskDao, navController = navController)
        }
        composable("crearNota") {
            CrearNotaScreen(noteDao = noteDao, taskDao = taskDao, navController = navController)
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
        composable(
            "taskDetail/{taskId}",
            arguments = listOf(navArgument("taskId") { type = NavType.IntType })
        ) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getInt("taskId") ?: 0
            DetalleTareaScreen(taskId = taskId, taskDao = taskDao, navController = navController)
        }
        composable(
            "editTask/{taskId}",
            arguments = listOf(navArgument("taskId") { type = NavType.IntType })
        ) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getInt("taskId") ?: 0
            EditarTareaScreen(taskId = taskId, taskDao = taskDao, navController = navController)
        }
    }
}