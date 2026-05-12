package com.nagarseva.app.ui.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.nagarseva.app.data.local.entity.getInitials
import com.nagarseva.app.ui.components.CircleBorderBackButton
import com.nagarseva.app.ui.navigation.Screen
import com.nagarseva.app.ui.theme.*
import com.nagarseva.app.util.ThemeManager
import com.nagarseva.app.viewmodel.AuthViewModel
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen(
    navController: NavController,
    showBackButton: Boolean = false
) {
    val authViewModel: AuthViewModel = hiltViewModel()
    val currentUser by authViewModel.currentUser.collectAsStateWithLifecycle()
    
    val context = LocalContext.current
    val themeManager = remember { ThemeManager(context) }
    val isDarkMode by themeManager.isDarkModeFlow.collectAsStateWithLifecycle(initialValue = false)
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            Row(
                Modifier.fillMaxWidth()
                    .height(60.dp)
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (showBackButton) {
                    CircleBorderBackButton { 
                        navController.popBackStack() 
                    }
                } else {
                    Spacer(Modifier.size(36.dp))
                }
                
                Text(
                    "Profile Settings",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
                
                // Bell icon clickable
                Box(
                    modifier = Modifier.clickable {
                        navController.navigate(Screen.Notifications.route)
                    }
                ) {
                    Icon(
                        Icons.Default.Notifications,
                        contentDescription = "Notifications",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    // Notification dot badge
                    Box(
                        Modifier
                            .size(8.dp)
                            .background(Saffron, CircleShape)
                            .align(Alignment.TopEnd)
                    )
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Profile Header
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(MaterialTheme.colorScheme.primary, CircleShape)
                    .clickable { navController.navigate(Screen.EditProfile.route) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = currentUser?.getInitials() ?: "NS",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = Bold
                )
            }
            
            Text(
                text = currentUser?.fullName ?: "User",
                modifier = Modifier.padding(top = 16.dp),
                fontSize = 18.sp,
                fontWeight = Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            
            Text(
                text = currentUser?.email ?: "",
                modifier = Modifier.padding(top = 4.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 13.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Info Items
            InfoItem("Phone", currentUser?.phone?.ifBlank { "Not provided" } ?: "Not provided", Icons.Default.Phone)
            InfoItem("Address", currentUser?.residentialAddress?.ifBlank { "Not provided" } ?: "Not provided", Icons.Default.Home)
            InfoItem("Ward", "Ward ${currentUser?.wardNumber?.ifBlank { "N/A" } ?: "N/A"}", Icons.Default.Map)

            Spacer(modifier = Modifier.height(24.dp))

            // Dark Mode Toggle Row
            Row(
                Modifier.fillMaxWidth()
                    .padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(MaterialTheme.colorScheme.secondaryContainer, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Filled.DarkMode,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Column(
                    Modifier.weight(1f)
                        .padding(start = 16.dp)
                ) {
                    Text(
                        "Dark Mode",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        if (isDarkMode) "On" else "Off",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(
                    checked = isDarkMode,
                    onCheckedChange = { enabled ->
                        coroutineScope.launch {
                            themeManager.setDarkMode(enabled)
                        }
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = MaterialTheme.colorScheme.primary,
                        uncheckedThumbColor = Color.White,
                        uncheckedTrackColor = GreyLight
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Actions
            OutlinedButton(
                onClick = { /* Security screen placeholder */ },
                modifier = Modifier.fillMaxWidth().height(54.dp),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Icon(Icons.Default.Security, null, tint = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.width(12.dp))
                Text("Security Settings", color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Medium)
                Spacer(Modifier.weight(1f))
                Icon(Icons.Default.ChevronRight, null, tint = GreyLight)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { 
                    authViewModel.logout()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(54.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ErrorRed)
            ) {
                Icon(Icons.AutoMirrored.Filled.Logout, null)
                Spacer(Modifier.width(12.dp))
                Text("Logout", fontWeight = Bold)
            }
        }
    }
}

@Composable
fun InfoItem(label: String, value: String, icon: ImageVector) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(MaterialTheme.colorScheme.secondaryContainer, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
        }
        Column(modifier = Modifier.padding(start = 16.dp)) {
            Text(label, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Medium)
            Text(value, fontSize = 15.sp, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.SemiBold)
        }
    }
}
