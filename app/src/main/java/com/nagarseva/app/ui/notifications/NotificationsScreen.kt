package com.nagarseva.app.ui.notifications

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.nagarseva.app.ui.components.CircleBorderBackButton
import com.nagarseva.app.ui.theme.*

@Composable
fun NotificationsScreen(
    navController: NavController
) {
    // Sample notifications data class
    data class AppNotification(
        val id: Int,
        val title: String,
        val message: String,
        val time: String,
        val icon: ImageVector,
        val iconColor: Color,
        val isRead: Boolean = false
    )
    
    // For now: static notifications (no real backend — v1.0)
    val notifications = remember {
        listOf(
            AppNotification(
                1,
                "Report Submitted",
                "Your pothole report NS-20250428-0042 has been successfully submitted.",
                "2 hours ago",
                Icons.Filled.CheckCircle,
                Color(0xFF2E7D32),
                false
            ),
            AppNotification(
                2,
                "Welcome to NagarSeva!",
                "Thank you for joining. Help us make your city better, one tap at a time.",
                "Today",
                Icons.Filled.Celebration,
                Color(0xFFFF8F00),
                true
            ),
            AppNotification(
                3,
                "App Update Available",
                "NagarSeva v2.0 is coming with more civic services. Stay tuned!",
                "Yesterday",
                Icons.Filled.SystemUpdate,
                Color(0xFF1565C0),
                true
            )
        )
    }
    
    Scaffold { padding ->
        Column(
            Modifier.fillMaxSize()
                .background(Color(0xFFF5F5F5))
                .padding(padding)
        ) {
            // TOP BAR
            Surface(
                Modifier.fillMaxWidth(),
                color = Color.White,
                shadowElevation = 2.dp
            ) {
                Row(
                    Modifier.fillMaxWidth()
                        .height(60.dp)
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircleBorderBackButton {
                        navController.popBackStack()
                    }
                    Text(
                        "Notifications",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1B5E20),
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center
                    )
                    // Mark all read (placeholder)
                    TextButton(
                        onClick = { }
                    ) {
                        Text(
                            "Mark all read",
                            color = Color(0xFF1B5E20),
                            fontSize = 12.sp
                        )
                    }
                }
            }
            
            if (notifications.isEmpty()) {
                // EMPTY STATE
                Box(
                    Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Filled.NotificationsNone,
                            contentDescription = null,
                            tint = Color(0xFFBDBDBD),
                            modifier = Modifier.size(80.dp)
                        )
                        Text(
                            "No notifications yet",
                            fontSize = 16.sp,
                            color = Color(0xFF757575),
                            modifier = Modifier.padding(top = 16.dp)
                        )
                        Text(
                            "We'll notify you when your reports are updated",
                            fontSize = 13.sp,
                            color = Color(0xFFBDBDBD),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 8.dp).padding(horizontal = 40.dp)
                        )
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(notifications) { notif ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 4.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (notif.isRead) Color.White else Color(0xFFF1F8E9)
                            ),
                            elevation = CardDefaults.cardElevation(
                                defaultElevation = if (notif.isRead) 1.dp else 2.dp
                            )
                        ) {
                            Row(
                                Modifier.fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.Top,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                // Icon circle
                                Box(
                                    Modifier
                                        .size(44.dp)
                                        .background(
                                            notif.iconColor.copy(0.15f),
                                            CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        notif.icon,
                                        contentDescription = null,
                                        tint = notif.iconColor,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                                
                                // Content
                                Column(
                                    Modifier.weight(1f)
                                ) {
                                    Row(
                                        Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            notif.title,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF212121),
                                            modifier = Modifier.weight(1f)
                                        )
                                        if (!notif.isRead) {
                                            Box(
                                                Modifier
                                                    .size(8.dp)
                                                    .background(Color(0xFF1B5E20), CircleShape)
                                            )
                                        }
                                    }
                                    Text(
                                        notif.message,
                                        fontSize = 12.sp,
                                        color = Color(0xFF757575),
                                        lineHeight = 18.sp,
                                        modifier = Modifier.padding(top = 4.dp)
                                    )
                                    Text(
                                        notif.time,
                                        fontSize = 11.sp,
                                        color = Color(0xFFBDBDBD),
                                        modifier = Modifier.padding(top = 6.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
