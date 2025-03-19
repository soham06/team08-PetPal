package com.cs446.petpal.observer

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.cs446.petpal.models.Event
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Locale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class EventSubject(private val userId: String) {
    private val observers: MutableList<EventObserver> = mutableListOf()
    private val client = OkHttpClient()
    private val events: MutableList<Event> = mutableListOf()

    fun attach(observer: EventObserver) {
        observers.add(observer)
    }

    fun detach(observer: EventObserver) {
        observers.remove(observer)
    }

    private fun notifyObservers() {
        for (observer in observers) {
            observer.onEventUpdated(events)
        }
    }

    private fun convertDateFormat(dateStr: String): String {
        val inputFormat = SimpleDateFormat("MM-dd-yyyy", Locale.US)
        val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val date = inputFormat.parse(dateStr) ?: return ""
        return outputFormat.format(date)
    }

    fun fetchEvents(registrationToken: State<String>) {
        CoroutineScope(Dispatchers.IO).launch {
            var successfulEventRetrieved = false
            try {
                val request = Request.Builder()
                    .url("http://10.0.2.2:3000/api/events/$userId")
                    .get()
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Accept", "application/json")
                    .build()

                withContext(Dispatchers.IO) {
                    client.newCall(request).execute().use { response ->
                        successfulEventRetrieved = response.isSuccessful
                        if (successfulEventRetrieved) {
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
                                    eventId = eventJson.optString("eventId"),
                                    notificationSent = false,
                                    registrationToken = registrationToken.value
                                )
                                withContext(Dispatchers.Main) {
                                    events.add(event)
                                }
                            }
                            withContext(Dispatchers.Main) {
                                notifyObservers()
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                successfulEventRetrieved = false
            }
        }
    }

    fun createEvent(
        description: String,
        startDate: String,
        endDate: String,
        startTime: String,
        endTime: String,
        location: String,
        registrationToken: State<String>,
        onResult: (Boolean, Event?) -> Unit
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val formattedStartDay = convertDateFormat(startDate)
            val formattedEndDay = convertDateFormat(endDate)
            var successfulEventAdded = false
            var event: Event? = null

            try {
                val json = JSONObject().apply {
                    put("description", description)
                    put("startDate", formattedStartDay)
                    put("endDate", formattedEndDay)
                    put("startTime", startTime)
                    put("endTime", endTime)
                    put("location", location)
                    put("registrationToken", registrationToken.value)
                }
                val requestBody = json.toString()
                    .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
                // Build the POST request
                val request = Request.Builder()
                    .url("http://10.0.2.2:3000/api/events/$userId")
                    .post(requestBody)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Accept", "application/json")
                    .build()

                withContext(Dispatchers.IO) {
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
                                eventId = jsonResponse.optString("eventId"),
                                notificationSent = false,
                                registrationToken = registrationToken.value
                            )
                            events.add(event)
                            withContext(Dispatchers.Main) {
                                notifyObservers()
                            }
                        } else {
                            // Optionally log error details from response
                            println("Adding event failed: ${response.body?.string()}")
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                successfulEventAdded = false
            }
            withContext(Dispatchers.Main) {
                onResult(successfulEventAdded, event)
            }
        }
    }

    fun updateEvent(
        eventId: String,
        description: String,
        startDate: String,
        endDate: String,
        startTime: String,
        endTime: String,
        location: String,
        registrationToken: State<String>,
        onResult: (Boolean, Event?) -> Unit
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val formattedStartDay = convertDateFormat(startDate)
            val formattedEndDay = convertDateFormat(endDate)
            var successfulEventUpdated = false
            var event: Event? = null

            try {
                val json = JSONObject().apply {
                    put("description", description)
                    put("startDate", formattedStartDay)
                    put("endDate", formattedEndDay)
                    put("startTime", startTime)
                    put("endTime", endTime)
                    put("location", location)
                    put("registrationToken", registrationToken.value)
                }
                val requestBody = json.toString()
                    .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

                // Build the PATCH request
                val request = Request.Builder()
                    .url("http://10.0.2.2:3000/api/events/${eventId}")
                    .patch(requestBody)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Accept", "application/json")
                    .build()

                withContext(Dispatchers.IO) {
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
                                eventId = jsonResponse.optString("eventId"),
                                notificationSent = false,
                                registrationToken = registrationToken.value
                            )
                            withContext(Dispatchers.Main) {
                                fetchEvents(registrationToken) // Refresh events after update
                            }
                        } else {
                            // Optionally log error details from response
                            println("Updating event failed: ${response.body?.string()}")
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                successfulEventUpdated = false
            }
            withContext(Dispatchers.Main) {
                onResult(successfulEventUpdated, event)
            }
        }
    }

    fun deleteEvent(eventId: String, onResult: (Boolean) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            var successfulEventDeleted = false
            try {
                val request = Request.Builder()
                    .url("http://10.0.2.2:3000/api/events/$eventId")
                    .delete()
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Accept", "application/json")
                    .build()
                withContext(Dispatchers.IO) {
                    client.newCall(request).execute().use { response ->
                        successfulEventDeleted = response.isSuccessful
                        if (successfulEventDeleted) {
                            events.removeIf { it.eventId == eventId }
                            withContext(Dispatchers.Main) {
                                notifyObservers()
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                successfulEventDeleted = false
            }
            withContext(Dispatchers.Main) {
                onResult(successfulEventDeleted)
            }
        }
    }
}
