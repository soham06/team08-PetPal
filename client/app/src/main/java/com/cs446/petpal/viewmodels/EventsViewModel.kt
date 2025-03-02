package com.cs446.petpal.viewmodels

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONObject
import com.cs446.petpal.models.Event
import org.json.JSONArray
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import java.sql.Date
import java.sql.Time

class EventsViewModel: ViewModel() {
    private val client = OkHttpClient()
    private val _events = mutableStateOf<List<Event>>(emptyList())
    val events: State<List<Event>> = _events
    var selectedEvent: MutableState<Event?> = mutableStateOf(null)

    fun setSelectedEvent(event: Event) {
        selectedEvent.value = event
    }

    init {
        getEventsForUser()
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
        viewModelScope.launch(Dispatchers.IO)
        {
            var successfulEventAdded = false
            var event: Event? = null
            try {
                val json = JSONObject().apply {
                    put("description", description)
                    put("startDate", startDate)
                    put("endDate", endDate)
                    put("startTime", startTime)
                    put("endTime", endTime)
                    put("location", location)
                }
                val requestBody = json.toString()
                    .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
//                // Build the POST request
                val request = Request.Builder()
                    .url("http://10.0.2.2:3000/api/events/PcjsCSow5nnbIFo5cowm") // REPLACE THIS TO ACTUALLY USE USERID
                    .post(requestBody)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Accept", "application/json")
                    .build()
                client.newCall(request).execute().use { response ->
                    successfulEventAdded = response.isSuccessful
                    if (successfulEventAdded) {
                        val responseBody = response.body?.string()
                        val jsonResponse = JSONObject(responseBody ?: "")
                        event = Event(
                            description = mutableStateOf(jsonResponse.optString("description")),
                            startDate = mutableStateOf(jsonResponse.optString("startDate")),
                            endDate = mutableStateOf(jsonResponse.optString("endDate")),
                            startTime = mutableStateOf(jsonResponse.optString("startTime")),
                            endTime = mutableStateOf(jsonResponse.optString("endTime")),
                            location = mutableStateOf(jsonResponse.optString("location")),
                        )
                        event.eventId = jsonResponse.optString("eventId")
                        val updatedEvents = _events.value.toMutableList() // Create a mutable copy
                        updatedEvents.add(event!!)
                        _events.value = updatedEvents
                    }
                    else {
                        // Optionally log error details from response
                        println("Adding event failed: ${response.body?.string()}")
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                successfulEventAdded = false
            }
            onResult(successfulEventAdded, event)
        }
    }

    fun updateEventForUser(
        currEventId: String,
        description: String,
        startDate: String,
        endDate: String,
        startTime: String,
        endTime: String,
        location: String,
        onResult: (Boolean, Event?) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO)
        {
            var successfulEventUpdated = false
            var event: Event? = null
            try {
                val json = JSONObject().apply {
                    put("description", description)
                    put("startDate", startDate)
                    put("endDate", endDate)
                    put("startTime", startTime)
                    put("endTime", endTime)
                    put("location", location)
                }
                val requestBody = json.toString()
                    .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
//                // Build the PATCH request
                val request = Request.Builder()
                    .url("http://10.0.2.2:3000/api/events/${currEventId}")
                    .patch(requestBody)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Accept", "application/json")
                    .build()
                client.newCall(request).execute().use { response ->
                    successfulEventUpdated = response.isSuccessful
                    if (successfulEventUpdated) {
                        val responseBody = response.body?.string()
                        val jsonResponse = JSONObject(responseBody ?: "")
                        event = Event(
                            description = mutableStateOf(jsonResponse.optString("description")),
                            startDate = mutableStateOf(jsonResponse.optString("startDate")),
                            endDate = mutableStateOf(jsonResponse.optString("endDate")),
                            startTime = mutableStateOf(jsonResponse.optString("startTime")),
                            endTime = mutableStateOf(jsonResponse.optString("endTime")),
                            location = mutableStateOf(jsonResponse.optString("location")),
                        )

                        event!!.eventId = jsonResponse.optString("eventId")
                        val updatedEvents = _events.value.toMutableList() // Create a mutable copy
                        val eventIndex = updatedEvents.indexOfFirst { it.eventId == currEventId}
                        if (eventIndex != -1) {
                            updatedEvents[eventIndex] = event!!
                        }
                        _events.value = updatedEvents
                    }
                    else {
                        // Optionally log error details from response
                        println("Updating event failed: ${response.body?.string()}")
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                successfulEventUpdated = false
            }
            onResult(successfulEventUpdated, event)
        }
    }

    fun getEventsForUser() {
        viewModelScope.launch(Dispatchers.IO)
        {
            var successfulEventRetrived = false
            try {
                val request = Request.Builder()
                    .url("http://10.0.2.2:3000/api/events/PcjsCSow5nnbIFo5cowm") // REPLACE THIS TO ACTUALLY USE USERID
                    .get()
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Accept", "application/json")
                    .build()
                client.newCall(request).execute().use { response ->
                    successfulEventRetrived = response.isSuccessful
                    if(successfulEventRetrived) {
                        val responseBody = response.body?.string()
                        val eventsArray = JSONArray(responseBody)

                        val events = mutableListOf<Event>()
                        for (i in 0 until eventsArray.length()) {
                            val eventJson = eventsArray.getJSONObject(i)
                            val event = Event(
                                description = mutableStateOf(eventJson.getString("description")),
                                startDate = mutableStateOf(eventJson.getString("startDate")),
                                endDate = mutableStateOf(eventJson.getString("endDate")),
                                startTime = mutableStateOf(eventJson.getString("startTime")),
                                endTime = mutableStateOf(eventJson.getString("endTime")),
                                location = mutableStateOf(eventJson.getString("location")),
                            )
                            event.eventId = eventJson.optString("eventId")
                            events.add(event)
                        }
                        _events.value = events
                    }
                }
            }
            catch (e: Exception) {
                e.printStackTrace()
                successfulEventRetrived = false
            }
        }
    }

    fun deleteEventForUser(eventId: String, onResult: (Boolean, Event?) -> Unit) {
        viewModelScope.launch(Dispatchers.IO)
        {
            var successfulEventDeleted = false
            try {
                val request = Request.Builder()
                    .url("http://10.0.2.2:3000/api/events/$eventId")
                    .delete()
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Accept", "application/json")
                    .build()
                client.newCall(request).execute().use { response ->
                    successfulEventDeleted = response.isSuccessful
                    if (successfulEventDeleted) {
                        _events.value = _events.value.filterNot { it.eventId == eventId }
                    }
                }
            }
            catch (e: Exception) {
                e.printStackTrace()
                successfulEventDeleted = false
            }
        }
    }
}