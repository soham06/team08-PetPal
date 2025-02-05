package com.cs446.petpal.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.cs446.petpal.viewmodels.SignUpViewModel
import androidx.lifecycle.viewmodel.compose.viewModel


@Composable
fun SignUpView(
    modifier: Modifier = Modifier,
    viewModel: SignUpViewModel = viewModel()
) {

    var email: String by remember { mutableStateOf("") }
    var password: String by remember { mutableStateOf("") }

    val user by viewModel.user.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("email") },
            modifier = Modifier.fillMaxWidth()
        )

        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("password") },
            modifier = Modifier.fillMaxWidth()
        )


        if (user == null) {
            Button(
                onClick = { viewModel.createAccount(email, password) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Register")
            }

            Button(
                onClick = { viewModel.signIn(email, password) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Login")
            }
        } else {
            Text("User signed in: ${user?.email}")
            Button(
                onClick = { viewModel.signOut() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Sign Out")
            }
        }

        errorMessage?.let {
            Text(text = it, color = androidx.compose.ui.graphics.Color.Red)
        }
    }
}