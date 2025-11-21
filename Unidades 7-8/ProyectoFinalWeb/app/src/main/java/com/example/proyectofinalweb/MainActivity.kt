package com.example.proyectofinalweb

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Surface
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import com.example.proyectofinalweb.ui.ProjectApp
import com.example.proyectofinalweb.ui.theme.ProyectoFinalWebTheme

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ProyectoFinalWebTheme {
                Surface {
                    val windowSizeClass = calculateWindowSizeClass(this)
                    ProjectApp(windowSizeClass.widthSizeClass)
                }
            }
        }
    }
}
