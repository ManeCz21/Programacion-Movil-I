package com.example.proyectofinalweb.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.proyectofinalweb.screens.CrearNotaScreen
import com.example.proyectofinalweb.screens.DetalleNotaScreen
import com.example.proyectofinalweb.screens.HomeScreen
import com.example.proyectofinalweb.viewmodel.NotaViewModel

@Composable
fun AppNavHost(
    navController: NavHostController,
    notaViewModel: NotaViewModel
) {
    NavHost(navController = navController, startDestination = "home") {

        composable("home") {
            HomeScreen(navController = navController, viewModel = notaViewModel)
        }

        composable("add") {
            CrearNotaScreen(navController = navController, viewModel = notaViewModel)
        }

        composable("crearNota") {
            CrearNotaScreen(navController = navController, viewModel = notaViewModel)
        }

        composable(
            "detail/{noteId}",
            arguments = listOf(navArgument("noteId") { type = NavType.IntType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("noteId") ?: 0
            DetalleNotaScreen(
                noteId = id,
                navController = navController,
                viewModel = notaViewModel
            )
        }
    }
}
