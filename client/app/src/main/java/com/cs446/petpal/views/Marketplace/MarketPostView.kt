package com.cs446.petpal.views.Marketplace

import androidx.compose.foundation.background
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cs446.petpal.R
import com.cs446.petpal.models.Post
import com.cs446.petpal.models.Pet
import com.cs446.petpal.viewmodels.MarketplaceViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.foundation.clickable
import androidx.compose.material3.DropdownMenuItem


@Composable
fun MarketPostView(marketplaceViewModel: MarketplaceViewModel = hiltViewModel()) {
    // User Values
    val firstName = marketplaceViewModel.firstName
    val lastName = marketplaceViewModel.lastName
    val email = marketplaceViewModel.email

    var showDeleteDialog by remember { mutableStateOf(false) }
    var currentPostToDelete by remember { mutableStateOf<Post?>(null) }
    var showEditDialog by remember { mutableStateOf(false) }
    var currentPostToEdit by remember { mutableStateOf<Post?>(null) }

    // State variables for the edit dialog's input fields:
    var editName by remember { mutableStateOf("") }
    var editCity by remember { mutableStateOf("") }
    var editPhone by remember { mutableStateOf("") }
    var editEmail by remember { mutableStateOf("") }
    var editDescription by remember { mutableStateOf("") }

    // Date
    val dateFormat = SimpleDateFormat("MM-dd-yyyy", Locale.getDefault())
    var dateInput by remember { mutableStateOf(dateFormat.format(Calendar.getInstance().time)) }
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    val datePickerDialog = remember {
        DatePickerDialog(
            context,
            { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
                dateInput = dateFormat.format(
                    Calendar.getInstance().apply {
                        set(year, month, dayOfMonth)
                    }.time
                )
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
    }

    // Pets
    // Holds the currently selected pet (null until one is chosen)
    var selectedPet by remember { mutableStateOf<Pet?>(null) }
    // Controls whether the dropdown menu is expanded
    var dropdownExpanded by remember { mutableStateOf(false) }
    val petList = marketplaceViewModel.pets
    var showPetInfoDialog by remember { mutableStateOf(false) }
    var petInfoToShow by remember { mutableStateOf<Pet?>(null) }
    var addPostFieldError by remember { mutableStateOf("") }
    var addPostPetError by remember { mutableStateOf("") }


    // Fetch posts on launch.
    LaunchedEffect(key1 = marketplaceViewModel.currentUserId) {
        marketplaceViewModel.getPostsForUser()
        marketplaceViewModel.getPetsForUser()
    }

    // Check if the user is a PetSitter.
    val isPetSitter = marketplaceViewModel.isPetSitter

    // UI state for the "Add Post" dialog (only for PetOwners).
    var showAddDialog by remember { mutableStateOf(false) }
    var nameInput by remember { mutableStateOf(firstName + " " + lastName ) }
    var cityInput by remember { mutableStateOf("") }
    var phoneInput by remember { mutableStateOf("") }
    var emailInput by remember { mutableStateOf(email) }
    var descriptionInput by remember { mutableStateOf("") }
    val maxDescriptionWords = 50

    Column {
        // Top Row: Title and conditionally show Add button.
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "PetPal MarketPlace",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier
                    .padding(top = 12.dp, start = 8.dp)
            )
            if (!isPetSitter) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    IconButton(
                        onClick = {
                            marketplaceViewModel.undoDeletePost()
                            // Clear inputs.
                            nameInput = ""
                            cityInput = ""
                            phoneInput = ""
                            emailInput = ""
                            descriptionInput = ""
                        },
                        modifier = Modifier
                            .size(48.dp)
                            .padding(top = 12.dp, end = 12.dp),
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = Color(0xFFA2D9FF)
                        )
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.undo),
                            contentDescription = "Add Post",
                            modifier = Modifier.size(24.dp),
                            tint = Color.Black
                        )
                    }
                    IconButton(
                        onClick = {
                            marketplaceViewModel.redoDeletePost()
                            // Clear inputs.
                            nameInput = ""
                            cityInput = ""
                            phoneInput = ""
                            emailInput = ""
                            descriptionInput = ""
                        },
                        modifier = Modifier
                            .size(48.dp)
                            .padding(top = 12.dp, end = 12.dp),
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = Color(0xFFA2D9FF)
                        )
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.redo),
                            contentDescription = "Add Post",
                            modifier = Modifier.size(24.dp),
                            tint = Color.Black
                        )
                    }
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
                            painter = painterResource(id = R.drawable.add),
                            contentDescription = "Add Post",
                            modifier = Modifier.size(24.dp),
                            tint = Color.Black
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // List of posts.
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp)
        ) {
            items(marketplaceViewModel.posts) { post ->
                // For PetOwners, enable editing; for PetSitters, disable it.
                val associatedPet = petList.find { it.petId == post.petId.value }
                PostCard(
                    post = post,
                    pet = associatedPet,
                    editable = !isPetSitter,
                    onEdit = {
                        // Set the current post to edit and pre-populate edit fields.
                        currentPostToEdit = post
                        editName = post.name.value
                        editCity = post.city.value
                        editPhone = post.phone.value
                        editEmail = post.email.value
                        editDescription = post.description.value
                        showEditDialog = true
                    },
                    onDelete = {
                        // Set the post to delete and open the confirmation dialog.
                        currentPostToDelete = post
                        showDeleteDialog = true
                    },
                    onPetClick = {
                        if (associatedPet != null) {
                            petInfoToShow = associatedPet
                            showPetInfoDialog = true
                        }
                    }
                )
            }
        }

        // Add Post dialog for PetOwners only.
        if (!isPetSitter && showAddDialog) {
            AlertDialog(
                onDismissRequest = { showAddDialog = false },
                title = { Text(text = "Add New Post") },
                text = {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = nameInput,
                            onValueChange = { nameInput = it },
                            label = { Text("Name") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = cityInput,
                            onValueChange = { cityInput = it },
                            label = { Text("City") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = phoneInput,
                            onValueChange = { input ->
                                // Remove all non-digit characters
                                val digits = input.filter { it.isDigit() }
                                // Format input as xxx-xxx-xxxx
                                val formatted = buildString {
                                    for (i in digits.indices) {
                                        append(digits[i])
                                        if (i == 2 || i == 5) append("-") // Insert dashes at appropriate positions
                                    }
                                }.take(12) // Ensure it doesn't exceed "123-456-7890"
                                phoneInput = formatted
                            },
                            label = { Text("Phone Number") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = emailInput,
                            onValueChange = { emailInput = it },
                            label = { Text("Email") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        // DATE
                        OutlinedTextField(
                            value = dateInput,
                            onValueChange = {},
                            label = { Text("Sitting Date") },
                            readOnly = true,
                            trailingIcon = {
                                Icon(
                                    imageVector = Icons.Default.CalendarToday,
                                    contentDescription = "Calendar Icon",
                                    modifier = Modifier.clickable { datePickerDialog.show() }
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { datePickerDialog.show() }
                        )

                        // PET DROPDOWN
                        Box(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedTextField(
                                    value = selectedPet?.name?.value ?: "Select a pet",
                                    onValueChange = { },
                                    label = { Text("Select Pet") },
                                    modifier = Modifier.weight(1f),
                                    readOnly = true,
                                )
                                IconButton(
                                    onClick = { dropdownExpanded = true }
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_dropdown), // use your dropdown icon resource
                                        contentDescription = "Open Dropdown"
                                    )
                                }
                            }
                            DropdownMenu(
                                expanded = dropdownExpanded,
                                onDismissRequest = { dropdownExpanded = false },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                petList.forEach { pet ->
                                    DropdownMenuItem(
                                        onClick = {
                                            selectedPet = pet
                                            dropdownExpanded = false
                                        },
                                        text = { Text(text = pet.name.value) }
                                    )
                                }
                            }
                        }
                        OutlinedTextField(
                            value = descriptionInput,
                            onValueChange = {
                                val words = it.trim().split("\\s+".toRegex())
                                if (words.size <= maxDescriptionWords) {
                                    descriptionInput = it
                                }
                            },
                            label = { Text("Description (max $maxDescriptionWords words)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Text(
                            text = "Word count: ${if (descriptionInput.isBlank()) 0 else descriptionInput.trim().split("\\s+".toRegex()).size}/$maxDescriptionWords",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                        if (addPostFieldError.isNotBlank()) {
                            Text(
                                text = addPostFieldError,
                                color = Color.Red,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                        if (addPostPetError.isNotBlank()) {
                            Text(
                                text = addPostPetError,
                                color = Color.Red,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            // Validate that every text field is filled.
                            val areFieldsValid = nameInput.trim().isNotEmpty() &&
                                    cityInput.trim().isNotEmpty() &&
                                    phoneInput.trim().isNotEmpty() &&
                                    emailInput.trim().isNotEmpty() &&
                                    descriptionInput.trim().isNotEmpty() &&
                                    dateInput.trim().isNotEmpty()

                            // Set error for missing fields if needed.
                            if (!areFieldsValid) {
                                addPostFieldError = "Please fill out every field in order to complete post."
                            } else {
                                addPostFieldError = ""
                            }

                            // Validate that a pet is selected.
                            if (selectedPet == null) {
                                addPostPetError = "Please add a pet to profile in order to create a pet posting."
                            } else {
                                addPostPetError = ""
                            }

                            // If any error exists, exit early.
                            if (!areFieldsValid || selectedPet == null) {
                                return@Button
                            }

                            // If all validations pass, clear error messages and create the post.
                            showAddDialog = false
                            marketplaceViewModel.createPost(
                                name = nameInput,
                                city = cityInput,
                                phone = phoneInput,
                                email = emailInput,
                                description = descriptionInput,
                                date = dateInput,
                                petId = selectedPet!!.petId
                            ) { success, errorMsg ->
                                // Optionally handle result.
                            }
                            // Clear inputs.
                            nameInput = ""
                            cityInput = ""
                            phoneInput = ""
                            emailInput = ""
                            descriptionInput = ""
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700))
                    ) {
                        Text(text = "Add Post", color = Color.Black)
                    }
                },
                dismissButton = {
                    Button(
                        onClick = { showAddDialog = false },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700))
                    ) {
                        Text(text = "Cancel", color = Color.Black)
                    }
                }
            )
        }

        // Delete Post Dialog for PetOwners only.
        if (!isPetSitter && showDeleteDialog && currentPostToDelete != null) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text(text = "Resolve Post") },
                text = { Text("Are you sure you want to resolve this post?") },
                confirmButton = {
                    Button(
                        onClick = {
                            // Call the ViewModel's deletePost method.
                            marketplaceViewModel.deletePost(currentPostToDelete!!.postId ?: "") { success ->
                                if (success) {
                                    // Hide the dialog upon successful deletion.
                                    showDeleteDialog = false
                                } else {
                                    // Optionally, show an error message or handle the failure case.
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFA2D9FF))
                    ) {
                        Text("Resolve", color = Color.Black)
                    }
                },
                dismissButton = {
                    Button(
                        onClick = { showDeleteDialog = false },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFA2D9FF))
                    ) {
                        Text("Cancel", color = Color.Black)
                    }
                }
            )
        }

        // Edit Post Dialog for PetOwners only.
        if (!isPetSitter && showEditDialog && currentPostToEdit != null) {

            // Inside the edit dialog block (after verifying currentPostToEdit is not null)
            val associatedPetForEdit = petList.find { it.petId == currentPostToEdit!!.petId.value }
            var editSelectedPet by remember { mutableStateOf<Pet?>(associatedPetForEdit) }
            var editPostFieldError by remember { mutableStateOf("") }
            var editPostPetError by remember { mutableStateOf("") }


            var editDate by remember { mutableStateOf(currentPostToEdit?.date?.value ?: dateFormat.format(Calendar.getInstance().time)) }

            val editDatePickerDialog = remember {
                DatePickerDialog(
                    context,
                    { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
                        editDate = dateFormat.format(
                            Calendar.getInstance().apply {
                                set(year, month, dayOfMonth)
                            }.time
                        )
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
                )
            }


            AlertDialog(
                onDismissRequest = { showEditDialog = false },
                title = { Text(text = "Edit Post") },
                text = {
                    // Display the input fields in a column.
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = editName,
                            onValueChange = { editName = it },
                            label = { Text("Name") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = editCity,
                            onValueChange = { editCity = it },
                            label = { Text("City") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = editPhone,
                            onValueChange = { input ->
                                val digits = input.filter { it.isDigit() }
                                val formatted = buildString {
                                    for (i in digits.indices) {
                                        append(digits[i])
                                        if (i == 2 || i == 5) append("-")
                                    }
                                }.take(12)
                                editPhone = formatted
                            },
                            label = { Text("Phone Number") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = editEmail,
                            onValueChange = { editEmail = it },
                            label = { Text("Email") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = editDate,
                            onValueChange = {},
                            label = { Text("Sitting Date") },
                            readOnly = true,
                            trailingIcon = {
                                Icon(
                                    imageVector = Icons.Default.CalendarToday,
                                    contentDescription = "Calendar Icon",
                                    modifier = Modifier.clickable { editDatePickerDialog.show() }
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { editDatePickerDialog.show() }
                        )
                        // PET DROPDOWN for Edit Dialog
                        Box(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedTextField(
                                    value = editSelectedPet?.name?.value ?: "Select a pet",
                                    onValueChange = { },
                                    label = { Text("Select Pet") },
                                    modifier = Modifier.weight(1f),
                                    readOnly = true,
                                )
                                IconButton(
                                    onClick = { dropdownExpanded = true }
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_dropdown), // your dropdown icon
                                        contentDescription = "Open Dropdown"
                                    )
                                }
                            }
                            DropdownMenu(
                                expanded = dropdownExpanded,
                                onDismissRequest = { dropdownExpanded = false },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                petList.forEach { pet ->
                                    DropdownMenuItem(
                                        onClick = {
                                            editSelectedPet = pet
                                            dropdownExpanded = false
                                        },
                                        text = { Text(text = pet.name.value) }
                                    )
                                }
                            }
                        }
                        OutlinedTextField(
                            value = editDescription,
                            onValueChange = { input ->
                                val words = input.trim().split("\\s+".toRegex())
                                if (words.size <= maxDescriptionWords) {
                                    editDescription = input
                                }
                            },
                            label = { Text("Description (max $maxDescriptionWords words)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Text(
                            text = "Word count: ${if (editDescription.isBlank()) 0 else editDescription.trim().split("\\s+".toRegex()).size}/$maxDescriptionWords",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                        if (editPostFieldError.isNotBlank()) {
                            Text(
                                text = editPostFieldError,
                                color = Color.Red,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                        if (editPostPetError.isNotBlank()) {
                            Text(
                                text = editPostPetError,
                                color = Color.Red,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            // Validate text fields.
                            val areFieldsValid = editName.trim().isNotEmpty() &&
                                    editCity.trim().isNotEmpty() &&
                                    editPhone.trim().isNotEmpty() &&
                                    editEmail.trim().isNotEmpty() &&
                                    editDescription.trim().isNotEmpty() &&
                                    editDate.trim().isNotEmpty()
                            if (!areFieldsValid) {
                                editPostFieldError = "Please fill out every field in order to complete post."
                            } else {
                                editPostFieldError = ""
                            }

                            // Validate that a pet is selected.
                            if (editSelectedPet == null) {
                                editPostPetError = "Please add a pet to profile in order to create a pet posting."
                            } else {
                                editPostPetError = ""
                            }

                            // If either error exists, do not proceed.
                            if (!areFieldsValid || editSelectedPet == null) {
                                return@Button
                            }

                            // All validations passed; proceed to update the post.
                            currentPostToEdit?.let { post ->
                                marketplaceViewModel.updatePost(
                                    postId = post.postId ?: "",
                                    name = editName,
                                    city = editCity,
                                    phone = editPhone,
                                    email = editEmail,
                                    description = editDescription,
                                    date = editDate,
                                    petId = editSelectedPet!!.petId  // Using the updated pet from the dropdown
                                ) { success, errorMsg ->
                                    if (success) {
                                        showEditDialog = false
                                    } else {
                                        // Optionally handle the error.
                                    }
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFA2D9FF))
                    ) {
                        Text("Save", color = Color.Black)
                    }
                },
                dismissButton = {
                    Button(
                        onClick = { showEditDialog = false },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFA2D9FF))
                    ) {
                        Text("Cancel", color = Color.Black)
                    }
                }
            )
        }

        // Pet Info Dialog
        if (showPetInfoDialog && petInfoToShow != null) {
            PetInfoDialog(
                petToShow = petInfoToShow,
                onDismissRequest = { showPetInfoDialog = false }
            )
        }

    }
}




