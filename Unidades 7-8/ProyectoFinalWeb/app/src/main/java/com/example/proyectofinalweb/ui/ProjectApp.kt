package com.example.proyectofinalweb.ui

import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.example.proyectofinalweb.ui.navigation.AppNavHost
import com.example.proyectofinalweb.util.AppContentType

@Composable
fun ProjectApp(
    windowSizeClass: WindowWidthSizeClass,
    taskId: Int? = null,
    onTaskOpened: () -> Unit
) {
    val navController = rememberNavController()
    val contentType = when (windowSizeClass) {
        WindowWidthSizeClass.Compact -> AppContentType.LIST_ONLY
        WindowWidthSizeClass.Medium -> AppContentType.LIST_ONLY
        else -> AppContentType.LIST_AND_DETAIL
    }
    AppNavHost(
        navController = navController,
        contentType = contentType,
        taskId = taskId,
        onTaskOpened = onTaskOpened
    )
}
