package com.cs446.petpal.views.TasksPage

import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.cs446.petpal.R
import com.cs446.petpal.viewmodels.EventsViewModel
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import com.cs446.petpal.models.Event
import java.util.Date

@Composable
fun DailyEventsView(eventsViewModel: EventsViewModel = viewModel()) {
    var currEvent = eventsViewModel.selectedEvent.value
    var showAddDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showDelDialog by remember { mutableStateOf(false) }
    var currEventID by remember { mutableStateOf("")}
    val calendar = Calendar.getInstance()
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val tomorrow = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, 1) }

    val displayFormat = SimpleDateFormat("MMMM d yyyy", Locale.getDefault())
    val events by eventsViewModel.events
    val scrollState = rememberScrollState()
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "Daily Events",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .padding(top = 12.dp)
                .padding(start = 8.dp)
        )
        IconButton(
            onClick = { showAddDialog = true },
            modifier = Modifier
                .size(48.dp)
                .padding(top = 12.dp),
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = Color(
                    0xFFA2D9FF
                )
            )
        ) {
            Icon(
                painter = painterResource(R.drawable.add),
                contentDescription = "Add Task",
                modifier = Modifier.size(24.dp),
                tint = Color.Black
            )
        }
    }
    Spacer(modifier = Modifier.height(8.dp))
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 330.dp)
            .heightIn(max = 330.dp)
            .verticalScroll(scrollState)
    )
    {
        // Display each task description in the Row
        events.forEach { event ->
            val eventStartDate = dateFormat.parse(event.startDate.value) // Parse event date
            val eventEndDate = dateFormat.parse(event.endDate.value)
            val formattedStartDate = when {
                eventStartDate == null -> event.startDate.value // Fallback to raw date if parsing fails
                isSameDay(eventStartDate, calendar.time) -> "Today"
                isSameDay(eventStartDate, tomorrow.time) -> "Tomorrow"
                else -> displayFormat.format(eventStartDate)
            }
            val formattedEndDate = when {
                eventEndDate == null -> event.endDate.value // Fallback to raw date if parsing fails
                isSameDay(eventEndDate, calendar.time) -> "Today"
                isSameDay(eventEndDate, tomorrow.time) -> "Tomorrow"
                else -> displayFormat.format(eventEndDate)
            }
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 3.dp, horizontal = 6.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFD700))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row (
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "From:",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Text(
                            text = formattedStartDate,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Text(
                            text = event.startTime.value,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                    Row (
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "To:",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Text(
                            text = formattedEndDate,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Text(
                            text = event.endTime.value,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row (
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = event.description.value,
                            style = MaterialTheme.typography.bodyLarge.copy(fontSize = 16.sp)
                        )
                        Row {
                            IconButton(
                                onClick = {
                                    showEditDialog = true
                                    currEvent = event
                                    eventsViewModel.setSelectedEvent(event)
                                    currEventID = event.eventId
                                },
                                modifier = Modifier
                                    .padding(end = 8.dp)
                                    .size(36.dp),
                                colors = IconButtonDefaults.iconButtonColors(
                                    containerColor = Color(
                                        0xFFFEFCF5
                                    )
                                )
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.edit),
                                    contentDescription = "Edit Task",
                                    modifier = Modifier.size(24.dp),
                                    tint = Color.Black
                                )
                            }
                            IconButton(
                                onClick = {
                                    showDelDialog = true
                                    currEventID = event.eventId
                                },
                                modifier = Modifier
                                    .size(36.dp),
                                colors = IconButtonDefaults.iconButtonColors(
                                    containerColor = Color(
                                        0xFFFEFCF5
                                    )
                                )
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.delete),
                                    contentDescription = "Delete Task",
                                    modifier = Modifier.size(24.dp),
                                    tint = Color.Black
                                )
                            }
                        }
                    }
                }
            }
        }
    }
    if (showAddDialog) {
        showAddDialog = eventsPopup(null, null,"ADD")
    }
    if (showDelDialog) {
        showDelDialog = eventsDelPopup(currEventID)
    }
    if (showEditDialog) {
        showEditDialog = eventsPopup(currEvent, currEventID, "EDIT")
    }
}
private fun isSameDay(date1: Date, date2: Date): Boolean {
    val cal1 = Calendar.getInstance().apply { time = date1 }
    val cal2 = Calendar.getInstance().apply { time = date2 }
    return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
            cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
}