package com.example.proyectofinalweb.model

enum class ReminderOption(val displayName: String) {
    AT_TIME("Al momento"),
    FIVE_MINUTES_BEFORE("5 minutos antes"),
    TEN_MINUTES_BEFORE("10 minutos antes"),
    THIRTY_MINUTES_BEFORE("30 minutos antes"),
    ONE_HOUR_BEFORE("1 hora antes"),
    ONE_DAY_BEFORE("1 día antes")
}