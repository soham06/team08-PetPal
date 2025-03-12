package com.cs446.petpal.views.Marketplace

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cs446.petpal.models.Post

@Composable
fun PostCard(post: Post, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xCC1C1C1C) // Dark grey with transparency.
        ),
        border = BorderStroke(1.dp, Color(0x66FFFFFF)), // Subtle white border.
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Post Name as the title
            Text(
                text = post.name.value,
                style = MaterialTheme.typography.titleMedium.copy(
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            )
            Spacer(modifier = Modifier.height(4.dp))
            // City information
            Text(
                text = "City: ${post.city.value}",
                style = MaterialTheme.typography.bodyMedium.copy(color = Color.White)
            )
            Spacer(modifier = Modifier.height(2.dp))
            // Phone information
            Text(
                text = "Phone: ${post.phone.value}",
                style = MaterialTheme.typography.bodyMedium.copy(color = Color.White)
            )
            Spacer(modifier = Modifier.height(2.dp))
            // Email information
            Text(
                text = "Email: ${post.email.value}",
                style = MaterialTheme.typography.bodyMedium.copy(color = Color.White)
            )
            Spacer(modifier = Modifier.height(8.dp))
            // Description label and content
            Text(
                text = "Description:",
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = post.description.value,
                style = MaterialTheme.typography.bodyMedium.copy(color = Color.White)
            )
        }
    }
}