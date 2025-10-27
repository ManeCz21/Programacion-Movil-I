package com.example.proyectofinalweb

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.*
import androidx.navigation.compose.rememberNavController
import com.example.proyectofinalweb.navigation.AppNavHost
import com.example.proyectofinalweb.ui.theme.ProyectoFinalWebTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ProyectoFinalWebTheme {
                val navController = rememberNavController()
                AppNavHost(navController = navController)
            }
        }
    }
}
