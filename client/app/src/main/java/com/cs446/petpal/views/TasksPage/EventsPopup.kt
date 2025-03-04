package com.cs446.petpal.views.TasksPage

import androidx.compose.material3.*
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.cs446.petpal.viewmodels.EventsViewModel
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.AccessTime
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.util.Log
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.ui.platform.LocalContext
import com.cs446.petpal.models.Event

@Composable
fun eventsDelPopup(currEventId: String, eventsViewModel: EventsViewModel = viewModel()): Boolean {
    var showDelDialog by remember { mutableStateOf(true) }
    AlertDialog(
        onDismissRequest = { showDelDialog = false },
        title = {
            Text(text = "Delete Event?")
        },
        text = {
            Text(text = "Are you sure you want to delete this event?")
        },
        dismissButton = {
            Button(
                onClick = {
                    showDelDialog = false
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700))
            ) {
                Text(
                    text = "Cancel",
                    color = Color.Black,
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    showDelDialog = false
                    eventsViewModel.deleteEventForUser(currEventId) { success, _ ->

                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700))
            ) {
                Text(
                    text = "Delete",
                    color = Color.Black,
                )
            }
        }
    )
    return showDelDialog
}
@Composable
fun eventsPopup(currEvent: Event?, currEventId: String?, popupType: String, eventsViewModel: EventsViewModel = viewModel()): Boolean {
    var showDialog by remember { mutableStateOf(true) }
    var userInputDescription by remember { mutableStateOf(currEvent?.description?.value ?: "") }
    var userInputLocation by remember { mutableStateOf(currEvent?.location?.value ?: "") }
    val context = LocalContext.current
    val dateFormat = SimpleDateFormat("MM-dd-yyyy", Locale.getDefault())
    val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
    val calendar = Calendar.getInstance()
    var startDate by remember { mutableStateOf(currEvent?.startDate?.value ?:dateFormat.format(calendar.time)) }
    var endDate by remember { mutableStateOf(currEvent?.endDate?.value ?:dateFormat.format(calendar.time)) }
    var startTime by remember { mutableStateOf(currEvent?.startTime?.value ?:timeFormat.format(calendar.time)) }
    var endTime by remember { mutableStateOf(currEvent?.endTime?.value ?:timeFormat.format(calendar.time)) }
    val startDatePickerDialog = remember {
        DatePickerDialog(
            context,
            { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
                startDate = dateFormat.format(Calendar.getInstance().apply {
                    set(year, month, dayOfMonth)
                }.time)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
    }
    val endDatePickerDialog = remember {
        DatePickerDialog(
            context,
            { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
                endDate = dateFormat.format(Calendar.getInstance().apply {
                    set(year, month, dayOfMonth)
                }.time)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
    }

    val startTimePickerDialog = remember {
        TimePickerDialog(
            context,
            { _: TimePicker, hour: Int, minute: Int ->
                startTime = timeFormat.format(Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, hour)
                    set(Calendar.MINUTE, minute)
                }.time)
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            false // 12-hour format
        )
    }
    val endTimePickerDialog = remember {
        TimePickerDialog(
            context,
            { _: TimePicker, hour: Int, minute: Int ->
                endTime = timeFormat.format(Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, hour)
                    set(Calendar.MINUTE, minute)
                }.time)
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            false // 12-hour format
        )
    }
    AlertDialog(
        onDismissRequest = { showDialog = false },
        title = {
            Text(text = "Add Event")
        },
        text = {
            Column {
                OutlinedTextField(
                    value = userInputDescription,
                    onValueChange = { userInputDescription = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = startDate,
                    onValueChange = {},
                    label = { Text("Start Date") },
                    readOnly = true,
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = "Calendar Icon",
                            modifier = Modifier.clickable { startDatePickerDialog.show() }
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { startDatePickerDialog.show() }
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = endDate,
                    onValueChange = {},
                    label = { Text("End Date") },
                    readOnly = true,
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = "Calendar Icon",
                            modifier = Modifier.clickable { endDatePickerDialog.show() }
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { endDatePickerDialog.show() }
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    // Start Time Picker
                    Column(modifier = Modifier.weight(1f)) {
                        OutlinedTextField(
                            value = startTime,
                            onValueChange = {},
                            label = { Text("Start Time") },
                            readOnly = true,
                            trailingIcon = {
                                Icon(
                                    imageVector = Icons.Default.AccessTime,
                                    contentDescription = "Time Icon",
                                    modifier = Modifier.clickable { startTimePickerDialog.show() }
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { startTimePickerDialog.show() }
                        )
                    }

                    // End Time Picker
                    Column(modifier = Modifier.weight(1f)) {
                        OutlinedTextField(
                            value = endTime,
                            onValueChange = {},
                            label = { Text("End Time") },
                            readOnly = true,
                            trailingIcon = {
                                Icon(
                                    imageVector = Icons.Default.AccessTime,
                                    contentDescription = "Time Icon",
                                    modifier = Modifier.clickable { endTimePickerDialog.show() }
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { endTimePickerDialog.show() }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = userInputLocation,
                    onValueChange = { userInputLocation = it },
                    label = { Text("Location") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        dismissButton = {
            Button(
                onClick = {
                    showDialog = false
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700))
            ) {
                Text(
                    text = "Cancel",
                    color = Color.Black,
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    showDialog = false
                    if (popupType == "ADD") {
                        eventsViewModel.createEventForUser(
                            userInputDescription.toString(),
                            startDate,
                            endDate,
                            startTime,
                            endTime,
                            userInputLocation,
                        ) { success, _ ->
                        }
                    }
                    else {
                        if (currEventId != null) {
                            eventsViewModel.updateEventForUser(
                                currEventId,
                                userInputDescription.toString(),
                                startDate,
                                endDate,
                                startTime,
                                endTime,
                                userInputLocation,
                            ) { success, _ ->
                            }
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700))
            ) {
                Text(
                    text = "Ok",
                    color = Color.Black,
                )
            }
        }
    )
    return showDialog
}
