package com.example.bridgespeak

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.bridgespeak.ui.theme.BridgeSpeakTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BridgeSpeakTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    val navController = rememberNavController()

                    // ✅ Main navigation structure
                    NavHost(
                        navController = navController,
                        startDestination = "login"
                    ) {
                        // Login Screen
                        composable("login") {
                            LoginScreen(
                                navController = navController
                            )
                        }

                        // Sign Up Screen
                        composable("signup") {
                            SignUpScreen(navController)
                        }

                        // ✅ Main app after login (contains bottom nav)
                        composable("main") {
                            MainScreen(navController)
                        }

                        // Optional — standalone screens (if accessed directly)
                        composable("image_translation") { ImageTranslationScreen() }
                        composable("home") { HomeScreen(navController) }
                        composable("history") { HistoryScreen(navController) }
                        composable("profile") { ProfileScreen(navController) }
                        composable("about") { AboutScreen(navController) }

                    }
                }
            }
        }
    }
}
