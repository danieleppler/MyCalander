package com.example.mycalender

import android.app.Application
import android.util.Log
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

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val TAG = "MyCalenderApplication"

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "=== APPLICATION STARTING ===")

        val context = this

        applicationScope.launch {
            Log.d(TAG, "Coroutine started in scope")

            try {
                // Initialize repository
                Log.d(TAG, "Initializing EventRepository...")
                EventRepository.initialize(context)
                Log.d(TAG, "EventRepository initialized ✓")

                // Sync holidays
                Log.d(TAG, "Starting holiday sync...")
                syncHolidays()
                Log.d(TAG, "Holiday sync completed ✓")

            } catch (e: Exception) {
                Log.e(TAG, "Error in onCreate: ${e.message}", e)
            }
        }

        Log.d(TAG, "=== APPLICATION ONCREATE FINISHED ===")
    }

    private suspend fun syncHolidays() {
        Log.d(TAG, "=== SYNC HOLIDAYS START ===")

        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        Log.d(TAG, "Current year: $currentYear")

        try {
            // Get database instance
            Log.d(TAG, "Getting EventRepository...")
            val eventRepo = EventRepository.get()

            // Create API service
            Log.d(TAG, "Creating HebcalApiService...")
            val hebcalApi = HebcalApiService.create()
            Log.d(TAG, "HebcalApiService created ✓")

            // Create repository
            Log.d(TAG, "Creating HolidayRepository...")
            val repository = HolidayRepository(hebcalApi, eventRepo)
            Log.d(TAG, "HolidayRepository created ✓")

            // TEST API FIRST
            Log.d(TAG, "Testing API connection...")
            repository.testApiCall()
            Log.d(TAG, "API test completed ✓")

            // Delete old holidays
            Log.d(TAG, "Deleting old holidays...")
            eventRepo.deleteAllCurrentHolidays()
            Log.d(TAG, "Old holidays deleted ✓")

            // Sync new holidays
            Log.d(TAG, "Syncing holidays for year $currentYear...")
            repository.syncJewishHolidays(currentYear)

            Log.d(TAG, "Syncing holidays for year ${currentYear + 1}...")
            //repository.syncJewishHolidays(currentYear + 1)

            Log.d(TAG, "Syncing holidays for year ${currentYear + 2}...")
            //repository.syncJewishHolidays(currentYear + 2)

            Log.d(TAG, "All holidays synced successfully ✓")

        } catch (e: Exception) {
            Log.e(TAG, "Error syncing holidays: ${e.javaClass.simpleName}")
            Log.e(TAG, "Message: ${e.message}")
            e.printStackTrace()
        }

        Log.d(TAG, "=== SYNC HOLIDAYS END ===")
    }
}