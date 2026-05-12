package com.nagarseva.app.ui.reports

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.font.FontWeight.Companion.Normal
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.nagarseva.app.data.local.entity.ReportEntity
import com.nagarseva.app.data.local.entity.getFormattedDate
import com.nagarseva.app.ui.components.StatusBadge
import com.nagarseva.app.ui.navigation.Screen
import com.nagarseva.app.ui.theme.*
import com.nagarseva.app.viewmodel.ReportViewModel
import java.io.File

@Composable
fun MyReportsScreen(
    navController: NavController
) {
    val reportViewModel: ReportViewModel = hiltViewModel()
    val reports by reportViewModel.userReports.collectAsStateWithLifecycle()
    val selectedFilter by reportViewModel.selectedFilter.collectAsStateWithLifecycle()
    val stats by reportViewModel.userStats.collectAsStateWithLifecycle()

    // Filter chips
    val filters = listOf(
        null to "All",
        "SUBMITTED" to "Submitted",
        "IN_REVIEW" to "In Review",
        "RESOLVED" to "Resolved"
    )

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(Screen.ReportStep1.route) },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "New Report")
            }
        }
    ) { paddingValues ->
        val layoutDirection = LocalLayoutDirection.current

        Column(
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(
                    top = paddingValues.calculateTopPadding(),
                    bottom = paddingValues.calculateBottomPadding(),
                    start = paddingValues.calculateStartPadding(layoutDirection),
                    end = paddingValues.calculateEndPadding(layoutDirection)
                )
        ) {

            // TOP BAR
            Surface(
                Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 2.dp
            ) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Menu",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        "My Reports",
                        fontSize = 20.sp,
                        fontWeight = Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center
                    )

                    // Bell icon clickable with badge
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .clickable {
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

            LazyColumn(
                Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {

                // SUMMARY CARD
                item {
                    Card(
                        Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSystemInDarkTheme()) MaterialTheme.colorScheme.surfaceVariant else GreenMint
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Text(
                                "Summary",
                                fontSize = 14.sp,
                                fontWeight = Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(Modifier.height(12.dp))
                            Row(
                                Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                // stats.first = total, stats.second = resolved, stats.third = pending
                                SummaryBox("${stats.first}", "Total", Modifier.weight(1f))
                                SummaryBox("${stats.second}", "Resolved", Modifier.weight(1f))
                                SummaryBox("${stats.third}", "Active", Modifier.weight(1f))
                            }
                        }
                    }
                }

                // FILTER CHIPS
                item {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(bottom = 8.dp)
                    ) {
                        items(filters) { (filterValue, label) ->
                            val isSelected = selectedFilter == filterValue
                            Surface(
                                modifier = Modifier.clickable {
                                    reportViewModel.setFilter(filterValue)
                                },
                                shape = RoundedCornerShape(50.dp),
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                                border = if (!isSelected) BorderStroke(1.dp, MaterialTheme.colorScheme.outline) else null
                            ) {
                                Text(
                                    label,
                                    color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 13.sp,
                                    fontWeight = if (isSelected) Bold else Normal,
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                                )
                            }
                        }
                    }
                }

                // REPORTS LIST
                if (reports.isEmpty()) {
                    item {
                        // Empty state
                        Box(
                            Modifier
                                .fillMaxWidth()
                                .padding(60.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = CenterHorizontally) {
                                Icon(
                                    Icons.AutoMirrored.Filled.Assignment, null,
                                    tint = MaterialTheme.colorScheme.outline,
                                    modifier = Modifier.size(64.dp)
                                )
                                Text(
                                    "No reports yet",
                                    fontSize = 16.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(top = 16.dp)
                                )
                                Text(
                                    "Tap + to report your first issue",
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.outline,
                                    modifier = Modifier.padding(top = 6.dp)
                                )
                            }
                        }
                    }
                } else {
                    items(reports) { report ->
                        ReportItemCard(report, navController)
                    }
                }
            }
        }
    }
}

@Composable
fun ReportItemCard(report: ReportEntity, navController: NavController) {
    Card(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clickable {
                navController.navigate(Screen.ReportDetail.createRoute(report.id))
            },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Photo thumbnail from real path
            if (report.photoPath.isNotEmpty()) {
                AsyncImage(
                    model = File(report.photoPath),
                    contentDescription = null,
                    modifier = Modifier
                        .size(60.dp)
                        .clip(RoundedCornerShape(10.dp)),
                    contentScale = ContentScale.Crop
                )
            } else {
                // Fallback icon
                Box(
                    Modifier
                        .size(60.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        when (report.defectType.lowercase()) {
                            "pothole" -> Icons.Default.Warning
                            "streetlight" -> Icons.Default.WbIncandescent
                            "garbage" -> Icons.Default.Delete
                            else -> Icons.Default.Description
                        },
                        null, tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            // Content
            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        report.issueTitle,
                        fontSize = 14.sp,
                        fontWeight = Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f),
                        maxLines = 1
                    )
                    StatusBadge(report.status)
                }
                Text(
                    report.address,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp),
                    maxLines = 1
                )
                Row(
                    Modifier.padding(top = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        Icons.Default.CalendarToday, null,
                        tint = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.size(12.dp)
                    )
                    Text(
                        if (report.status == "RESOLVED") "Resolved: ${report.getFormattedDate()}"
                        else "Reported: ${report.getFormattedDate()}",
                        fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun SummaryBox(
    value: String, label: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            Modifier.padding(12.dp),
            horizontalAlignment = CenterHorizontally
        ) {
            Text(
                value, fontSize = 24.sp,
                fontWeight = Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                label, fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
