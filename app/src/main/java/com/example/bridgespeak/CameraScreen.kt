package com.example.bridgespeak

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier              // <<-- IMPORTANT
import androidx.compose.ui.unit.dp            // for dp
import androidx.navigation.NavController

@Composable
fun CameraScreen(navController: NavController) {
    // TODO: Add your CameraX PreviewView and TFLite model inference here

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Camera is Active", style = MaterialTheme.typography.titleLarge)

        Button(onClick = { navController.popBackStack() }) {
            Text("Back")
        }
    }
}
