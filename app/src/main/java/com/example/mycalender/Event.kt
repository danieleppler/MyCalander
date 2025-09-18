package com.example.mycalender

import java.util.Date

class Event(val eventName: String,
                 val eventLocation:String,
                 val eventDate : Date,
                 val isEventPublic : Boolean) : CalenderItem(itemName = eventName, itemDate = eventDate)