package com.cs446.petpal.views

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.toSize
import com.cs446.petpal.models.User
import com.cs446.petpal.viewmodels.NewSignUpViewModel

@Composable
fun NewSignUpView(signUpViewModel: NewSignUpViewModel = viewModel()) {
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var userType by remember { mutableStateOf("Pet Owner") }
    var signUpSuccess by remember { mutableStateOf<Boolean?>(null) }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFA2D9FF)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp) // Reduced padding
        ) {
            Text(
                text = "Welcome!",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            )
            Text(
                text = "Join the world’s largest community of pet owners",
                fontSize = 15.sp,
                color = Color.DarkGray,
                fontWeight = FontWeight.Bold
            )

            val textFieldModifier = Modifier
                .fillMaxWidth()
                .background(Color.White, RoundedCornerShape(20.dp))
                .padding(horizontal = 12.dp, vertical = 6.dp)

            listOf(
                "First Name" to firstName,
                "Last Name" to lastName,
                "Address" to address,
                "Email Address" to email
            ).forEach { (label, value) ->
                OutlinedTextField(
                    value = value,
                    onValueChange = { newValue ->
                        when (label) {
                            "First Name" -> firstName = newValue
                            "Last Name" -> lastName = newValue
                            "Address" -> address = newValue
                            "Email Address" -> email = newValue
                        }
                    },
                    label = { Text(label) },
                    modifier = textFieldModifier,
                    shape = RoundedCornerShape(20.dp)
                )
            }

            listOf(
                "Password" to password to passwordVisible,
                "Confirm Password" to confirmPassword to confirmPasswordVisible
            ).forEach { (pair, visibility) ->
                val (label, value) = pair
                OutlinedTextField(
                    value = value,
                    onValueChange = { newValue ->
                        when (label) {
                            "Password" -> password = newValue
                            "Confirm Password" -> confirmPassword = newValue
                        }
                    },
                    label = { Text(label) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    visualTransformation = if (visibility) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = {
                            if (label == "Password") passwordVisible = !passwordVisible
                            else confirmPasswordVisible = !confirmPasswordVisible
                        }) {
                            Icon(
                                imageVector = if (visibility) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                contentDescription = null
                            )
                        }
                    },
                    modifier = textFieldModifier,
                    shape = RoundedCornerShape(20.dp)
                )
            }

            // User Type Dropdown
            var expanded by remember { mutableStateOf(false) }
            val userTypes = listOf("Pet Owner", "Pet Sitter")
            var textFieldSize by remember { mutableStateOf(Size.Zero) }

            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = userType,
                    onValueChange = {},
                    label = { Text("User Type") },
                    readOnly = true,
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "Dropdown",
                            modifier = Modifier.clickable { expanded = !expanded }
                        )
                    },
                    modifier = textFieldModifier
                        .onGloballyPositioned { coordinates ->
                            textFieldSize = coordinates.size.toSize()
                        },
                    shape = RoundedCornerShape(50.dp)
                )

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier
                        .width(with(LocalDensity.current) { textFieldSize.width.toDp() })
                        .background(Color.White)
                ) {
                    userTypes.forEach { type ->
                        DropdownMenuItem(
                            text = { Text(type) },
                            onClick = {
                                userType = type
                                expanded = false
                            }
                        )
                    }
                }
            }

            // Sign Up Button
            Button(
                onClick = {
                    if (password == confirmPassword) {
                        val user = User(
                            mutableStateOf(firstName),
                            mutableStateOf(lastName),
                            mutableStateOf(address),
                            mutableStateOf(email),
                            mutableStateOf(password),
                            mutableStateOf(userType.removePrefix("Pet "))
                        )
                        signUpViewModel.registerUser(user) { success ->
                            signUpSuccess = success
                        }
                    } else {
                        signUpSuccess = false
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp), // Less rounded button
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.LightGray,  // Background color
                )
            ) {
                Text("Sign Up", color = Color.Black)
            }

            signUpSuccess?.let { success ->
                Text(
                    text = if (success) " Registration Successful " else " Registration Failed ",
                    color = if (success) Color.Green else Color.Red,
                    modifier = if (success) Modifier.background(Color.Black) else Modifier.background(Color.White),
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }

            Text(
                text = "Already have an account?",
                color = Color.DarkGray,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
            )

            Text(
                text = "Login here",
                color = Color.DarkGray,
                fontWeight = FontWeight.Bold,
                textDecoration = TextDecoration.Underline,
                fontSize = 15.sp,
                modifier = Modifier.clickable {
                    // Navigate to Login Screen
                }
            )
        }
    }
}
