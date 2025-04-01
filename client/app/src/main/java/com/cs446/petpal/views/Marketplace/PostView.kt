package com.cs446.petpal.views.Marketplace

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cs446.petpal.R
import com.cs446.petpal.models.Post
import com.cs446.petpal.models.Pet
import androidx.compose.foundation.clickable
import androidx.compose.foundation.Image
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton

@Composable
fun PostCard(
    post: Post,
    pet: Pet?,
    modifier: Modifier = Modifier,
    editable: Boolean = false,
    onEdit: () -> Unit = {},
    onDelete: () -> Unit = {},
    onPetClick: () -> Unit = {}
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFA2D9FF)
        ),
        border = BorderStroke(1.dp, Color(0xCC1C1C1C))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = post.name.value,
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                )
                if (editable) {
                    Row {
                        IconButton(
                            onClick = { onEdit() },
                            colors = IconButtonDefaults.iconButtonColors(
                                containerColor = Color(0xFFFFD700)
                            )
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.edit),
                                contentDescription = "Edit Post",
                                tint = Color.Black
                            )
                        }
                        IconButton(
                            onClick = { onDelete() },
                            colors = IconButtonDefaults.iconButtonColors(
                                containerColor = Color(0xFFFFD700)
                            )
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_close),
                                contentDescription = "Resolve Post",
                                tint = Color.Black
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            // City
            Text(
                text = "City: ${post.city.value}",
                style = MaterialTheme.typography.bodyMedium.copy(color = Color.Black)
            )
            Spacer(modifier = Modifier.height(2.dp))
            // Phone
            Text(
                text = "Phone: ${post.phone.value}",
                style = MaterialTheme.typography.bodyMedium.copy(color = Color.Black)
            )
            Spacer(modifier = Modifier.height(2.dp))
            // Email
            Text(
                text = "Email: ${post.email.value}",
                style = MaterialTheme.typography.bodyMedium.copy(color = Color.Black)
            )
            Text(
                text = "Sitting Date: ${post.date.value}",
                style = MaterialTheme.typography.bodyMedium.copy(color = Color.Black)
            )
            Spacer(modifier = Modifier.height(8.dp))
            // Pet
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onPetClick() }
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
                    val petImageRes = R.drawable.pet_max
                    Image(
                        painter = painterResource(id = petImageRes),
                        contentDescription = "Pet Image",
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape),
                        contentScale = androidx.compose.ui.layout.ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Pet: ${pet?.name?.value ?: "Unknown"}",
                        fontSize = 16.sp,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold,
                        color = Color.Black
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            // Description label and content.
            Text(
                text = "Description:",
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = Color.Black,
                    fontWeight = FontWeight.Bold
                )
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = post.description.value,
                style = MaterialTheme.typography.bodyMedium.copy(color = Color.Black)
            )
        }
    }
}
