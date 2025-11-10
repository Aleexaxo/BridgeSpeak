package com.example.bridgespeak

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(navController: NavController) {
    var expandedSection by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Help and About",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF003366))
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .background(Color(0xFFEFF3F9))
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // App Info Card
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF003366)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text("BridgeSpeak", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text("Version 1.0.0", color = Color.White.copy(alpha = 0.8f))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Breaking barriers through AI-powered sign language translation",
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // How to Use
            Text(
                "How to Use BridgeSpeak",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF003366),
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Expandable Sections
            ExpandableSection(
                title = "Getting Started",
                content = {
                    Text(
                        "1. Create an account or log in\n" +
                                "2. Navigate to the home screen\n" +
                                "3. Choose between Image or Video translation\n" +
                                "4. Upload or capture sign language content"
                    )
                },
                isExpanded = expandedSection == "Getting Started",
                onClick = { expandedSection = toggle(expandedSection, "Getting Started") }
            )

            ExpandableSection(
                title = "Image Translation",
                content = {
                    Text(
                        "Upload or capture a photo of someone performing sign language. " +
                                "Our AI will analyze the hand gestures and translate them into text. " +
                                "You can also use text-to-speech to hear the translation."
                    )
                },
                isExpanded = expandedSection == "Image Translation",
                onClick = { expandedSection = toggle(expandedSection, "Image Translation") }
            )

            ExpandableSection(
                title = "Video Translation",
                content = {
                    Text(
                        "Record or upload a video of sign language sentences. " +
                                "The AI will process the entire sequence and provide a complete translation " +
                                "with the option to play audio."
                    )
                },
                isExpanded = expandedSection == "Video Translation",
                onClick = { expandedSection = toggle(expandedSection, "Video Translation") }
            )

            ExpandableSection(
                title = "Learning Features",
                content = {
                    Text(
                        "Access our educational section to learn sign language alphabet (A-Z), " +
                                "numbers (0-9), and common words. Each sign includes a video demonstration."
                    )
                },
                isExpanded = expandedSection == "Learning Features",
                onClick = { expandedSection = toggle(expandedSection, "Learning Features") }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Development Team Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Development Team", fontWeight = FontWeight.Bold, color = Color(0xFF003366))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("💙 Built with love by the BridgeSpeak Team")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Our mission is to break down communication barriers and create a more " +
                                "inclusive world through AI technology."
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Contact & Feedback Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Contact & Feedback", fontWeight = FontWeight.Bold, color = Color(0xFF003366))
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = "",
                        onValueChange = {},
                        placeholder = { Text("Your name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = "",
                        onValueChange = {},
                        placeholder = { Text("your.email@example.com") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = "",
                        onValueChange = {},
                        placeholder = { Text("Share your feedback or ask a question...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = { /* TODO: handle send feedback */ },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0066CC)),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Send Feedback", color = Color.White)
                    }
                }
            }
        }
    }
}

// Helper Composable for collapsible sections
@Composable
fun ExpandableSection(
    title: String,
    content: @Composable () -> Unit,
    isExpanded: Boolean,
    onClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(title, fontWeight = FontWeight.Bold, color = Color(0xFF003366))
                IconButton(onClick = onClick) {
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = null,
                        tint = Color(0xFF003366)
                    )
                }
            }
            if (isExpanded) {
                Spacer(modifier = Modifier.height(8.dp))
                content()
            }
        }
    }
}

// Helper function: ensures only one section open at a time
private fun toggle(current: String?, selected: String): String? {
    return if (current == selected) null else selected
}
