package com.cs446.petpal.views

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.* // Box, Column, Row, Spacer, etc.
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.hilt.navigation.compose.hiltViewModel
import com.cs446.petpal.R
import androidx.compose.foundation.Image

import com.cs446.petpal.viewmodels.HomepagePetsViewModel

@Composable
fun HomepageView(homepagePetsViewModel: HomepagePetsViewModel = hiltViewModel(), navController: NavController) {
    // Retrieve pet lists and upcoming events from the ViewModel
    val myPets by homepagePetsViewModel.myPetsList.collectAsState()
    val sharedPets by homepagePetsViewModel.sharedPetsList.collectAsState()
    val upcomingEvents by homepagePetsViewModel.upcomingEvents.collectAsState()

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
                        onClick = { navController.navigate("petspage/${pet.petId}") },
                        petImageResId = petImageRes
                    )
                }

                // Shared Pets Section
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
                        0 -> R.drawable.pet_max
                        1 -> R.drawable.pet_luna
                        else -> R.drawable.pet_max
                    }
                    PetListItem(
                        petName = pet.name.value,
                        onClick = { navController.navigate("petspage/${pet.petId}") },
                        petImageResId = petImageRes
                    )
                }

                // Upcoming Events Section
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
                items(upcomingEvents) { event ->
                    EventListItem(
                        event = event,
                    )
                }
            }
        }

        // Footer (Bottom Bar)
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
            //height(100.dp),
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
            // Display hardcoded pet image
            Image(
                painter = painterResource(id = petImageResId),
                contentDescription = "Pet Image",
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(8.dp))
            // Pet name
            Text(
                text = petName,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.weight(1f))
            // Arrow icon for navigation
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
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
            //.height(80.dp),
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
                painter = painterResource(id = R.drawable.ic_event), // Replace with your logo resource
                contentDescription = "Event Logo",
                modifier = Modifier.size(40.dp),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = event.description.value,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${event.startDate.value} ${event.startTime.value}",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

/* NOT HARDCODED FOR IMAGES
package com.cs446.petpal.views

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.* // Box, Column, Row, Spacer, etc.
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavController
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.Image
import com.cs446.petpal.R

import com.cs446.petpal.viewmodels.HomepagePetsViewModel

@Composable
fun HomepageView(homepagePetsViewModel: HomepagePetsViewModel = hiltViewModel(), navController: NavController) {
    // Retrieve pet lists and upcoming events from the ViewModel
    val myPets by homepagePetsViewModel.myPetsList.collectAsState()
    val sharedPets by homepagePetsViewModel.sharedPetsList.collectAsState()
    val upcomingEvents by homepagePetsViewModel.upcomingEvents.collectAsState()


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
                // **My Pets Section**
                item {
                    Text(
                        text = "My Pets",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
                items(myPets) { pet ->
                    PetListItem(
                        petName = pet.name.value,  // Access pet name from MutableState
                        onClick = {navController.navigate("petspage/${pet.petId}")}
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
                items(sharedPets) { pet ->
                    PetListItem(
                        petName = pet.name.value,
                        onClick = {navController.navigate("petspage/${pet.petId}") }
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
                items(upcomingEvents) { event ->
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
fun PetListItem(petName: String, onClick: () -> Unit) {
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

            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(Color.Gray),

            )
            Spacer(modifier = Modifier.width(8.dp))
            // Pet name
            Text(
                text = petName,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.weight(1f))
            // Arrow icon for navigation
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
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
            //.height(80.dp),
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
                painter = painterResource(id = R.drawable.ic_event), // Replace with your logo
                contentDescription = "Event Logo",
                modifier = Modifier
                    .size(40.dp),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(8.dp))
            // Column for event name and start date/time
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = event.description.value,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${event.startDate.value} ${event.startTime.value}",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
        }
    }
}
*/