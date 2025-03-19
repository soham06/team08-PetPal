package com.cs446.petpal.viewmodels

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.cs446.petpal.models.Event
import com.cs446.petpal.observer.EventObserver
import com.cs446.petpal.observer.EventSubject
import com.cs446.petpal.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class EventsViewModel @Inject constructor(
    val userRepository: UserRepository
) : ViewModel(), EventObserver {
    private val eventSubject = EventSubject(userRepository.currentUser.value?.userId ?: "")
    private val _events = mutableStateOf<List<Event>>(emptyList())
    val events: State<List<Event>> = _events
    var selectedEvent = mutableStateOf<Event?>(null)

    init {
        eventSubject.attach(this) // Register as an observer
        eventSubject.fetchEvents() // Fetch initial events
    }

    override fun onEventUpdated(events: List<Event>) {
        _events.value = events
    }

    fun setSelectedEvent(event: Event) {
        selectedEvent.value = event
    }

    fun createEventForUser(
        description: String,
        startDate: String,
        endDate: String,
        startTime: String,
        endTime: String,
        location: String,
        onResult: (Boolean, Event?) -> Unit
    ) {
        eventSubject.createEvent(description, startDate, endDate, startTime, endTime, location, onResult)
    }

    fun updateEventForUser(
        eventId: String,
        description: String,
        startDate: String,
        endDate: String,
        startTime: String,
        endTime: String,
        location: String,
        onResult: (Boolean, Event?) -> Unit
    ) {
        eventSubject.updateEvent(eventId, description, startDate, endDate, startTime, endTime, location, onResult)
    }

    fun deleteEventForUser(eventId: String, onResult: (Boolean) -> Unit) {
        eventSubject.deleteEvent(eventId, onResult)
    }

    override fun onCleared() {
        super.onCleared()
        eventSubject.detach(this) // Detach observer to prevent memory leaks
    }
}
