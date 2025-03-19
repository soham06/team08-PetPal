package com.cs446.petpal.observer

import com.cs446.petpal.models.Event

interface EventObserver {
    fun onEventUpdated(events: List<Event>)
}
