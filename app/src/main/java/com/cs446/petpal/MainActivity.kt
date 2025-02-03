package com.cs446.petpal

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cs446.petpal.ui.theme.PetPalTheme
import com.cs446.petpal.firebase.emailauth.EmailAuth

class MainActivity : ComponentActivity() {
    private val emailAuth: EmailAuth by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PetPalTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    RegisterFormExample(
                        modifier = Modifier.padding(innerPadding),
                        emailAuth
                    )
                }
            }
        }
    }
}

@Composable
fun RegisterFormExample(modifier: Modifier = Modifier, emailAuth: EmailAuth) {
    var email: String by remember { mutableStateOf("") }
    var password: String by remember { mutableStateOf("") }

    val user by emailAuth.user.collectAsState()
    val errorMessage by emailAuth.errorMessage.collectAsState()

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
                onClick = { emailAuth.createAccount(email, password) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Register")
            }

            Button(
                onClick = { emailAuth.signIn(email, password) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Login")
            }
        } else {
            Text("User signed in: ${user?.email}")
            Button(
                onClick = { emailAuth.signOut() },
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