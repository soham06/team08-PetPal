package com.cs446.petpal.views

import androidx.compose.foundation.background
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.cs446.petpal.R
import com.cs446.petpal.viewmodels.TaskspageViewModel
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.material3.OutlinedTextField


@Composable
fun TasksPageView(taskspageViewModel: TaskspageViewModel = viewModel(), navController: NavController) {
    var showDialog by remember { mutableStateOf(false) }
    var userInput by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        TopBar(navController)

        // Main Content
        Box(
            modifier = Modifier
                .weight(1f) // Takes remaining space
                .fillMaxSize()
                .background(Color.White),
            contentAlignment = Alignment.TopStart
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.SpaceEvenly,
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Daily Tasks",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier
                            .align(Alignment.CenterVertically) // Vertically center the text
                            .padding(top = 12.dp)
                    )
                    Button(
                        onClick = { showDialog = true },
                        modifier = Modifier
                            .size(48.dp)
                            .padding(top = 12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFA2D9FF))
                        //contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(
                            text = "",
                            color = Color.White,
                            fontSize = 24.sp
                        )
                    }
                }

                Text(
                    text = "Daily Events",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }

        BottomBar(navController)
    }
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = {
                Text(text = "Add Task")
            },
            text = {
                Column {
                    OutlinedTextField(
                        value = userInput,
                        onValueChange = { userInput = it },
                        label = { Text("Your Input") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDialog = false
                        taskspageViewModel.createTaskForUser(userInput, "OPEN") { success, _ ->
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