package com.example.proyectofinalweb.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp

enum class ScreenType { Compact, Medium, Expanded }

@Composable
fun rememberScreenType(): ScreenType {
    val configuration = LocalConfiguration.current
    val screenWidthDp = configuration.screenWidthDp

    return when {
        screenWidthDp < 600 -> ScreenType.Compact
        screenWidthDp < 840 -> ScreenType.Medium
        else -> ScreenType.Expanded
    }
}
