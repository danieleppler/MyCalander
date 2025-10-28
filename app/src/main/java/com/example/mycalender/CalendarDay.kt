package com.example.mycalender

data class CalendarDay(
    val dayText: String,
    val isValidDay: Boolean,
    val events: List<Event?> = mutableListOf(),
    val tasks: List<Task> = mutableListOf(),
    val birthdays: List<BirthDay> = mutableListOf()
)