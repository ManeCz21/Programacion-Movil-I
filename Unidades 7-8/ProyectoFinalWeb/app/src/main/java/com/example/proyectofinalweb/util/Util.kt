package com.example.proyectofinalweb.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.proyectofinalweb.model.ReminderOption
import com.example.proyectofinalweb.model.Task
import com.example.proyectofinalweb.receivers.AlarmasReceiver
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

fun Task.setAlarm(context: Context) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    if (date.isNotBlank() && time.isNotBlank()) {
        try {
            val dateTimeFormatter = DateTimeFormatter.ofPattern("d/M/yyyy H:m")
            val taskDateTime = LocalDateTime.parse("$date $time", dateTimeFormatter)

            for (reminder in reminders) {
                val reminderDateTime = when (reminder) {
                    ReminderOption.AT_TIME -> taskDateTime
                    ReminderOption.FIVE_MINUTES_BEFORE -> taskDateTime.minusMinutes(5)
                    ReminderOption.TEN_MINUTES_BEFORE -> taskDateTime.minusMinutes(10)
                    ReminderOption.THIRTY_MINUTES_BEFORE -> taskDateTime.minusMinutes(30)
                    ReminderOption.ONE_HOUR_BEFORE -> taskDateTime.minusHours(1)
                    ReminderOption.ONE_DAY_BEFORE -> taskDateTime.minusDays(1)
                }

                val triggerTime = reminderDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

                // Create a unique request code for each reminder
                val requestCode = id * 100 + reminder.ordinal

                val intent = Intent(context, AlarmasReceiver::class.java).apply {
                    putExtra("EXTRA_MESSAGE", title)
                    putExtra("EXTRA_TASK_ID", id)
                    putExtra("EXTRA_NOTIFICATION_ID", requestCode) // Pass unique ID for the notification
                }

                val pendingIntent = PendingIntent.getBroadcast(
                    context,
                    requestCode,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )

                if (triggerTime > System.currentTimeMillis()) {
                    alarmManager.setExact(
                        AlarmManager.RTC_WAKEUP,
                        triggerTime,
                        pendingIntent
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

fun Task.cancelAlarm(context: Context) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    // Cancel all potential alarms for this task
    for (reminder in ReminderOption.values()) {
        val requestCode = id * 100 + reminder.ordinal
        val intent = Intent(context, AlarmasReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent)
        }
    }
}
