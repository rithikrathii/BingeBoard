package com.example.cinerate.ui.screens.about

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.School
import androidx.compose.material.icons.rounded.Work
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.cinerate.R
import com.example.cinerate.ui.components.TeamMemberRow
import com.example.cinerate.ui.theme.Background
import com.example.cinerate.ui.theme.CardSurface
import com.example.cinerate.ui.theme.GoldAccent
import com.example.cinerate.ui.theme.SecondaryText

@Composable
fun AboutScreen(
    onNavigateToLogin: () -> Unit,
    onLogout: () -> Unit,
    viewModel: AboutViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showLogoutDialog by remember { mutableStateOf(false) }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = {
                Text(
                    text = "Logout",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White
                )
            },
            text = {
                Text(
                    text = "Are you sure you want to log out of BingeBoard?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = SecondaryText
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLogoutDialog = false
                        onLogout()
                    }
                ) {
                    Text(
                        text = "Logout",
                        color = GoldAccent,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text(
                        text = "Cancel",
                        color = SecondaryText
                    )
                }
            },
            containerColor = CardSurface,
            shape = RoundedCornerShape(16.dp)
        )
    }

    Scaffold(
        containerColor = Background
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(32.dp))
                
                // Logo
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(GoldAccent),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.movie_frame),
                        contentDescription = "App Logo",
                        modifier = Modifier.size(55.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "BingeBoard",
                    style = MaterialTheme.typography.titleLarge.copy(fontSize = 24.sp),
                    color = Color.White
                )

                Text(
                    text = "Your Movie Companion",
                    style = MaterialTheme.typography.bodyMedium.copy(fontStyle = FontStyle.Italic),
                    color = SecondaryText
                )

                Spacer(modifier = Modifier.height(32.dp))
                
                HorizontalDivider(color = SecondaryText.copy(alpha = 0.2f))
                
                Spacer(modifier = Modifier.height(32.dp))
            }

            // About Card
            item {
                AboutInfoCard(
                    icon = Icons.Rounded.Info,
                    title = "About This App",
                    description = "BingeBoard is a movie discovery and rating platform designed to help users explore, review, and track their favorite films. Built with a focus on clean UI and intuitive navigation for the modern cinephile."
                )
                
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Team Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = CardSurface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Rounded.Work, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Project Team",
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.White
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        val team = listOf("Anna Müller", "Ben Hoffmann", "Clara Schmidt", "David Krause")
                        team.forEachIndexed { index, name ->
                            TeamMemberRow(name = name, showDivider = index < team.lastIndex)
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
            }

            // University Section
            item {
                AboutInfoCard(
                    icon = Icons.Rounded.School,
                    title = "University",
                    description = "Hochschule Rhein-Waal\nDistributed Systems Project"
                )
                
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Auth Section
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (uiState.currentUser != null) {
                        Text(
                            text = "Logged in as ${uiState.currentUser?.fullName}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = SecondaryText
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedButton(
                            onClick = { showLogoutDialog = true },
                            modifier = Modifier.fillMaxWidth().height(52.dp),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, GoldAccent),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = GoldAccent)
                        ) {
                            Text("Logout", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                        }
                    } else {
                        Button(
                            onClick = onNavigateToLogin,
                            modifier = Modifier.fillMaxWidth().height(52.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = GoldAccent, contentColor = Color.Black)
                        ) {
                            Text("Login", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Version Info
                Text(
                    text = "Version 1.0.0",
                    style = MaterialTheme.typography.labelSmall,
                    color = SecondaryText,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                
            }
        }
    }
}

@Composable
private fun AboutInfoCard(
    icon: ImageVector,
    title: String,
    description: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CardSurface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = SecondaryText,
                lineHeight = 20.sp
            )
        }
    }
}
