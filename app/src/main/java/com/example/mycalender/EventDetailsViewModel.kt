package com.example.mycalender

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mycalender.DateModels.Event
import com.example.mycalender.Reposetories.EventRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

class EventDetailsViewModel() : ViewModel() {

    private var _event: MutableStateFlow<Event?> = MutableStateFlow(Event())
    val event: StateFlow<Event?> = _event.asStateFlow()
    val eventRepository = EventRepository.get()


    fun loadEventById(eventId: UUID) {
        viewModelScope.launch {
            eventRepository.getEventById(eventId.toString()).collect { event ->
                _event.value = event
            }
        }
    }

    fun saveEvent(){
        viewModelScope.launch(Dispatchers.IO) {
            eventRepository.addEvent(event.value!!)
        }

    }

    fun updateEvent(onUpdate: (Event) -> Event) {
        _event.update { oldEvent ->
            oldEvent?.let { onUpdate(it) }
        }
    }
}

