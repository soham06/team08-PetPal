package com.cs446.petpal.observer

import com.cs446.petpal.models.Event

//interface EventObserver {
//    var events: MutableList<Event>
//    fun onEventUpdated(context: MutableList<Event>) {
//        events = context
//    }
//
//    fun getEvents(): MutableList<Event> {
//        return events
//    }
//}

interface EventObserver {
    // Common interface for observer
    fun update(context: EventSubject)

    // Specific interface for Events
    fun getEvents(): MutableList<Event>
}
