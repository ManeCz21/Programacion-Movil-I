package com.example.proyectofinalweb.ui

import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.example.proyectofinalweb.ui.navigation.AppNavHost
import com.example.proyectofinalweb.util.AppContentType

@Composable
fun ProjectApp(widthSizeClass: WindowWidthSizeClass) {
    val navController = rememberNavController()
    val contentType = when (widthSizeClass) {
        WindowWidthSizeClass.Compact -> AppContentType.LIST_ONLY
        WindowWidthSizeClass.Medium -> AppContentType.LIST_ONLY
        else -> AppContentType.LIST_AND_DETAIL
    }
    AppNavHost(navController = navController, contentType = contentType)
}
