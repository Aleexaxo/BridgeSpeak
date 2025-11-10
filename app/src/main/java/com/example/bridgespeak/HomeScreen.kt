package com.example.bridgespeak

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun HomeScreen(navController: NavController, userName: String = "Candesse") {
    Scaffold(
        // bottomBar removed
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFE8EBF1))
                .padding(innerPadding)
        ) {
            // Top Section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF003366))
                    .padding(16.dp)
            ) {
                Column {
                    Text(
                        text = "Welcome, $userName!",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Breaking Barriers through communication",
                        fontSize = 14.sp,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 2x2 Grid of Options
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .weight(1f, fill = true),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    HomeOptionCard(
                        title = "Image Translation",
                        description = "Upload or capture photos of hand signs.",
                        icon = R.drawable.ic_image,
                        onClick = { navController.navigate("image_translation") },
                        modifier = Modifier.weight(1f)
                    )
                    HomeOptionCard(
                        title = "Profile",
                        description = "Edit your personal information.",
                        icon = R.drawable.ic_profile,
                        onClick = { navController.navigate("profile") },
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    HomeOptionCard(
                        title = "History",
                        description = "View saved translations.",
                        icon = R.drawable.ic_history,
                        onClick = { navController.navigate("history") },
                        modifier = Modifier.weight(1f)
                    )
                    HomeOptionCard(
                        title = "Help and About",
                        description = "Learn how to use BridgeSpeak.",
                        icon = R.drawable.ic_help,
                        onClick = { navController.navigate("about") },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun HomeOptionCard(
    title: String,
    description: String,
    icon: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(200.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            Image(
                painter = painterResource(id = icon),
                contentDescription = title,
                modifier = Modifier.size(50.dp)
            )
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Text(
                text = description,
                fontSize = 12.sp,
                color = Color.Gray,
                lineHeight = 14.sp,
                modifier = Modifier.padding(horizontal = 4.dp),
                textAlign = TextAlign.Center
            )
        }
    }
}
