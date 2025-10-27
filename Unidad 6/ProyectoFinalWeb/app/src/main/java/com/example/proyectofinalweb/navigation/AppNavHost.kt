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
import java.net.URLDecoder

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
        composable(
            "detail/{notaTexto}",
            arguments = listOf(navArgument("notaTexto") { type = NavType.StringType })
        ) { backStackEntry ->
            val encodedTexto = backStackEntry.arguments?.getString("notaTexto") ?: ""
            val notaTexto = URLDecoder.decode(encodedTexto, "UTF-8")
            DetalleNotaScreen(notaTexto)
        }
    }
}