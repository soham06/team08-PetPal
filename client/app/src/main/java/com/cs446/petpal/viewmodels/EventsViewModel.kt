package com.cs446.petpal.viewmodels

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.cs446.petpal.models.Event
import com.cs446.petpal.observer.EventsObserver
import com.cs446.petpal.observer.EventSubject
import com.cs446.petpal.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.tasks.await

@HiltViewModel
class EventsViewModel @Inject constructor(
    val userRepository: UserRepository,
) : ViewModel() {
    private val eventSubject: EventSubject = EventSubject(userRepository.currentUser.value?.userId ?: "")
    val observer = EventsObserver()
    var selectedEvent: MutableState<Event?> = mutableStateOf(null)
    private val _registrationToken = mutableStateOf("")
    val registrationToken: State<String> = _registrationToken

    init {
        println("User: ${userRepository.currentUser.value}")
        eventSubject.attach(observer) // Register as an observer
        eventSubject.fetchEvents(registrationToken) // Fetch initial events
        fetchFCMToken()
    }

    fun setSelectedEvent(event: Event) {
        selectedEvent.value = event
    }

    fun fetchFCMToken() {
        viewModelScope.launch {
            try {
                val token = FirebaseMessaging.getInstance().token.await()
                Log.d("PushNotification", "FCM Token: $token")
                _registrationToken.value = token
            } catch (e: Exception) {
                Log.e("PushNotification", "FCM token error", e)
            }
        }
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
        eventSubject.createEvent(description, startDate, endDate, startTime,
                                 endTime, location, registrationToken, onResult)
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
        eventSubject.updateEvent(eventId, description, startDate, endDate,
                                 startTime, endTime, location, registrationToken, onResult)
    }

    fun getEventsForUser() {
        eventSubject.fetchEvents(registrationToken)
    }

    fun deleteEventForUser(eventId: String, onResult: (Boolean) -> Unit) {
        eventSubject.deleteEvent(eventId, onResult)
    }

    override fun onCleared() {
        super.onCleared()
        eventSubject.detach(observer) // Detach observer to prevent memory leaks
    }
}
