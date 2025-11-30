package com.example.proyectofinalweb.receivers

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.proyectofinalweb.MainActivity
import com.example.proyectofinalweb.R

class AlarmasReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val taskId = intent.getIntExtra("EXTRA_TASK_ID", -1)
        if (taskId == -1) return

        val notificationId = intent.getIntExtra("EXTRA_NOTIFICATION_ID", taskId)
        val message = intent.getStringExtra("EXTRA_MESSAGE") ?: "Tarea"
        val isReminder = intent.getBooleanExtra("EXTRA_IS_REMINDER", false)

        val title: String
        val text: String

        if (isReminder) {
            title = "Recordatorio de Tarea"
            text = "Tu tarea '$message' está pendiente."
        } else {
            title = "Tarea Pendiente"
            text = "Es hora de tu tarea '$message'."
        }

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val activityIntent = Intent(context, MainActivity::class.java).apply {
            putExtra("EXTRA_TASK_ID", taskId)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            taskId, // A single request code for all notifications of the same task is OK
            activityIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, "task_channel")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(notificationId, notification)
    }
}
