package com.cs446.petpal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.cs446.petpal.ui.theme.PetPalTheme
import com.cs446.petpal.views.LoginView
import com.cs446.petpal.views.LandingPageScreen
import com.cs446.petpal.views.SignUpView
import com.cs446.petpal.views.HomepageView
import com.cs446.petpal.views.PetsPageView
import com.cs446.petpal.views.TasksPage.TasksPageView
import com.cs446.petpal.views.ProfilePageView
import com.cs446.petpal.repository.UserRepository
import com.cs446.petpal.views.Marketplace.MarketplaceView
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var userRepository: UserRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            PetPalTheme {
                Surface(
                    color = Color.White,
                    modifier = Modifier.fillMaxSize()
                ) {
                    NavHost(navController = navController, startDestination = "landing") {
                        composable("landing") { LandingPageScreen(navController) }
                        composable("signup") { SignUpView(navController = navController) }
                        composable("login") { LoginView(navController = navController) }
                        composable("homepage") { HomepageView(navController = navController) }
                        composable("taskspage") { TasksPageView(navController = navController) }
                        composable("petspage") { PetsPageView(navController = navController, petId = null) }
                        composable("petspage/{petId}") { backStackEntry ->
                            val petId = backStackEntry.arguments?.getString("petId")
                            PetsPageView(navController = navController, petId = petId)}
                        composable("profilepage") { ProfilePageView(navController = navController) }
                        composable("marketplace") { MarketplaceView(navController = navController) }
                    }
                }
            }
        }
    }
}
