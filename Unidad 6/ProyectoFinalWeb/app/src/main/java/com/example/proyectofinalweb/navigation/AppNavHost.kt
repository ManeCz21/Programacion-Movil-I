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
import java.net.URLDecoder

@Composable
fun AppNavHost(
    navController: NavHostController,
    notaViewModel: NotaViewModel
) {
    NavHost(navController = navController, startDestination = "home") {

        // Pantalla principal
        composable("home") {
            HomeScreen(
                navController = navController,
                viewModel = notaViewModel
            )
        }

        // Pantalla para crear una nota
        composable("add") {
            CrearNotaScreen(
                navController = navController,
                viewModel = notaViewModel
            )
        }

        // Alias de crearNota (por si se usa otro nombre en la navegación)
        composable("crearNota") {
            CrearNotaScreen(
                navController = navController,
                viewModel = notaViewModel
            )
        }

        // Pantalla de detalle (usa texto codificado en la ruta)
        composable(
            "detail/{notaTexto}",
            arguments = listOf(navArgument("notaTexto") { type = NavType.StringType })
        ) { backStackEntry ->
            val encodedTexto = backStackEntry.arguments?.getString("notaTexto") ?: ""
            val notaTexto = URLDecoder.decode(encodedTexto, "UTF-8")

            DetalleNotaScreen(
                notaTexto = notaTexto,
                navController = navController,
                viewModel = notaViewModel
            )
        }
    }
}
