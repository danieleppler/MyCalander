package com.example.mycalender

import java.util.Date

data class Task(val TaskName: String,
                val TaskDateFrom : Date,
                val TaskDateTo : Date, val TaskDescription : String){

}