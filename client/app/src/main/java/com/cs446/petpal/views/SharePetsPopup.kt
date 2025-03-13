package com.cs446.petpal.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cs446.petpal.R
import com.cs446.petpal.models.Pet
import com.cs446.petpal.viewmodels.PetsPageViewModel
import kotlinx.coroutines.delay

@Composable
fun sharePetsPopup(currPet: Pet?, currPetId: String?, petsViewModel: PetsPageViewModel = hiltViewModel()): Boolean {
    var showDialog by remember { mutableStateOf(true) }
    var userInputEmail by remember { mutableStateOf("") }
    var sharePetSuccess by remember { mutableStateOf<Boolean?>(null) }
    var unsharePetSuccess by remember { mutableStateOf<Boolean?>(null) }
    val userEmails = remember { mutableStateOf(mapOf<String, String>()) }
    var showShareMessage by remember { mutableStateOf(true) }
    var showUnshareMessage by remember { mutableStateOf(true) }

    AlertDialog(
        onDismissRequest = { showDialog = false },
        title = {
            Text("Manage Pet Sharing")
        },
        text = {
            Column {
                Text(
                    text = "Share Pet Profile With a User",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                )
                OutlinedTextField(
                    value = userInputEmail,
                    onValueChange = { userInputEmail = it },
                    label = { Text("Email Address") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = {
                            if (currPetId != null) {
                                petsViewModel.sharePetToUser(
                                    petId = currPetId,
                                    emailAddress = userInputEmail,
                                ) { success, _ ->
                                    sharePetSuccess = success
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700)),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Share Profile",
                            color = Color.Black,
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))

                LaunchedEffect(sharePetSuccess) {
                    if (sharePetSuccess != null) {
                        delay(2000)
                        showShareMessage = false
                    }
                }

                if (showShareMessage) {
                    sharePetSuccess?.let { success ->
                        if (success) {
                            Text(
                                text = "Successfully Shared Pet Profile",
                                color = Color.Blue,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        } else {
                            Text(
                                text = "Failed to Share Pet Profile",
                                color = Color.Red,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Column(
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "Manage Sharing for ${currPet?.name?.value}",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Column {
                        if (currPet?.sharedUsers?.value?.isNotEmpty() == true) {
                            currPet.sharedUsers.value.forEach { userId ->
                                val email = userEmails.value[userId] ?: ""
                                if (email.isEmpty()) {
                                    petsViewModel.getUserEmailAddress(userId) { success, fetchedEmail ->
                                        if (success) {
                                            userEmails.value += (userId to fetchedEmail)
                                        }
                                    }
                                }

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = email,
                                        color = Color.Black,
                                        fontSize = 14.sp,
                                        modifier = Modifier.weight(1f)
                                    )
                                    IconButton(
                                        onClick = {
                                            if (currPetId != null) {
                                                petsViewModel.unsharePetToUser(
                                                    petId = currPetId,
                                                    emailAddress = email,
                                                ) { success ->
                                                    unsharePetSuccess = success
                                                }
                                            }
                                        },
                                    ) {
                                        Icon(
                                            painter = painterResource(R.drawable.delete),
                                            contentDescription = "Unshare Profile",
                                            modifier = Modifier.size(20.dp),
                                        )
                                    }
                                }
                            }
                        } else {
                            Text(
                                text = "Profile hasn't been shared with any users yet",
                                color = Color.Black,
                                fontSize = 14.sp
                            )
                        }
                    }
                }

                LaunchedEffect(unsharePetSuccess) {
                    if (unsharePetSuccess != null) {
                        delay(2000)
                        showUnshareMessage = false
                    }
                }
                if (showUnshareMessage) {
                    unsharePetSuccess?.let { success ->
                        if (success) {
                            Text(
                                text = "Successfully Unshared Pet Profile",
                                color = Color.Blue,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        } else {
                            Text(
                                text = "Failed to Unshare Pet Profile",
                                color = Color.Red,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        },
        dismissButton = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = {
                        showDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700)),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Cancel",
                        color = Color.Black,
                    )
                }
            }
        },
        confirmButton = {}
    )
    return showDialog
}
