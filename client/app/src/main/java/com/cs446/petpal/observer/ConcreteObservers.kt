package com.cs446.petpal.observer

import com.cs446.petpal.models.Event

//class ConcreteObservers(private val onEventUpdate: (List<Event>) -> Unit) : EventObserver {
//    override fun onEventUpdated(events: List<Event>) {
//        onEventUpdate(events)
//    }
//}

class EventsObserver : EventObserver {
    private var eventsList: MutableList<Event> = mutableListOf<Event>()

    override fun update(context: EventSubject) {
        eventsList = context.getState()
    }

    override fun getEvents(): MutableList<Event> = eventsList
}
