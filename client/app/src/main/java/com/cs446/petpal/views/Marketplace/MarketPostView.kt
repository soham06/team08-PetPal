package com.cs446.petpal.views.Marketplace

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cs446.petpal.R
import com.cs446.petpal.viewmodels.MarketplaceViewModel
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.foundation.lazy.items



@Composable
fun MarketPostView(marketplaceViewModel: MarketplaceViewModel = hiltViewModel()) {
    // State variable to control the visibility of the "Add Post" dialog
    var showAddDialog by remember { mutableStateOf(false) }

    // State Variables
    var nameInput by remember { mutableStateOf("") }
    var cityInput by remember { mutableStateOf("") }
    var phoneInput by remember { mutableStateOf("") }
    var emailInput by remember { mutableStateOf("") }
    var descriptionInput by remember { mutableStateOf("") }

    // Max Word Limit for Description
    val maxDescriptionWords = 50

    // MARKETPLACE TITLE
    Row (
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ){
        Text(
            text = "PetPal MarketPlace",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .padding(top = 12.dp)
                .padding(start = 8.dp)
        )
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
                contentDescription = "Add Post",
                modifier = Modifier.size(24.dp),
                tint = Color.Black
            )
        }
    }

    Spacer(modifier = Modifier.height(8.dp))

    // Posts
    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp)
    ) {
        items(marketplaceViewModel.posts) { post ->
            PostCard(post = post)
        }
    }

    // Add Post Dialog - displayed when showAddDialog == True
    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text(text = "Add New Post") },
            text = {
                // Column for the input fields with spacing between them.
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Input for Name
                    OutlinedTextField(
                        value = nameInput,
                        onValueChange = { nameInput = it },
                        label = { Text("Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    // Input for City
                    OutlinedTextField(
                        value = cityInput,
                        onValueChange = { cityInput = it },
                        label = { Text("City") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    // Input for Phone Number
                    OutlinedTextField(
                        value = phoneInput,
                        onValueChange = { phoneInput = it },
                        label = { Text("Phone Number") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    // Input for Email
                    OutlinedTextField(
                        value = emailInput,
                        onValueChange = { emailInput = it },
                        label = { Text("Email") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    // Input for Description with a word limit check.
                    OutlinedTextField(
                        value = descriptionInput,
                        onValueChange = {
                            // Split the input into words and only update if within limit.
                            val words = it.trim().split("\\s+".toRegex())
                            if (words.size <= maxDescriptionWords) {
                                descriptionInput = it
                            }
                        },
                        label = { Text("Description (max $maxDescriptionWords words)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    // Display the current word count.
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
                        // Call the viewmodel function to create a new post.
                        marketplaceViewModel.createPost(
                            name = nameInput,
                            city = cityInput,
                            phone = phoneInput,
                            email = emailInput,
                            description = descriptionInput
                        ) { success, errorMsg ->
                            // Handle the success or failure of the operation if needed.
                        }
                        // Clear inputs after adding the post.
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

}

