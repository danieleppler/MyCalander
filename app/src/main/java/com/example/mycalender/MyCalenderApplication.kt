package com.example.mycalender

import android.app.Application
import com.example.mycalender.Reposetories.EventRepository

class MyCalenderApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        EventRepository.initialize(this)
    }
}