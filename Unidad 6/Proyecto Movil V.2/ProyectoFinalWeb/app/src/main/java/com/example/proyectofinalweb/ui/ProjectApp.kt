package com.example.proyectofinalweb.ui

import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.example.proyectofinalweb.navigation.AppNavHost

@Composable
fun ProjectApp(widthSizeClass: WindowWidthSizeClass) {
    val navController = rememberNavController()
    AppNavHost(navController = navController, widthSizeClass = widthSizeClass)
}
