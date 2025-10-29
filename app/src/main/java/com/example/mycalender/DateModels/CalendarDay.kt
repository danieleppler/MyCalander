package com.example.mycalender.DateModels

import com.example.mycalender.BirthDay
import com.example.mycalender.Task

data class CalendarDay(
    val dayText: String,
    val isValidDay: Boolean,
    val events: List<Event?> = mutableListOf(),
    val tasks: List<Task> = mutableListOf(),
    val birthdays: List<BirthDay> = mutableListOf()
)