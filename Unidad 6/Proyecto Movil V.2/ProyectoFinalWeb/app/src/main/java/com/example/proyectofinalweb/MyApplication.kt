package com.example.proyectofinalweb

import android.app.Application
import com.example.proyectofinalweb.di.AppContainer

class MyApplication : Application() {

    lateinit var appContainer: AppContainer

    override fun onCreate() {
        super.onCreate()
        appContainer = AppContainer(this)
    }
}