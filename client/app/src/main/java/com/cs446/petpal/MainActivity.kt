package com.cs446.petpal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.cs446.petpal.ui.theme.PetPalTheme
import com.cs446.petpal.views.SignUpView


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PetPalTheme {
                SignUpView()
            }
        }
    }
}
