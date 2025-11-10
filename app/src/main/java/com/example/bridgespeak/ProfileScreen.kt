package com.example.bridgespeak

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.foundation.BorderStroke

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController) {
    var darkModeEnabled by remember { mutableStateOf(false) }

    // 🎨 Dynamic colors based on dark mode
    val backgroundColor = if (darkModeEnabled) Color(0xFF121212) else Color(0xFFEFF3F9)
    val cardColor = if (darkModeEnabled) Color(0xFF1E1E1E) else Color.White
    val textColor = if (darkModeEnabled) Color.White else Color.Black
    val secondaryText = if (darkModeEnabled) Color.LightGray else Color.Gray

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_back),
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF003366))
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(backgroundColor),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // Profile Card
            Card(
                modifier = Modifier.fillMaxWidth(0.9f),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = cardColor)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(70.dp)
                            .background(Color(0xFF003366), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("CG", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 22.sp)
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text("Candesse Gabrielle", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = textColor)
                    Text(
                        "candiesbartolome05@gmail.com",
                        fontSize = 14.sp,
                        color = secondaryText,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedButton(
                        onClick = { /* TODO: Edit profile */ },
                        border = BorderStroke(1.dp, Color(0xFF003366)),
                        shape = RoundedCornerShape(30.dp)
                    ) {
                        Text("Edit Profile", color = Color(0xFF003366))
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Account Settings
            SettingsSection(
                title = "Account Settings",
                items = listOf(
                    SettingItem("Change Email", R.drawable.ic_email),
                    SettingItem("Change Password", R.drawable.ic_lock)
                ),
                textColor = textColor
            )

            Spacer(modifier = Modifier.height(10.dp))

            // App Settings Section
            Card(
                modifier = Modifier.fillMaxWidth(0.9f),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = cardColor)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("App Settings", fontWeight = FontWeight.Bold, color = textColor)
                    Spacer(modifier = Modifier.height(10.dp))

                    // 🌙 Dark Mode Toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_darkmode),
                                contentDescription = null,
                                tint = textColor,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Dark Mode", color = textColor)
                        }
                        Switch(
                            checked = darkModeEnabled,
                            onCheckedChange = { darkModeEnabled = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color(0xFF003366),
                                uncheckedThumbColor = Color.Gray
                            )
                        )
                    }

                    Divider(modifier = Modifier.padding(vertical = 10.dp), color = Color.Gray.copy(alpha = 0.3f))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_language),
                            contentDescription = null,
                            tint = textColor
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text("Language", color = textColor)
                            Text("English", color = secondaryText, fontSize = 14.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Logout Button
            OutlinedButton(
                onClick = { navController.navigate("login") },
                border = BorderStroke(1.dp, Color.Red),
                shape = RoundedCornerShape(30.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red),
                modifier = Modifier.fillMaxWidth(0.9f)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_logout),
                    contentDescription = "Logout",
                    tint = Color.Red
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Log out", color = Color.Red)
            }
        }
    }
}

@Composable
fun SettingsSection(title: String, items: List<SettingItem>, textColor: Color) {
    Card(
        modifier = Modifier.fillMaxWidth(0.9f),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, fontWeight = FontWeight.Bold, color = textColor)
            Spacer(modifier = Modifier.height(10.dp))
            items.forEach { item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = item.icon),
                        contentDescription = item.title,
                        tint = textColor
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(item.title, color = textColor)
                }
            }
        }
    }
}

data class SettingItem(val title: String, val icon: Int)
