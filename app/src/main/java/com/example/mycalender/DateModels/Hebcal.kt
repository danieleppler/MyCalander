package com.example.mycalender.DateModels


data class HebcalResponse(
    val items: List<HebcalEvent>
)

data class HebcalEvent(
    val title: String,
    val date: String,  // Format: "2024-09-16"
    val category: String,
    val hebrew: String?
)