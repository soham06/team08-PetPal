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
import androidx.compose.ui.text.font.FontStyle
import com.cs446.petpal.models.User
import com.cs446.petpal.viewmodels.SignUpViewModel
import java.security.MessageDigest

@Composable
fun SignUpView(signUpViewModel: SignUpViewModel = viewModel()) {
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

    fun hashPassword(password: String): String {
        val md = MessageDigest.getInstance("MD5")
        val digest = md.digest(password.toByteArray())
        return digest.joinToString("") { "%02x".format(it) }
    }

    Box(
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
            Text(
                text = "Welcome!",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )
            )
            Text(
                text = "Join the world’s largest community of pet owners",
                fontSize = 15.sp,
                color = Color.DarkGray,
                fontWeight = FontWeight.SemiBold
            )

            val textFieldModifier = Modifier
                .fillMaxWidth()
                .background(Color.White, RoundedCornerShape(15.dp))
                .padding(top = 4.dp, bottom = 10.dp, start = 10.dp, end = 10.dp)
//                .padding(6.dp)
//                .shadow(4.dp, RoundedCornerShape(15.dp))

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
//                    colors = TextFieldDefaults.outlinedTextFieldColors(
//                        containerColor = Color.White,
//                        focusedBorderColor = Color(0xFF42A5F5),
//                        unfocusedBorderColor = Color.LightGray
//                    ),
                    modifier = textFieldModifier,
                    shape = RoundedCornerShape(15.dp)
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
                    shape = RoundedCornerShape(15.dp)
                )
            }

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
                    shape = RoundedCornerShape(15.dp)
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

            Button(
                onClick = {
                    if (password == confirmPassword) {
                        val hashedPassword = hashPassword(password)
                        val user = User(
                            mutableStateOf(firstName),
                            mutableStateOf(lastName),
                            mutableStateOf(address),
                            mutableStateOf(email),
                            mutableStateOf(hashedPassword),
                            mutableStateOf(userType)
                        )
                        signUpViewModel.registerUser(user) { success ->
                            signUpSuccess = success
                        }
                    } else {
                        signUpSuccess = false
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700))
            ) {
                Text("Sign Up", color = Color.Black)
            }

            signUpSuccess?.let { success ->
                Text(
                    text = if (success) "Registration Successful" else "Registration Failed",
                    color = if (success) Color.Green else Color.Red,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }
            Row {
                Text(
                    text = "Already have an account? ",
                    color = Color.DarkGray,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp
                )

                Text(
                    text = "Login here",
                    color = Color.Blue,
                    fontWeight = FontWeight.Bold,
                    fontStyle = FontStyle.Italic,
                    textDecoration = TextDecoration.Underline,
                    fontSize = 15.sp,
                    modifier = Modifier.clickable { }
                )
            }
        }
    }
}
