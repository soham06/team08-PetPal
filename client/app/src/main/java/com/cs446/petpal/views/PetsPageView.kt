package com.cs446.petpal.views

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ManageAccounts
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import android.util.Log
import androidx.compose.ui.platform.LocalContext
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import com.cs446.petpal.R
import com.cs446.petpal.models.Pet
import com.cs446.petpal.viewmodels.PetsPageViewModel
import java.io.File

fun getAllFilesInDirectory(directoryPath: String): List<String> {
    val directory = File(directoryPath)
    val filePaths = directory.listFiles()
        ?.filter { it.isFile && it.extension.lowercase() in listOf("jpg") }
        ?.map { "${directoryPath}/${it.name}" }
        ?: emptyList()
    println(filePaths)
    return filePaths
}

@Composable
fun PetsPageView(
    petsPageViewModel: PetsPageViewModel = hiltViewModel(),
    navController: NavController,
    petId: String?,
) {
    val myPets by petsPageViewModel.myPetsList.collectAsState()
    val sharedPets by petsPageViewModel.sharedPetsList.collectAsState()
    val selectedPet by petsPageViewModel.selectedPet.collectAsState()

    LaunchedEffect(Unit) {
        petsPageViewModel.fetchAllPetsFromServer()
    }
    LaunchedEffect(key1 = petId, key2 = myPets) {
        if (!myPets.isNullOrEmpty() && petId != null) {
            Log.d("PetsPageView", "Selecting pet with petId: $petId")
            petsPageViewModel.selectPet(petId)
        }
    }

    val petToShow = selectedPet ?: if (myPets.isNotEmpty()) myPets.firstOrNull() else sharedPets.firstOrNull()


    val scrollState = rememberScrollState()
    var showSharePetDialog by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            TopBar(navController = navController)

            Spacer(modifier = Modifier.height(16.dp))


            MyPetSelectionRow(
                pets = myPets,
                selectedPet = petToShow?.petId ?: "",
                onPetSelected = { petsPageViewModel.selectPet(it) },
            )

            Spacer(modifier = Modifier.height(6.dp))

            SharedPetSelectionRow(
                pets = sharedPets,
                selectedPet = petToShow?.petId ?: "",
                onPetSelected = { petsPageViewModel.selectPet(it) },
            )

            Spacer(modifier = Modifier.height(8.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.Start
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Image(
                        painter = painterResource(
                            id = if (petToShow != null) getPetProfilePic(petToShow.name.value)
                            else R.drawable.profile_pic_main
                        ),
                        contentDescription = "Pet Profile Picture",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clip(RoundedCornerShape(16.dp)),
                        contentScale = ContentScale.Crop
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                PetInfoCard(petToShow = petToShow, petsPageViewModel = petsPageViewModel)

                Spacer(modifier = Modifier.height(8.dp))

                InsuranceInfoCard(petToShow)

                Spacer(modifier = Modifier.height(8.dp))

                MedicationInfoCard(petToShow)

                Spacer(modifier = Modifier.height(16.dp))

                val addedImages = getAllFilesInDirectory("/data/user/0/com.cs446.petpal/files/${selectedPet?.petId}/")
                ImageGallery(addedImages)

                Spacer(modifier = Modifier.height(16.dp))
            }
            BottomBar(navController)
        }

        if (!petsPageViewModel.isSharedPetProfile()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(end = 6.dp, bottom = 100.dp),
                contentAlignment = Alignment.BottomEnd
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    FloatingActionButton(
                        onClick = { showSharePetDialog = true },
                        containerColor = Color(0xFF64B5F6),
                        modifier = Modifier.size(38.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ManageAccounts,
                            contentDescription = "Share Pet",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    FloatingActionButton(
                        onClick = {
                            petToShow?.petId?.let { petId ->
                                petsPageViewModel.deletePetForUser(petId) { success ->
                                }
                            }
                        },
                        containerColor = Color(0xFF64B5F6),
                        modifier = Modifier.size(35.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = "Delete Pet",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }

    if (showSharePetDialog && petToShow != null) {
        showSharePetDialog = sharePetsPopup(
            currPet = selectedPet,
            currPetId = petToShow.petId,
            petsViewModel = petsPageViewModel
        )
    }
}

@Composable
fun PetInfoCard(
    petToShow: Pet?,
    petsPageViewModel: PetsPageViewModel
) {
    var showEditDialog by remember { mutableStateOf(false) }

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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = petToShow?.name?.value ?: "No Pet Selected",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
                if (petToShow != null && !petsPageViewModel.isSharedPetProfile()) {
                    Text(
                        text = "Edit",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.clickable { showEditDialog = true }
                    )
                }
            }

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
                    PetInfoRow("Animal", petToShow?.animal ?: "--", "animal_icon")
                    PetInfoRow("Gender", petToShow?.gender?.value ?: "--", "gender_icon")
                    PetInfoRow("Breed", petToShow?.breed ?: "--", "breed_icon")
                }
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    PetInfoRow("Age", "${petToShow?.age?.value ?: "--"} years", "calendar_icon")
                    PetInfoRow("Birthday", petToShow?.birthday ?: "--", "birthday_icon")
                    PetInfoRow("Weight", "${petToShow?.weight?.value ?: "--"} lbs", "weight_icon")
                }
            }
        }
    }

    if (showEditDialog && petToShow != null) {
        showEditDialog = petsPopup(
            currPet = petToShow,
            currPetId = petToShow.petId,
            popupType = "EDIT",
            petsViewModel = petsPageViewModel
        )
    }
}

