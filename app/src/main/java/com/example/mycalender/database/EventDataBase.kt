package com.example.mycalender.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.mycalender.Event


@Database(entities = [Event::class], version = 2)
@TypeConverters(EventTypeConverters::class)
abstract class EventDataBase :RoomDatabase(){
    abstract fun EventDao():EventDao
}