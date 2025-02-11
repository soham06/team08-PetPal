package com.cs446.petpal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.cs446.petpal.ui.theme.PetPalTheme
import com.cs446.petpal.views.LandingPageScreen
import com.cs446.petpal.views.NewSignUpView

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            PetPalTheme {
                NavHost(navController = navController, startDestination = "landing") {
                    composable("landing") { LandingPageScreen(navController) } // ✅ Pass `navController`
                    composable("signup") { NewSignUpView() }
                    composable("login") {}
                }
            }
        }
    }
}
