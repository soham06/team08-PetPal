package com.cs446.petpal.observer

import com.cs446.petpal.models.Event

interface EventObserver {
    fun update(context: EventSubject)
    fun getEvents(): MutableList<Event>
}
