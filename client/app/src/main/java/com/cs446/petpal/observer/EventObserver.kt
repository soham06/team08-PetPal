package com.cs446.petpal.observer

import com.cs446.petpal.models.Event

interface EventObserver {
    // Common interface for observer
    fun update(context: EventSubject)

    // Specific interface for Events
    fun getEvents(): MutableList<Event>
}
