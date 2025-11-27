package com.example.proyectofinalweb.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.proyectofinalweb.model.Task
import com.example.proyectofinalweb.receivers.AlarmasReceiver
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

fun Task.setAlarm(context: Context) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val intent = Intent(context, AlarmasReceiver::class.java).apply {
        putExtra("EXTRA_MESSAGE", title)
        putExtra("EXTRA_TASK_ID", id)
    }
    val pendingIntent = PendingIntent.getBroadcast(
        context,
        id,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    if (date.isNotBlank() && time.isNotBlank()) {
        try {
            val dateTimeFormatter = DateTimeFormatter.ofPattern("d/M/yyyy H:m")
            val taskDateTime = LocalDateTime.parse("$date $time", dateTimeFormatter)
            val triggerTime = taskDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

            if (triggerTime > System.currentTimeMillis()) {
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    triggerTime,
                    pendingIntent
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

fun Task.cancelAlarm(context: Context) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val intent = Intent(context, AlarmasReceiver::class.java)
    val pendingIntent = PendingIntent.getBroadcast(
        context,
        id,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )
    alarmManager.cancel(pendingIntent)
}
