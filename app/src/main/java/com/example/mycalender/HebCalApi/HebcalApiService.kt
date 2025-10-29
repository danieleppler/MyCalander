package com.example.mycalender.HebCalApi

import com.example.mycalender.DateModels.HebcalResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query


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
        private const val BASE_URL = "https://www.hebcal.com/hebcal/"

        fun create(): HebcalApiService {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(HebcalApiService::class.java)
        }
    }
}