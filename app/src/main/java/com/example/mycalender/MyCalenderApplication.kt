package com.example.mycalender

import android.app.Application
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.example.mycalender.HebCalApi.HebcalApiService
import com.example.mycalender.Reposetories.EventRepository
import com.example.mycalender.Reposetories.HolidayRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.util.Calendar

class MyCalenderApplication : Application() {

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        val context = this
        // Sync holidays when app starts
        applicationScope.launch {
            EventRepository.initialize(context)
            syncHolidays()
        }

    }

    private suspend fun syncHolidays() {
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)

        // Get your database instance
        val eventRepo = EventRepository.get()

        // Create repository
        val hebcalApi = HebcalApiService.create()
        val repository = HolidayRepository(hebcalApi, eventRepo)

        // Delete old holidays and sync new ones
        eventRepo.deleteAllCurrentHolidays()
        repository.syncJewishHolidays(currentYear)
        //repository.syncJewishHolidays(currentYear + 1)
        //repository.syncJewishHolidays(currentYear + 2)
    }
}