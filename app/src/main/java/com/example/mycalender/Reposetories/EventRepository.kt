package com.example.mycalender.Reposetories

import android.content.Context
import androidx.room.Room
import com.example.mycalender.DateModels.Event
import com.example.mycalender.database.EventDataBase

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow

private const val DB_NAME = "event"
private const val DATABASE_FILE_NAME = "events-database.db"

class EventRepository private constructor(context: Context,
                                          private val coroutineScope : CoroutineScope = GlobalScope){
    private val database:EventDataBase = Room.databaseBuilder(context.applicationContext,EventDataBase::class.java,
        DB_NAME)
        .createFromAsset(DATABASE_FILE_NAME)
        .fallbackToDestructiveMigration()
        .build()

    fun addEvent(event: Event){
        database.EventDao().addEvent(event)
    }

    fun getEvents() : Flow<List<Event>> = database.EventDao().getEvents()

    fun getEventById(id:String) : Flow<Event> = database.EventDao().getEventById(id)

    fun deleteAllCurrentHolidays():Unit = database.EventDao().deleteAllHolidays()

    companion object{
        private var INSTANCE : EventRepository? = null

        fun initialize(context:Context){
            if(INSTANCE == null){
                INSTANCE = EventRepository(context)
            }
        }

        fun get():EventRepository{
            return INSTANCE?: throw IllegalStateException("EventRepository must be initialized")
        }
    }

}