package com.nagarseva.app.ui.home

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.nagarseva.app.data.local.entity.getInitials
import com.nagarseva.app.data.local.entity.getRelativeTime
import com.nagarseva.app.ui.components.StatPill
import com.nagarseva.app.ui.navigation.Screen
import com.nagarseva.app.ui.theme.*
import com.nagarseva.app.viewmodel.AuthViewModel
import com.nagarseva.app.viewmodel.ReportViewModel

@Composable
fun HomeScreen(
    navController: NavController
) {
    val authViewModel: AuthViewModel = hiltViewModel()
    val reportViewModel: ReportViewModel = hiltViewModel()
    
    val currentUser by authViewModel.currentUser.collectAsStateWithLifecycle()
    val stats by reportViewModel.userStats.collectAsStateWithLifecycle()
    val recentActivity by reportViewModel.recentActivity.collectAsStateWithLifecycle()

    val scrollState = rememberScrollState()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate(Screen.ReportStep1.route)
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = CircleShape,
                modifier = Modifier.size(56.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Report",
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .background(MaterialTheme.colorScheme.background)
                .padding(padding)
        ) {
            // ── GREEN HEADER ZONE ──────────────────
            Box(Modifier.fillMaxWidth()) {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.primary)
                ) {
                    // APP BAR ROW
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .height(60.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Logo circle + Name
                        Box(
                            Modifier
                                .size(32.dp)
                                .background(Color.White.copy(alpha = 0.2f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Apartment,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Text(
                            text = "NagarSeva",
                            color = Color.White,
                            fontSize = 19.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(start = 8.dp)
                        )

                        Spacer(Modifier.weight(1f))

                        // Bell with badge
                        Box(
                            modifier = Modifier.clickable {
                                navController.navigate(Screen.Notifications.route)
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Notifications,
                                contentDescription = "Notifications",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                            Box(
                                Modifier
                                    .size(8.dp)
                                    .background(Saffron, CircleShape)
                                    .align(Alignment.TopEnd)
                            )
                        }

                        Spacer(Modifier.width(12.dp))

                        // Avatar
                        Box(
                            Modifier
                                .size(34.dp)
                                .background(MaterialTheme.colorScheme.secondary, CircleShape)
                                .clickable {
                                    navController.navigate(Screen.Profile.route) {
                                        popUpTo(Screen.Home.route) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = currentUser?.getInitials() ?: "NS",
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // GREETING SECTION
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 12.dp)
                    ) {
                        Text(
                            text = "${authViewModel.getGreeting()}, " +
                                    "${currentUser?.fullName?.split(" ")
                                        ?.firstOrNull() ?: "there"} 👋",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "What would you like to report today?",
                            color = Color.White.copy(alpha = 0.75f),
                            fontSize = 13.sp,
                            modifier = Modifier.padding(top = 6.dp)
                        )
                        Spacer(Modifier.height(4.dp))
                    }

                    // WAVE TRANSITION
                    val waveColor = if (isSystemInDarkTheme()) Color(0xFF121212) else Color.White
                    Canvas(
                        Modifier
                            .fillMaxWidth()
                            .height(36.dp)
                    ) {
                        val path = Path()
                        path.moveTo(0f, size.height)
                        path.cubicTo(
                            size.width * 0.3f, 0f,
                            size.width * 0.7f, 0f,
                            size.width, size.height
                        )
                        path.lineTo(size.width, size.height)
                        path.lineTo(0f, size.height)
                        path.close()
                        drawPath(path, color = waveColor)
                    }
                }
            }
            // ── END GREEN HEADER ──────────────────

            // ── WHITE BODY ────────────────────────
            Column(
                Modifier
                    .fillMaxWidth()
            ) {
                // STATS PILLS ROW
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StatPill("${stats.second}", "Solved", StatusSubmitted, MaterialTheme.colorScheme.secondaryContainer)
                    StatPill("${stats.third}", "Pending", MaterialTheme.colorScheme.secondary, MaterialTheme.colorScheme.secondaryContainer)
                    StatPill("${stats.third}", "Alerts", Saffron, SaffronBackground)
                }

                // QUICK ACTIONS HEADER
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Quick Actions",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = "View all",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.clickable {
                            navController.navigate(Screen.Services.route)
                        }
                    )
                }

                // ACTION CARDS ROW (3 cards)
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Card 1 — Report Issue (dashed border)
                    val outlineColor = MaterialTheme.colorScheme.secondary
                    Box(
                        Modifier
                            .weight(1f)
                            .height(100.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surface)
                            .drawBehind {
                                val stroke = Stroke(
                                    width = 1.5.dp.toPx(),
                                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                                )
                                drawRoundRect(
                                    color = outlineColor,
                                    style = stroke,
                                    cornerRadius = CornerRadius(12.dp.toPx())
                                )
                            }
                            .clickable {
                                navController.navigate(Screen.ReportStep1.route)
                            }
                    ) {
                        Column(
                            Modifier
                                .fillMaxSize()
                                .padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Warning,
                                contentDescription = null,
                                tint = Saffron,
                                modifier = Modifier.size(28.dp)
                            )
                            Text(
                                text = "Report Issue",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                            Text(
                                text = "Grievance portal",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    // Card 2 — Track Ticket
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .height(100.dp)
                            .clickable {
                                navController.navigate(Screen.Tracker.route)
                            },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Column(
                            Modifier
                                .fillMaxSize()
                                .padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.ConfirmationNumber,
                                contentDescription = null,
                                tint = StatusSubmitted,
                                modifier = Modifier.size(28.dp)
                            )
                            Text(
                                text = "Track Ticket",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                            Text(
                                text = "Status & updates",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    // Card 3 — My Reports
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .height(100.dp)
                            .clickable {
                                navController.navigate(Screen.MyReports.route)
                            },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Column(
                            Modifier
                                .fillMaxSize()
                                .padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Description,
                                contentDescription = null,
                                tint = StatusSubmitted,
                                modifier = Modifier.size(28.dp)
                            )
                            Text(
                                text = "My Reports",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                            Text(
                                text = "History",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                // RECENT ACTIVITY
                Text(
                    text = "Recent Activity",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(start = 20.dp, top = 8.dp, bottom = 12.dp)
                )

                if (recentActivity.isEmpty()) {
                    Text(
                        "No recent activity",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)
                    )
                } else {
                    recentActivity.take(2).forEach { report ->
                        ActivityRow(
                            accentColor = when(report.status) {
                                "RESOLVED" -> MaterialTheme.colorScheme.secondary
                                "IN_REVIEW" -> Saffron
                                else -> StatusSubmitted
                            },
                            title = "${report.defectType.getDisplayName()} — ${
                                report.status.replace("_"," ")
                                    .lowercase()
                                    .replaceFirstChar { it.uppercase() }
                            }",
                            subtitle = report.address,
                            time = report.getRelativeTime()
                        )
                    }
                }

                Spacer(Modifier.height(80.dp))
            }
        }
    }
}

@Composable
fun ActivityRow(
    accentColor: Color,
    title: String,
    subtitle: String,
    time: String
) {
    Column {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Accent bar
            Box(
                Modifier
                    .width(4.dp)
                    .height(48.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(accentColor)
            )
            // Content
            Column(
                Modifier
                    .weight(1f)
                    .padding(start = 12.dp)
            ) {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 2.dp),
                    maxLines = 2
                )
            }
            Text(
                text = time,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
        HorizontalDivider(
            Modifier.padding(horizontal = 16.dp),
            color = MaterialTheme.colorScheme.outlineVariant,
            thickness = 0.5.dp
        )
    }
}

fun String.getDisplayName(): String = when(this) {
    "POTHOLE" -> "Pothole"
    "STREETLIGHT" -> "Broken Light"
    "GARBAGE" -> "Garbage"
    "WATERLEAK" -> "Water Leak"
    else -> this
}
