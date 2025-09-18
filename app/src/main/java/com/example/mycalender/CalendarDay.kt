package com.example.mycalender

data class CalendarDay(
    val dayText: String,
    val isValidDay: Boolean,
    val items : List<CalenderItem> = mutableListOf()
)