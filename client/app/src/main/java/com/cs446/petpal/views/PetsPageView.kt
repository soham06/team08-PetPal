package com.cs446.petpal.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.cs446.petpal.R
import com.cs446.petpal.models.Pet
import com.cs446.petpal.viewmodels.PetspageViewModel
import com.cs446.petpal.views.TasksPage.eventsPopup

@Composable
fun PetsPageView(
    petspageViewModel: PetspageViewModel = viewModel(),
    navController: NavController
) {
    // 1) Collect the flows from the ViewModel
    val pets by petspageViewModel.petsList.collectAsState()
    val selectedPet by petspageViewModel.selectedPet.collectAsState()
    val testUserID = "CgL0tQ81vTFMGn2DyA9M"
    // 2) Trigger network fetch once on screen load
    LaunchedEffect(Unit) {
        // Replace with the real userId once you have it
        petspageViewModel.fetchPetsFromServer(testUserID)
    }

    val petToShow = selectedPet ?: pets.firstOrNull()

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top Bar
            TopBar(navController)

            Spacer(modifier = Modifier.height(16.dp))

            // Pet Selection Row
            PetSelectionRow(
                pets = pets,
                selectedPet = petToShow?.petId ?: "",
                onPetSelected = { petspageViewModel.selectPet(it) },
                modifier = Modifier.padding(start = 16.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalAlignment = Alignment.Start
            ) {
                // Pet Image section in PetsPageView
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.profile_pic_main),
                        contentDescription = "Pet Profile Picture",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clip(RoundedCornerShape(16.dp)),
                        contentScale = ContentScale.Crop
                    )
                }


                Spacer(modifier = Modifier.height(12.dp))

                // Pet Info Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Pet Name
                        Text(
                            text = petToShow?.name?.value ?: "No Pet Selected",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )

                        // Info rows
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                PetInfoRow("Gender", (petToShow?.gender?.value ?: "--"), "gender_icon")
                                PetInfoRow("Age", "${petToShow?.age?.value ?: "--"} years", "calendar_icon")
                            }
                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                PetInfoRow("Birthday", petToShow?.birthday ?: "--", "birthday_icon")
                                PetInfoRow("Weight", "${petToShow?.weight?.value ?: "--"} lbs", "weight_icon")
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Insurance Info Card
                InsuranceInfoCard(petToShow) {
                    // Handle insurance edit if needed
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Medication Info Card
                MedicationInfoCard(petToShow) {
                    // Handle medication edit if needed
                }
            }

            // Bottom Bar
            BottomBar(navController)
        }

        // Floating Action Button
        FloatingActionButton(
            onClick = { /* Navigate to Delete Pet */ },
            containerColor = Color(0xFF64B5F6),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 95.dp)
                .size(48.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Delete,
                contentDescription = "Add Pet",
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

// Pet Selection Row
@Composable
fun PetSelectionRow(
    pets: List<Pet>,
    selectedPet: String,
    onPetSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showAddDialog by remember { mutableStateOf(false) }
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start
    ) {
        pets.forEach { pet ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .clickable { onPetSelected(pet.petId) }
            ) {
                // Use a custom image for each pet
                Image(
                    painter = painterResource(id = getPetProfilePic(pet.name.value)),
                    contentDescription = "Profile picture for ${pet.name.value}",
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                Text(
                    text = pet.name.value,
                    fontSize = 12.sp,
                    fontWeight = if (selectedPet == pet.petId) FontWeight.Bold else FontWeight.Normal
                )
            }
        }

        Spacer(modifier = Modifier.weight(0.9f))

        IconButton(
            onClick = { showAddDialog = true },
            modifier = Modifier
                .size(48.dp)
                .padding(top = 12.dp, end = 12.dp),
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
    if (showAddDialog) {
        showAddDialog = petsPopup(null, null,"ADD")
    }
}


// Pet Info Row - using Image to preserve icon color
@Composable
fun PetInfoRow(label: String, value: String, iconRes: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = getIconPlaceholder(iconRes)),
            contentDescription = label,
            modifier = Modifier
                .size(38.dp)
                .padding(end = 8.dp),
            contentScale = ContentScale.Fit
        )
        Column {
            Text(text = label, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Text(text = value, fontSize = 14.sp, fontWeight = FontWeight.Normal)
        }
    }
}

// Insurance Info Card
@Composable
fun InsuranceInfoCard(
    pet: Pet?,
    onEditClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Insurance",
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = "Edit",
                    style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.primary),
                    modifier = Modifier.clickable { onEditClick() }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Info Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Left Column
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = getIconPlaceholder("insurance_icon")),
                            contentDescription = "Provider",
                            modifier = Modifier.size(38.dp),
                            contentScale = ContentScale.Fit
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            Text(text = "Provider", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            Text(text = pet?.insuranceProvider?.value ?: "--", fontSize = 12.sp)
                        }
                    }
                }

                // Right Column
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = getIconPlaceholder("policy_icon")),
                            contentDescription = "Policy #",
                            modifier = Modifier.size(38.dp),
                            contentScale = ContentScale.Fit
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            Text(text = "Policy #", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            Text(text = pet?.policyNumber?.value ?: "--", fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}

// Medication Info Card
@Composable
fun MedicationInfoCard(
    pet: Pet?,
    onEditClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Medication",
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = "Edit",
                    style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.primary),
                    modifier = Modifier.clickable { onEditClick() }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Info Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Left Column
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = getIconPlaceholder("medication_icon")),
                            contentDescription = "Medication Name",
                            modifier = Modifier.size(38.dp),
                            contentScale = ContentScale.Fit
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            Text(text = "Name", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            Text(text = pet?.medicationName?.value ?: "--", fontSize = 12.sp)
                        }
                    }
                }

                // Right Column
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = getIconPlaceholder("dosage_icon")),
                            contentDescription = "Dosage",
                            modifier = Modifier.size(38.dp),
                            contentScale = ContentScale.Fit
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            Text(text = "Dosage", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            Text(text = pet?.medicationDosage?.value ?: "--", fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}

// Icon placeholders
fun getIconPlaceholder(iconName: String): Int {
    return when (iconName) {
        "gender_icon" -> R.drawable.ic_gender
        "calendar_icon" -> R.drawable.ic_age
        "birthday_icon" -> R.drawable.ic_birthday
        "weight_icon" -> R.drawable.ic_weight
        "insurance_icon" -> R.drawable.ic_insurance
        "policy_icon" -> R.drawable.ic_policy
        "medication_icon" -> R.drawable.ic_medication
        //"dosage_icon" -> R.drawable.ic_dosage
        else -> R.drawable.ic_default
    }
}
//temp function for profile pics
fun getPetProfilePic(petName: String): Int {
    return when (petName) {
        "Toby" -> R.drawable.pet_toby
        "Max" -> R.drawable.pet_max
        "Luna" -> R.drawable.pet_luna
        else -> R.drawable.profile_pic_default
    }
}