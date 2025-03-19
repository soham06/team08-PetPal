package com.cs446.petpal.views

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.rememberAsyncImagePainter

@Composable
fun ImageUploadPopup(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onImageSelected: (Uri) -> Unit
) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text(text = "Upload Image", fontWeight = FontWeight.Bold) },
            text = { Text("Upload images of your pet from your phone gallery!") },
            confirmButton = {
                Column {
                    val galleryLauncher =
                        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
                            uri?.let { onImageSelected(it) }
                            onDismiss()
                        }

                    Button(
                        onClick = { galleryLauncher.launch("image/*") },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Pick from Gallery")
                    }
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700))
                ) {
                    Text(
                        text = "Cancel",
                        color = Color.Black,
                    )
                }
            }
        )
    }
}

@Composable
fun imageUploadScreen(): Boolean {
    var showUploadDialog by remember { mutableStateOf(true) }
    var showImageDialog by remember { mutableStateOf(false) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    AlertDialog(
        onDismissRequest = { showUploadDialog = false },
        title = {
            Text("Upload New Image")
        },
        text = {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(onClick = { showImageDialog = true }, modifier = Modifier.fillMaxWidth()) {
                    Text("Upload Image")
                }

                selectedImageUri?.let { uri ->
                    Image(
                        painter = rememberAsyncImagePainter(uri),
                        contentDescription = "Uploaded Image",
                        modifier = Modifier
                            .size(150.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.Gray)
                            .clickable { showImageDialog = true }
                    )
                }
            }
        },
        dismissButton = {
            Button(
                onClick = {
                    showUploadDialog = false
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700))
            ) {
                Text(
                    text = "Cancel",
                    color = Color.Black,
                )
            }
        },
        confirmButton = {}
    )

    ImageUploadPopup(
        showDialog = showImageDialog,
        onDismiss = { showImageDialog = false },
        onImageSelected = { uri -> selectedImageUri = uri }
    )
    return showUploadDialog
}

