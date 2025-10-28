package com.example.mycalender.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.mycalender.Event
import kotlinx.coroutines.flow.Flow

@Dao
interface EventDao {
    @Query("SELECT * FROM event")
    fun getEvents() : Flow<List<Event>>

    @Query("SELECT * FROM event WHERE id = :eventId")
    fun getEventById(eventId: String): Flow<Event>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addEvent(event:Event)
}