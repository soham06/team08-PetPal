package com.cs446.petpal.views.TasksPage

import androidx.compose.material3.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
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
import com.cs446.petpal.viewmodels.TasksViewModel
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.material3.OutlinedTextField
import com.cs446.petpal.models.Task
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun DailyTasksView(tasksViewModel: TasksViewModel = hiltViewModel()) {
    var currTask = tasksViewModel.selectedTask.value
    var showAddDialog by remember { mutableStateOf(false) }
    var showDelDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var userInput by remember { mutableStateOf("") }
    val tasks by tasksViewModel.tasks
    val scrollState = rememberScrollState()
    var currTaskID by remember { mutableStateOf("")}
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "Daily Tasks",
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
                .padding(top = 12.dp, end = 12.dp),
            colors = IconButtonDefaults.iconButtonColors(containerColor = Color(0xFFA2D9FF))
        ) {
            Icon(
                painter = painterResource(R.drawable.add),
                contentDescription = "Add Task",
                modifier = Modifier.size(24.dp),
                tint = Color.Black
            )
        }
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 250.dp)
            .heightIn(max = 250.dp)
            .verticalScroll(scrollState)
    )
    {
        tasks.forEach { task ->
            val isChecked = task.status.value == "CLOSED"
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 3.dp, horizontal = 6.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFD700))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = isChecked,
                            onCheckedChange = { isCheckedNew ->
                                task.status.value = if (isCheckedNew) "CLOSED" else "OPEN"
                                tasksViewModel.updateTaskForUser(task.taskId, task.description.value, task.status.value) { success, _ ->
                                }
                            }
                        )
                        Text(
                            text = task.description.value,
                            style = MaterialTheme.typography.bodyLarge.copy(fontSize = 20.sp),
                            modifier = Modifier
                                .padding(start = 8.dp)
                                .weight(1f),
                            maxLines = Int.MAX_VALUE,
                            softWrap = true
                        )
                    }

                    Row(
                        modifier = Modifier.padding(start = 8.dp),
                    ) {
                        IconButton(
                            onClick = {
                                showEditDialog = true
                                tasksViewModel.setSelectedTask(task)
                                currTaskID = task.taskId
                            },
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .size(36.dp),
                            colors = IconButtonDefaults.iconButtonColors(
                                containerColor = Color(0xFFFEFCF5)
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
                                currTaskID = task.taskId
                            },
                            modifier = Modifier.size(36.dp),
                            colors = IconButtonDefaults.iconButtonColors(
                                containerColor = Color(0xFFFEFCF5)
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
    if (showDelDialog) {
        AlertDialog(
            onDismissRequest = { showDelDialog = false },
            title = {
                Text(text = "Delete Task?")
                    },
            text = {
                Text(text = "Are you sure you want to delete this task?")
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
                        tasksViewModel.deleteTaskForUser(currTaskID) { success, _ ->

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
        }

        if (showEditDialog) {
            if (currTask != null) {
                userInput = currTask.description.value
            }
            AlertDialog(
                onDismissRequest = { showEditDialog = false },
                title = {
                    Text(text = "Edit Task")
                },
                text = {
                    Column {
                        OutlinedTextField(
                            value = userInput,
                            onValueChange = { userInput = it },
                            label = { Text("Description") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showEditDialog = false
                            tasksViewModel.updateTaskForUser(currTaskID, userInput, "OPEN") { success, _ ->
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
        }


    if (showAddDialog) {
        userInput = ""
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = {
                Text(text = "Add Task")
            },
            text = {
                Column {
                    OutlinedTextField(
                        value = userInput,
                        onValueChange = { userInput = it },
                        label = { Text("Description") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showAddDialog = false
                        tasksViewModel.createTaskForUser(userInput, "OPEN") { success, _ ->
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
    }
}
