package com.cs446.petpal.observer

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import androidx.compose.runtime.setValue
import com.cs446.petpal.models.Event

class EventsObserver : EventObserver {
    private var eventsList = mutableStateOf<List<Event>>(emptyList())
    val events: State<List<Event>> get() = eventsList

    override fun update(context: EventSubject) {
        eventsList.value = context.getState()
    }

    override fun getEvents(): MutableList<Event> = eventsList.value.toMutableList()
}
