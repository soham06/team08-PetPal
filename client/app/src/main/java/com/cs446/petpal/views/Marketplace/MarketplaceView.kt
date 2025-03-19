package com.cs446.petpal.views.Marketplace

import androidx.compose.foundation.background
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.cs446.petpal.viewmodels.TasksViewModel
import com.cs446.petpal.viewmodels.EventsViewModel
import com.cs446.petpal.views.BottomBar
import com.cs446.petpal.views.TopBar
import androidx.hilt.navigation.compose.hiltViewModel
import com.cs446.petpal.viewmodels.MarketplaceViewModel

@Composable
fun MarketplaceView( marketplaceViewModel: MarketplaceViewModel = hiltViewModel(),
                  navController: NavController) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        TopBar(navController = navController)

        // Main Content
        Box(
            modifier = Modifier
                .weight(1f) // Takes remaining space
                .fillMaxSize()
                .background(Color.White),
            contentAlignment = Alignment.TopStart
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.Top
            ) {
                MarketPostView(marketplaceViewModel)
                Spacer(modifier = Modifier.height(20.dp))
               // DailyEventsView(eventsViewModel)
            }
        }
        BottomBar(navController)
    }
}