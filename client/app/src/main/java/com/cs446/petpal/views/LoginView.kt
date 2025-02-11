package com.cs446.petpal.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.cs446.petpal.models.User
import com.cs446.petpal.viewmodels.LoginViewModel
import java.security.MessageDigest



@Composable
fun LoginView(loginViewModel: LoginViewModel = viewModel(), navController: NavController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var loginSuccess by remember { mutableStateOf<Boolean?>(null) }

    // Utility function to hash the password.
    fun hashPassword(password: String): String {
        val md = MessageDigest.getInstance("MD5")
        val digest = md.digest(password.toByteArray())
        return digest.joinToString("") { "%02x".format(it) }
    }


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
                .padding(24.dp)
        ) {
            // Welcome Back Text
            Text(
                text = "Welcome Back!",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Bold
                )
            )

            val textFieldModifier = Modifier
                .fillMaxWidth()
                .background(Color.White, RoundedCornerShape(15.dp))
                .padding(top = 4.dp, bottom = 10.dp, start = 10.dp, end = 10.dp)

            // EMAIL ADDRESS FIELD
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email Address") },
                modifier = textFieldModifier,
                shape = RoundedCornerShape(15.dp)
            )

            // PASSWORD
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                            contentDescription = null
                        )
                    }
                },
                modifier = textFieldModifier,
                shape = RoundedCornerShape(15.dp)
            )

            // Forgot Password
            Row (
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = "Forgot password?",
                    color = Color.DarkGray,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    modifier = Modifier.clickable {
                        // TODO: Implement forgot password navigation.
                    }
                )
            }

            // Login Button
            Button(
                onClick = {
                     loginViewModel.loginUser(email, hashPassword(password)) { success, _ ->
                         loginSuccess = success
//                         if(success) {
//                             navController.navigate("homepage") {
//                                 popUpTo("login") { inclusive = true }
//                             }
//                         }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700))
            ) {
                Text("Login", color = Color.Black)
            }


            // Show a login result message.
            loginSuccess?.let { success ->
                if (!success) {
                    Text(
                        text = "Login Failed",
                        color = Color.Red,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }

            LaunchedEffect(loginSuccess) {
                if (loginSuccess == true) {
                    navController.navigate("homepage")
                }
            }

            // Create an Account
            Text(
                text = "Create an account",
                color = Color.DarkGray,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                modifier = Modifier.clickable {
                    navController.navigate("signup")
                }
            )
        }
    }
}