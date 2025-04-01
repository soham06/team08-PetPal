package com.cs446.petpal.views

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.cs446.petpal.R
import com.cs446.petpal.viewmodels.HomepagePetsViewModel

@Composable
fun HomepageView(homepagePetsViewModel: HomepagePetsViewModel = hiltViewModel(), navController: NavController) {

    val myPets by homepagePetsViewModel.myPetsList.collectAsState()
    val sharedPets by homepagePetsViewModel.sharedPetsList.collectAsState()
    val upcomingEvents by homepagePetsViewModel.events
    println(upcomingEvents)

    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        TopBar(navController = navController)

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxSize()
                .background(Color.White),
            contentAlignment = Alignment.TopStart
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                item {
                    Text(
                        text = "My Pets",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
                itemsIndexed(myPets) { index, pet ->
                    val petImageRes = when (index) {
                        0 -> R.drawable.pet_max
                        1 -> R.drawable.pet_luna
                        else -> R.drawable.pet_max
                    }
                    PetListItem(
                        petName = pet.name.value,
                        onClick = { Log.d("HomepageView", "Clicked pet: ${pet.petId}")
                            navController.navigate("petspage/${pet.petId}") },
                        petImageResId = petImageRes
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Shared Pets",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
                itemsIndexed(sharedPets) { index, pet ->
                    val petImageRes = when (index) {
                        0 -> R.drawable.pet_toby
                        1 -> R.drawable.pet_luna
                        else -> R.drawable.pet_toby
                    }
                    PetListItem(
                        petName = pet.name.value,
                        onClick = { Log.d("HomepageView", "Clicked pet: ${pet.petId}")
                            navController.navigate("petspage/${pet.petId}") },
                        petImageResId = petImageRes
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Upcoming Events",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
                items(upcomingEvents.take(3)) { event ->
                    EventListItem(
                        event = event,
                    )
                }
            }
        }
        BottomBar(navController)
    }
}

@Composable
fun PetListItem(
    petName: String,
    onClick: () -> Unit,
    petImageResId: Int
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = petImageResId),
                contentDescription = "Pet Image",
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = petName,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                imageVector = Icons.Filled.ArrowForward,
                contentDescription = "Go to Pet Profile",
                tint = Color.Gray
            )
        }
    }
}

@Composable
fun EventListItem(event: com.cs446.petpal.models.Event) {
    val formattedDate = remember(event.startDate.value) {
        parseDateToMonthDay(event.startDate.value)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),

        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_event),
                    contentDescription = "Event Icon",
                    modifier = Modifier.size(40.dp),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = event.description.value,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = event.startTime.value,
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }
            Text(
                text = formattedDate,
                fontSize = 14.sp,
                color = Color.Gray
            )
        }
    }
}

fun parseDateToMonthDay(dateStr: String): String {
    return try {
        val inputFormat = java.text.SimpleDateFormat("MM-dd-yyyy", java.util.Locale.US)
        val outputFormat = java.text.SimpleDateFormat("MMMM d", java.util.Locale.US)
        val date = inputFormat.parse(dateStr)
        if (date != null) {
            outputFormat.format(date)
        } else {
            dateStr
        }
    } catch (e: Exception) {
        dateStr
    }
}
