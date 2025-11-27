package com.example.proyectofinalweb.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.proyectofinalweb.ProjectApplication
import com.example.proyectofinalweb.util.setAlarm
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.LocalDateTime.parse
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatter.ofPattern

class MyReceiverBootCompleted : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            Log.d("BootReceiver", "El sistema se ha reiniciado. Reprogramando alarmas.")

            val pendingResult = goAsync()
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val tasksRepository = (context.applicationContext as ProjectApplication).container.tasksRepository
                    val tasks = tasksRepository.getAllTasksStream().first()

                    val dateTimeFormatter = ofPattern("d/M/yyyy H:m")
                    val now = LocalDateTime.now()

                    tasks.forEach { task ->
                        if (task.date.isNotBlank() && task.time.isNotBlank()) {
                            try {
                                val taskDateTime =
                                    parse("${task.date} ${task.time}", dateTimeFormatter)
                                if (taskDateTime.isAfter(now)) {
                                    task.setAlarm(context)
                                    Log.d("BootReceiver", "Alarma reprogramada para la tarea: ${task.title}")
                                }
                            } catch (e: Exception) {
                                Log.e("BootReceiver", "Error al parsear fecha/hora para la tarea: ${task.title}", e)
                            }
                        }
                    }
                } finally {
                    pendingResult.finish()
                }
            }
        }
    }
}