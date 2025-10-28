package com.example.mycalender

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mycalender.Reposetories.EventRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.lang.Thread.State

class CalenderViewModel : ViewModel() {
    private var _events : MutableStateFlow<List<Event?>> = MutableStateFlow(emptyList())
    val events : StateFlow<List<Event?>> get() = _events.asStateFlow()


    private val evetRepository = EventRepository.get()

    init{
        viewModelScope.launch {
            evetRepository.getEvents().collect{
                _events.value = it
            }
        }
    }


}