@Composable
fun MyPetSelectionRow(
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
        Column(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier.padding(horizontal = 4.dp)
        ) {
            Text(
                text = "My Pets",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )
            Row(
                horizontalArrangement = Arrangement.Start,
            ) {
                pets.forEach { pet ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                            .clickable { onPetSelected(pet.petId) }
                    ) {
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
            }
        }

        Spacer(modifier = Modifier.weight(0.9f))

        IconButton(
            onClick = { showAddDialog = true },
            modifier = Modifier
                .size(48.dp)
                .padding(top = 12.dp, end = 12.dp),
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = Color(0xFFA2D9FF)
            )
        ) {
            Icon(
                painter = painterResource(R.drawable.add),
                contentDescription = "Add Pet",
                modifier = Modifier.size(24.dp),
                tint = Color.Black
            )
        }
    }
    if (showAddDialog) {
        showAddDialog = petsPopup(
            currPet = null,
            currPetId = null,
            popupType = "ADD",
            petsViewModel = viewModel()
        )
    }
}

@Composable
fun SharedPetSelectionRow(
    pets: List<Pet>,
    selectedPet: String,
    onPetSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth().padding(start = 4.dp),
        horizontalArrangement = Arrangement.Start
    ) {
        Column(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier.padding(horizontal = 4.dp)
        ) {
            Text(
                text = "Shared Pets",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )
            Row(
                horizontalArrangement = Arrangement.Start,
            ) {
                pets.forEach { pet ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                            .clickable { onPetSelected(pet.petId) }
                    ) {
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
            }
        }

        Spacer(modifier = Modifier.weight(0.9f))
    }
}

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
                .size(30.dp)
                .padding(end = 8.dp),
            contentScale = ContentScale.Fit
        )
        Column {
            Text(text = label, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Text(text = value, fontSize = 14.sp, fontWeight = FontWeight.Normal)
        }
    }
}

@Composable
fun InsuranceInfoCard(
    pet: Pet?,
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Insurance",
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = getIconPlaceholder("insurance_icon")),
                            contentDescription = "Provider",
                            modifier = Modifier.size(30.dp),
                            contentScale = ContentScale.Fit
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            Text(text = "Provider", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            Text(text = pet?.insuranceProvider?.value ?: "--", fontSize = 12.sp)
                        }
                    }
                }
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = getIconPlaceholder("policy_icon")),
                            contentDescription = "Policy #",
                            modifier = Modifier.size(0.dp),
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

@Composable
fun MedicationInfoCard(
    pet: Pet?,
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Medication",
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = getIconPlaceholder("medication_icon")),
                            contentDescription = "Medication Name",
                            modifier = Modifier.size(30.dp),
                            contentScale = ContentScale.Fit
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            Text(text = "Name", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            Text(text = pet?.medicationName?.value ?: "--", fontSize = 12.sp)
                        }
                    }
                }
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

@Composable
fun ImageGallery(addedImages: List<String>) {
    var showAddPhotoDialog by remember { mutableStateOf(false) }
    var selectedAddedImage by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Photo Gallery",
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = "Add Image",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.clickable { showAddPhotoDialog = true }
                )
            }

            if (addedImages.size > 0) {
                LazyRow {
                    items(addedImages) { imageRes ->
                        val imageFile = File(imageRes)
                        val imageUri = Uri.fromFile(imageFile)
                        Image(
                            painter = rememberAsyncImagePainter(
                                ImageRequest.Builder(context)
                                    .data(imageUri)
                                    .build()
                            ),
                            contentDescription = "Uploaded image",
                            modifier = Modifier
                                .padding(8.dp)
                                .size(100.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { selectedAddedImage = imageRes },
                        )
                    }
                }
            } else {
                Text(
                    text = "Your gallery is empty!",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    }
    if (showAddPhotoDialog) {
        showAddPhotoDialog = imageUploadScreen()
    }

    if (selectedAddedImage != null) {
        AlertDialog(
            onDismissRequest = { selectedAddedImage = null },
            title = {
                Text("Enlarge Image")
            },
            text = {
                Box(
                    modifier = Modifier.clickable { selectedAddedImage = null },
                    contentAlignment = Alignment.Center
                ) {
                    val imageFile = selectedAddedImage?.let { File(it) }
                    val imageUri = Uri.fromFile(imageFile)
                    Image(
                        painter = rememberAsyncImagePainter(
                            ImageRequest.Builder(context)
                                .data(imageUri)
                                .build()
                        ),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    )
                }
            },
            dismissButton = {},
            confirmButton = {}
        )
    }
}

fun getIconPlaceholder(iconName: String): Int {
    return when (iconName) {
        "gender_icon" -> R.drawable.ic_gender
        "calendar_icon" -> R.drawable.ic_age
        "birthday_icon" -> R.drawable.ic_birthday
        "weight_icon" -> R.drawable.ic_weight
        "animal_icon" -> R.drawable.ic_animal
        "breed_icon" -> R.drawable.ic_breed
        "insurance_icon" -> R.drawable.ic_insurance
        "policy_icon" -> R.drawable.ic_policy
        "medication_icon" -> R.drawable.ic_medication
        else -> R.drawable.ic_default
    }
}

fun getPetProfilePic(petName: String): Int {
    return when (petName) {
        "Toby" -> R.drawable.pet_toby
        "Max" -> R.drawable.pet_max
        "Luna" -> R.drawable.pet_luna
        else -> R.drawable.profile_pic_default
    }
}