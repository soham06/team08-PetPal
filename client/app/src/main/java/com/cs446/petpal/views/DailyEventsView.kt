package com.cs446.petpal.views

import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.layout.Arrangement
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
import com.cs446.petpal.viewmodels.TaskspageViewModel
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@Composable
fun DailyEventsView(taskspageViewModel: TaskspageViewModel = viewModel()) {
    var showDialog by remember { mutableStateOf(false) }
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
                .align(Alignment.CenterVertically) // Vertically center the text
                .padding(top = 12.dp)
                .padding(start = 8.dp)
        )
        IconButton(
            onClick = { showDialog = true },
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
}