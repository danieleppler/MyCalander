package com.example.mycalender.DateModels

import android.graphics.Color
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date
import java.util.UUID

@Entity
data class Event(
    @PrimaryKey var id: String = UUID.randomUUID().toString(),
    var eventName: String = "Daily",
    var eventLocation: String = "Teams",
    var eventDateFrom: Date = Date(),
    var eventDateTo: Date = Date(),
    var eventColor: Int = Color.BLUE
)