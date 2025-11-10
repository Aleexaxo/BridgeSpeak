package com.example.bridgespeak

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

@Composable
fun MainScreen(rootNavController: NavHostController) {
    // inner nav controller for the bottom tabs
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController = navController, currentRoute = currentRoute)
        }
    ) { innerPadding: PaddingValues ->
        // inner NavHost handles screens shown while bottom bar is visible
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("home") {
                // show Home using the inner navController so bottom bar remains visible
                HomeScreen(navController = navController)
            }
            composable("image_translation") {
                // if your ImageTranslationScreen needs a NavController, change signature and pass navController
                ImageTranslationScreen()
            }
            composable("history") {
                HistoryScreen(navController = navController)
            }
            composable("profile") {
                ProfileScreen(navController = navController)
            }
            composable("about") {
                AboutScreen(navController = navController)
            }
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController, currentRoute: String?) {
    NavigationBar(containerColor = Color.White, tonalElevation = 8.dp) {
        NavigationBarItem(
            icon = { Icon(painterResource(R.drawable.ic_image), contentDescription = "Home") },
            label = { Text("Home") },
            selected = (currentRoute == "home"),
            onClick = {
                if (currentRoute != "home") {
                    navController.navigate("home") {
                        launchSingleTop = true
                    }
                }
            }
        )

        NavigationBarItem(
            icon = { Icon(painterResource(R.drawable.ic_history), contentDescription = "History") },
            label = { Text("History") },
            selected = (currentRoute == "history"),
            onClick = {
                if (currentRoute != "history") {
                    navController.navigate("history") {
                        launchSingleTop = true
                    }
                }
            }
        )

        NavigationBarItem(
            icon = { Icon(painterResource(R.drawable.ic_profile), contentDescription = "Profile") },
            label = { Text("Profile") },
            selected = (currentRoute == "profile"),
            onClick = {
                if (currentRoute != "profile") {
                    navController.navigate("profile") {
                        launchSingleTop = true
                    }
                }
            }
        )
    }
}
