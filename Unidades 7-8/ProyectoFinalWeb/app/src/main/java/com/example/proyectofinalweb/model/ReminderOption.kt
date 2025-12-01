package com.example.proyectofinalweb.model

import androidx.annotation.StringRes
import com.example.proyectofinalweb.R

enum class ReminderOption(@StringRes val displayName: Int) {
    AT_TIME(R.string.at_time),
    FIVE_MINUTES_BEFORE(R.string.five_minutes_before),
    TEN_MINUTES_BEFORE(R.string.ten_minutes_before),
    THIRTY_MINUTES_BEFORE(R.string.thirty_minutes_before),
    ONE_HOUR_BEFORE(R.string.one_hour_before),
    ONE_DAY_BEFORE(R.string.one_day_before)
}