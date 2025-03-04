package com.cs446.petpal.models

import androidx.compose.runtime.MutableState
import java.sql.Date
import java.sql.Time

data class Event (
    val description: MutableState<String>,
    val startDate: MutableState<String>,
    var endDate: MutableState<String>,
    var startTime: MutableState<String>,
    var endTime: MutableState<String>,
    var location: MutableState<String>,
    var eventId: String = "",
)