package com.cs446.petpal.views.Marketplace

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cs446.petpal.models.Pet
import androidx.compose.material3.Icon
import androidx.compose.ui.res.painterResource
import com.cs446.petpal.R
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.ButtonDefaults

@Composable
fun PetInfoRow(
    label: String,
    info: String,
    iconResId: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = iconResId),
            contentDescription = "$label icon",
            modifier = Modifier.size(20.dp),
            tint = Color.Unspecified
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = "$label: $info",
            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp)
        )
    }
}

@Composable
fun PetInfoDialog(
    petToShow: Pet?,
    onDismissRequest: () -> Unit,
) {
    AlertDialog(
        modifier = Modifier.fillMaxWidth(),
        onDismissRequest = onDismissRequest,
        title = { Text(text = "Pet Details") },
        text = {
            Card(
                modifier = Modifier.fillMaxWidth(),
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
                                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                            )
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Replace with your actual icon resource IDs.
                            PetInfoRow("Animal", petToShow?.animal ?: "--", R.drawable.ic_animal)
                            PetInfoRow("Gender", petToShow?.gender?.value ?: "--", R.drawable.ic_gender)
                            PetInfoRow("Breed", petToShow?.breed ?: "--", R.drawable.ic_breed)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            PetInfoRow("Age", "${petToShow?.age?.value ?: "--"} years", R.drawable.ic_age)
                            PetInfoRow("Birthday", petToShow?.birthday ?: "--", R.drawable.ic_birthday)
                            PetInfoRow("Weight", "${petToShow?.weight?.value ?: "--"} lbs", R.drawable.ic_weight)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismissRequest,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700))
            ) {
                Text("Close", color = Color.Black)
            }
        }

    )
}
