package com.example.mycalender.DateModels

data class CalendarDay(
    val dayText: String,
    val isValidDay: Boolean,
    val events: List<Event?> = mutableListOf(),
)