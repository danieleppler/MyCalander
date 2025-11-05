package com.example.mycalender.Reposetories

import android.util.Log
import com.example.mycalender.DateModels.Event
import com.example.mycalender.HebCalApi.HebcalApiService
import com.example.mycalender.database.EventDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class HolidayRepository(
    private val hebcalApi: HebcalApiService,
    private val eventRepository: EventRepository
) {

    private val TAG = "HolidayRepository"

    suspend fun syncJewishHolidays(year: Int) {
        try {
            Log.d("Entered sync syncJewishHolidays function",TAG)
            val response = hebcalApi.getHolidays(year = year)
            Log.d("got response",TAG)
            val holidays = response.items.map { hebcalEvent ->
                Event(
                    id = UUID.randomUUID().toString(),
                    eventName = hebcalEvent.title,
                    eventLocation = "",
                    eventColor = 0xFF87CEEB .toInt(), // sky blue color
                    eventDateFrom = parseDate(hebcalEvent.date),
                    eventDateTo = parseDate(hebcalEvent.date),
                )
            }
            // Insert all holidays
            holidays.forEach { holiday ->
                Log.d("holiday fetched : ${holiday.eventName}",TAG)
                eventRepository.addEvent(holiday)
            }
        } catch (e: Exception) {
            Log.d("error caught",TAG)
            e.printStackTrace()
        }
    }

    suspend fun testApiCall() {
        Log.d(TAG, "=== TEST API CALL START ===")
        Log.d(TAG, "Thread: ${Thread.currentThread().name}")

        testBasicConnectivity()
        
        withContext(Dispatchers.IO) {
            Log.d(TAG, "Switched to IO dispatcher: ${Thread.currentThread().name}")

            try {
                Log.d(TAG, "About to call getHolidays...")

                val response = withTimeout(15000L) {
                    hebcalApi.getHolidays(year = 2024)
                }

                Log.d(TAG, "✅ SUCCESS! Got response")
                Log.d(TAG, "Items count: ${response.items.size}")

            } catch (e: TimeoutCancellationException) {
                Log.e(TAG, "❌ TIMEOUT after 15 seconds")
            } catch (e: java.net.UnknownHostException) {
                Log.e(TAG, "❌ UNKNOWN HOST - Check internet connection")
                Log.e(TAG, "Details: ${e.message}")
            } catch (e: java.net.SocketTimeoutException) {
                Log.e(TAG, "❌ SOCKET TIMEOUT")
            } catch (e: retrofit2.HttpException) {
                Log.e(TAG, "❌ HTTP ERROR: ${e.code()}")
                Log.e(TAG, "Message: ${e.message()}")
            } catch (e: Exception) {
                Log.e(TAG, "❌ EXCEPTION: ${e.javaClass.simpleName}")
                Log.e(TAG, "Message: ${e.message}")
                e.printStackTrace()
            }
        }

        Log.d(TAG, "=== TEST API CALL END ===")
    }

    suspend fun testBasicConnectivity() = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Testing Google...")
            val googleUrl = URL("https://www.google.com")
            val connection = googleUrl.openConnection() as HttpURLConnection
            connection.connectTimeout = 5000
            connection.connect()
            Log.d(TAG, "✅ Google works: ${connection.responseCode}")
            connection.disconnect()

            Log.d(TAG, "Testing Hebcal...")
            val hebcalUrl = URL("https://www.hebcal.com")
            val hebcalConnection = hebcalUrl.openConnection() as HttpURLConnection
            hebcalConnection.connectTimeout = 5000
            hebcalConnection.connect()
            Log.d(TAG, "✅ Hebcal works: ${hebcalConnection.responseCode}")
            hebcalConnection.disconnect()

        } catch (e: Exception) {
            Log.e(TAG, "❌ Connectivity test failed: ${e.message}")
            e.printStackTrace()
        }
    }

    private fun parseDate(dateString: String): Date {
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return format.parse(dateString) ?: Date()
    }
}