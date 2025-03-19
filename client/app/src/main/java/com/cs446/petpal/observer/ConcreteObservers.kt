package com.cs446.petpal.observer

import com.cs446.petpal.models.Event

class ConcreteObservers(private val onEventUpdate: (List<Event>) -> Unit) : EventObserver {
    override fun onEventUpdated(events: List<Event>) {
        onEventUpdate(events)
    }
}
