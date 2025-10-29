package com.example.proyectofinalweb.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.example.proyectofinalweb.ui.navigation.AppNavHost

@Composable
fun ProjectApp() {
    val navController = rememberNavController()
    AppNavHost(navController = navController)
}
