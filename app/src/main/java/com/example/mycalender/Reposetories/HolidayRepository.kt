package com.example.mycalender.Reposetories

import com.example.mycalender.DateModels.Event
import com.example.mycalender.HebCalApi.HebcalApiService
import com.example.mycalender.database.EventDao
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class HolidayRepository(
    private val hebcalApi: HebcalApiService,
    private val eventRepository: EventRepository
) {

    suspend fun syncJewishHolidays(year: Int) {
        try {
            val response = hebcalApi.getHolidays(year = year)
            val holidays = response.items.map { hebcalEvent ->
                Event(
                    id = UUID.randomUUID().toString(),
                    eventName = hebcalEvent.title,
                    eventLocation = "",
                    eventColor = 0xFF2196F3.toInt(), // Blue color
                    eventDateFrom = parseDate(hebcalEvent.date),
                    eventDateTo = parseDate(hebcalEvent.date),
                )
            }
            // Insert all holidays
            holidays.forEach { holiday ->
                eventRepository.addEvent(holiday)
            }
        } catch (e: Exception) {
            // Handle error
            e.printStackTrace()
        }
    }

    private fun parseDate(dateString: String): Date {
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return format.parse(dateString) ?: Date()
    }
}