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

@Composable
fun PostCard(
    post: Post,
    modifier: Modifier = Modifier,
    editable: Boolean = false, // Controls visibility of edit/delete buttons.
    onEdit: () -> Unit = {},
    onDelete: () -> Unit = {}
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFA2D9FF)// Color(0xCC1C1C1C)
        ),
        border = BorderStroke(1.dp, Color(0xCC1C1C1C))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Top Row: Title with optional action buttons.
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
