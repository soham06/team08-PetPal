package com.cs446.petpal.views.Marketplace

import androidx.compose.foundation.background
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
import com.cs446.petpal.viewmodels.MarketplaceViewModel

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

    // Fetch posts on launch.
    LaunchedEffect(key1 = marketplaceViewModel.currentUserId) {
        marketplaceViewModel.getPostsForUser()
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

        Spacer(modifier = Modifier.height(8.dp))

        // List of posts.
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp)
        ) {
            items(marketplaceViewModel.posts) { post ->
                // For PetOwners, enable editing; for PetSitters, disable it.
                PostCard(
                    post = post,
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
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showAddDialog = false
                            marketplaceViewModel.createPost(
                                name = nameInput,
                                city = cityInput,
                                phone = phoneInput,
                                email = emailInput,
                                description = descriptionInput
                            ) { success, errorMsg ->
                                // Optionally handle result (e.g., show a toast on error)
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
                title = { Text(text = "Delete Post") },
                text = { Text("Are you sure you want to delete this post?") },
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
                        Text("Delete", color = Color.Black)
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
                            label = { Text("Phone") },
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
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            // Call updatePost from the ViewModel with the updated values.
                            currentPostToEdit?.let { post ->
                                marketplaceViewModel.updatePost(
                                    postId = post.postId ?: "",
                                    name = editName,
                                    city = editCity,
                                    phone = editPhone,
                                    email = editEmail,
                                    description = editDescription
                                ) { success, errorMsg ->
                                    if (success) {
                                        // Hide the dialog upon successful update.
                                        showEditDialog = false
                                    } else {
                                        // Optionally handle the error (e.g., display a message).
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


    }
}




