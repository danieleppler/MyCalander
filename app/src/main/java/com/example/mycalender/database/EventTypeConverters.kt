package com.example.mycalender.database


import android.graphics.Color
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.TypeConverter
import java.util.Date
import androidx.core.graphics.toColorInt
import java.nio.ByteBuffer
import java.util.UUID

class EventTypeConverters {


    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun fromUUID(uuid: String?): UUID? {
        return uuid?.let { UUID.fromString(it) }
    }

    @TypeConverter
    fun toUUID(uuid: UUID?): String? {
        return uuid?.toString()
    }


}