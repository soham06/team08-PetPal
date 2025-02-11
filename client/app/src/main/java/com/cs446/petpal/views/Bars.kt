package com.cs446.petpal.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import com.cs446.petpal.R
import androidx.navigation.NavController


@Composable
fun BottomBarButton(label: String, iconRes: Int) {
    IconButton(
        onClick = { /* Handle click */ },
        modifier = Modifier.size(84.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = label,
                modifier = Modifier.size(24.dp)
            )
            Text(label, color = Color.Black)
        }
    }
}

@Composable
fun TopBar(navController: NavController) {
    var expanded by remember { mutableStateOf(false) }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .background(Color(0xFFA2D9FF)),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(
                onClick = { },
                modifier = Modifier
                    .size(48.dp)
                    .padding(top = 12.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.notif),
                    contentDescription = "Profile",
                    modifier = Modifier.size(24.dp),
                    tint = Color.Black
                )
            }
            Box(modifier = Modifier.weight(1f) .padding(top = 12.dp), contentAlignment = Alignment.Center) {
                Text(
                    text = "PetPal",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    textAlign = TextAlign.Center
                )
            }

            Box(modifier = Modifier.wrapContentSize(Alignment.TopEnd)) {
                IconButton(
                    onClick = { expanded = true },
                    modifier = Modifier
                    .size(48.dp)
                    .padding(top = 12.dp)
            ) {
                    Icon(
                        painter = painterResource(R.drawable.profile),
                        contentDescription = "Profile",
                        modifier = Modifier.size(24.dp),
                        tint = Color.Black
                    )
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.width(150.dp),
                ) {
                    DropdownMenuItem(
                        text = { Text("Logout") },
                        onClick = {
                        expanded = false
                        navController.navigate("landing")
                    })
                }
            }
        }
    }
}

@Composable
fun BottomBar() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(84.dp)
            .background(Color(0xFFA2D9FF)),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomBarButton("Home", R.drawable.home)
            BottomBarButton("Calendar", R.drawable.calendar)
            BottomBarButton("Tasks", R.drawable.tasks)
            BottomBarButton("Pets", R.drawable.pets)
        }
    }
}
