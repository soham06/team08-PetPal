package com.cs446.petpal.views.Marketplace

import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cs446.petpal.R
import com.cs446.petpal.models.Pet
import com.cs446.petpal.models.Post
import com.cs446.petpal.viewmodels.MarketplaceViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


@Composable
fun MarketPostView(marketplaceViewModel: MarketplaceViewModel = hiltViewModel()) {
    val firstName = marketplaceViewModel.firstName
    val lastName = marketplaceViewModel.lastName
    val email = marketplaceViewModel.email

    var showDeleteDialog by remember { mutableStateOf(false) }
    var currentPostToDelete by remember { mutableStateOf<Post?>(null) }
    var showEditDialog by remember { mutableStateOf(false) }
    var currentPostToEdit by remember { mutableStateOf<Post?>(null) }

    var editName by remember { mutableStateOf("") }
    var editCity by remember { mutableStateOf("") }
    var editPhone by remember { mutableStateOf("") }
    var editEmail by remember { mutableStateOf("") }
    var editDescription by remember { mutableStateOf("") }

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


    var selectedPet by remember { mutableStateOf<Pet?>(null) }
    var dropdownExpanded by remember { mutableStateOf(false) }
    val petList = marketplaceViewModel.pets
    var showPetInfoDialog by remember { mutableStateOf(false) }
    var petInfoToShow by remember { mutableStateOf<Pet?>(null) }
    var addPostFieldError by remember { mutableStateOf("") }
    var addPostPetError by remember { mutableStateOf("") }

    LaunchedEffect(key1 = marketplaceViewModel.currentUserId) {
        marketplaceViewModel.getPostsForUser()
        marketplaceViewModel.getPetsForUser()
    }

    val isPetSitter = marketplaceViewModel.isPetSitter

    var showAddDialog by remember { mutableStateOf(false) }
    var nameInput by remember { mutableStateOf(firstName + " " + lastName ) }
    var cityInput by remember { mutableStateOf("") }
    var phoneInput by remember { mutableStateOf("") }
    var emailInput by remember { mutableStateOf(email) }
    var descriptionInput by remember { mutableStateOf("") }
    val maxDescriptionWords = 50

    Column {
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

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp)
        ) {
            items(marketplaceViewModel.posts) { post ->
                val associatedPet = petList.find { it.petId == post.petId.value }
                PostCard(
                    post = post,
                    pet = associatedPet,
                    editable = !isPetSitter,
                    onEdit = {
                        currentPostToEdit = post
                        editName = post.name.value
                        editCity = post.city.value
                        editPhone = post.phone.value
                        editEmail = post.email.value
                        editDescription = post.description.value
                        showEditDialog = true
                    },
                    onDelete = {
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
                                val digits = input.filter { it.isDigit() }
                                val formatted = buildString {
                                    for (i in digits.indices) {
                                        append(digits[i])
                                        if (i == 2 || i == 5) append("-")
                                    }
                                }.take(12)
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
                                        painter = painterResource(id = R.drawable.ic_dropdown),
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
                            val areFieldsValid = nameInput.trim().isNotEmpty() &&
                                    cityInput.trim().isNotEmpty() &&
                                    phoneInput.trim().isNotEmpty() &&
                                    emailInput.trim().isNotEmpty() &&
                                    descriptionInput.trim().isNotEmpty() &&
                                    dateInput.trim().isNotEmpty()

                            if (!areFieldsValid) {
                                addPostFieldError = "Please fill out every field in order to complete post."
                            } else {
                                addPostFieldError = ""
                            }

                            if (selectedPet == null) {
                                addPostPetError = "Please add a pet to profile in order to create a pet posting."
                            } else {
                                addPostPetError = ""
                            }

                            if (!areFieldsValid || selectedPet == null) {
                                return@Button
                            }

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
                            }
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

        if (!isPetSitter && showDeleteDialog && currentPostToDelete != null) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text(text = "Resolve Post") },
                text = { Text("Are you sure you want to resolve this post?") },
                confirmButton = {
                    Button(
                        onClick = {
                            marketplaceViewModel.deletePost(currentPostToDelete!!.postId ?: "") { success ->
                                if (success) {
                                    showDeleteDialog = false
                                } else {
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

        if (!isPetSitter && showEditDialog && currentPostToEdit != null) {
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
                                        painter = painterResource(id = R.drawable.ic_dropdown),
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

                            if (editSelectedPet == null) {
                                editPostPetError = "Please add a pet to profile in order to create a pet posting."
                            } else {
                                editPostPetError = ""
                            }

                            if (!areFieldsValid || editSelectedPet == null) {
                                return@Button
                            }

                            currentPostToEdit?.let { post ->
                                marketplaceViewModel.updatePost(
                                    postId = post.postId ?: "",
                                    name = editName,
                                    city = editCity,
                                    phone = editPhone,
                                    email = editEmail,
                                    description = editDescription,
                                    date = editDate,
                                    petId = editSelectedPet!!.petId
                                ) { success, errorMsg ->
                                    if (success) {
                                        showEditDialog = false
                                    } else {
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

        if (showPetInfoDialog && petInfoToShow != null) {
            PetInfoDialog(
                petToShow = petInfoToShow,
                onDismissRequest = { showPetInfoDialog = false }
            )
        }

    }
}




