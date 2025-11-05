package com.example.mycalender.HebCalApi

import android.util.Log
import com.example.mycalender.DateModels.HebcalResponse
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.concurrent.TimeUnit


interface HebcalApiService {

    @GET("hebcal")
    suspend fun getHolidays(
        @Query("v") version: Int = 1,
        @Query("cfg") config: String = "json",
        @Query("maj") majorHolidays: String = "on",
        @Query("min") minorHolidays: String = "on",
        @Query("mod") modernHolidays: String = "on",
        @Query("nx") roshChodesh: String = "on",
        @Query("year") year: Int,
        @Query("month") month: String = "x",  // x means all months
        @Query("ss") sunsetTime: String = "off",
        @Query("mf") monthFormat: String = "off",
        @Query("c") candles: String = "off",
        @Query("geo") geo: String = "none",
        @Query("M") hebrew: String = "on",
        @Query("s") sedrot: String = "off"
    ): HebcalResponse

    companion object {
        private const val BASE_URL = "https://www.hebcal.com/"

        fun create(): HebcalApiService {
            val TAG = "HebcalApiService"
            Log.d(TAG, "Creating API client")

            // Add logging interceptor
            val loggingInterceptor = HttpLoggingInterceptor { message ->
                Log.d(TAG, "OkHttp: $message")
            }.apply {
                level = HttpLoggingInterceptor.Level.BODY
            }

            val okHttpClient = OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(loggingInterceptor)
                .addInterceptor { chain ->
                    val request = chain.request()
                    Log.d(TAG, "=== REQUEST START ===")
                    Log.d(TAG, "URL: ${request.url}")
                    Log.d(TAG, "Method: ${request.method}")

                    try {
                        val response = chain.proceed(request)
                        Log.d(TAG, "=== RESPONSE START ===")
                        Log.d(TAG, "Code: ${response.code}")
                        Log.d(TAG, "Message: ${response.message}")
                        response
                    } catch (e: Exception) {
                        Log.e(TAG, "=== REQUEST FAILED ===")
                        Log.e(TAG, "Error: ${e.message}", e)
                        throw e
                    }
                }
                .build()

            Log.d(TAG, "OkHttp client created")

            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            Log.d(TAG, "Retrofit instance created")

            return retrofit.create(HebcalApiService::class.java)
        }
    }
}