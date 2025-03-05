package com.cs446.petpal.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.cs446.petpal.R
import com.cs446.petpal.viewmodels.ProfilePageViewModel


@Composable
fun ProfilePageView(profilePageViewModel: ProfilePageViewModel = hiltViewModel(), navController: NavController) {
    // User Values
    val firstName = profilePageViewModel.firstName
    val lastName = profilePageViewModel.lastName
    val address = profilePageViewModel.address
    val email = profilePageViewModel.email
    val userType = profilePageViewModel.userType

    Box(
        // Blue Background
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFA2D9FF)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 24.dp, end = 24.dp, top = 0.dp, bottom = 24.dp)
        ){
            // Close Screen
            Row (
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = {
                        navController.navigate("homepage")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    shape = CircleShape,
                    modifier = Modifier.size(50.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_close),
                        contentDescription = "Close Profile",
                        tint = Color.Black,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            // Profile Picture
            Image(
                painter = painterResource(id = R.drawable.ic_profile),
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .size(150.dp)
                    .clip(CircleShape)
                    .border(2.dp,Color.White, CircleShape),
                contentScale = ContentScale.Crop
            )
            // Edit Profile Text
            Text(
                text = "Profile",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Bold
                )
            )

            // User Type Text
            Text(
                text = userType,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                )
            )

            val textFieldModifier = Modifier
                .fillMaxWidth()
                .background(Color.White, RoundedCornerShape(15.dp))
                .padding(top = 4.dp, bottom = 10.dp, start = 10.dp, end = 10.dp)

            // FIRST NAME FIELD
            OutlinedTextField(
                value = firstName,
                onValueChange = {  },
                label = { Text("First Name") },
                modifier = textFieldModifier,
                shape = RoundedCornerShape(15.dp),
                readOnly = true
            )
            // LAST NAME FIELD
            OutlinedTextField(
                value = lastName,
                onValueChange = {  },
                label = { Text("Last Name") },
                modifier = textFieldModifier,
                shape = RoundedCornerShape(15.dp),
                readOnly = true
            )
            // ADDRESS FIELD
            OutlinedTextField(
                value = address,
                onValueChange = {  },
                label = { Text("Address") },
                modifier = textFieldModifier,
                shape = RoundedCornerShape(15.dp),
                readOnly = true
            )
            // EMAIL FIELD
            OutlinedTextField(
                value = email,
                onValueChange = {  },
                label = { Text("Email") },
                modifier = textFieldModifier,
                shape = RoundedCornerShape(15.dp),
                readOnly = true
            )

            // LOGOUT
            Button(
                onClick = {
                    navController.navigate("landing")
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700))
            ) {
                Text("Logout", color = Color.Black)
            }

        }
    }


}