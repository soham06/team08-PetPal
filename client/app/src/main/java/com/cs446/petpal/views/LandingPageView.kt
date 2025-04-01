package com.cs446.petpal.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.cs446.petpal.R

val yellow = Color(0xFFFFD700)

@Composable
fun LandingPageScreen(navController: NavController) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.dog_image),
            contentDescription = "Background Dog Image",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 60.dp)
        ) {
            Text(
                text = "PetPal",
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(top = 60.dp)
            )

            Text(
                text = "Take care of pets with ease!",
                fontSize = 20.sp,
                color = Color.DarkGray,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 4.dp, bottom = 20.dp)
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .padding(bottom = 20.dp)
            ) {
                Button(
                    onClick = { navController.navigate("signup") },
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = yellow)
                ) {
                    Text(text = "Sign up", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                }

                Button(
                    onClick = { navController.navigate("login") },
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = yellow)
                ) {
                    Text(text = "Login", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                }
            }
        }
    }
}
