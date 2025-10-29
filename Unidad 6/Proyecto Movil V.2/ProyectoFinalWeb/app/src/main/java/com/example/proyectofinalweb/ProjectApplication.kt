package com.example.proyectofinalweb

import android.app.Application
import com.example.proyectofinalweb.di.AppContainer
import com.example.proyectofinalweb.di.AppDataContainer

class ProjectApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
    }
}
