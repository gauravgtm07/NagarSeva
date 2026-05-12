package com.nagarseva.app.ui.tracker

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.nagarseva.app.data.local.entity.getFormattedDate
import com.nagarseva.app.ui.components.NagarSevaCard
import com.nagarseva.app.ui.components.StatusBadge
import com.nagarseva.app.ui.navigation.Screen
import com.nagarseva.app.ui.theme.*
import com.nagarseva.app.util.UiState
import com.nagarseva.app.viewmodel.ReportViewModel

@Composable
fun TrackerScreen(navController: NavController) {
    var ticketInput by remember { mutableStateOf("") }
    val reportViewModel: ReportViewModel = hiltViewModel()
    val trackerState by reportViewModel.trackerState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .background(White)
                .padding(padding)
        ) {

            // TOP APP BAR
            Surface(
                Modifier.fillMaxWidth(),
                color = White,
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
                        Icons.Default.Menu, null,
                        tint = GreenPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        "Status Tracker",
                        fontSize = 18.sp,
                        fontWeight = Bold,
                        color = GreenPrimary,
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
                            Icons.Default.Notifications, null,
                            tint = GreenPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                        Box(
                            Modifier
                                .size(8.dp)
                                .background(Saffron, CircleShape)
                                .align(Alignment.TopEnd)
                        )
                    }
                }
            }

            Column(
                Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {

                // SEARCH CARD
                NagarSevaCard(Modifier.fillMaxWidth()) {
                    Text(
                        "Track your report",
                        fontSize = 15.sp,
                        fontWeight = Bold,
                        color = Charcoal
                    )
                    Spacer(Modifier.height(12.dp))
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .border(
                                1.dp, GreyDivider,
                                RoundedCornerShape(12.dp)
                            ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Search, null,
                            tint = GreyText,
                            modifier = Modifier
                                .padding(start = 12.dp)
                                .size(20.dp)
                        )
                        BasicTextField(
                            value = ticketInput,
                            onValueChange = { ticketInput = it },
                            modifier = Modifier
                                .weight(1f)
                                .padding(14.dp),
                            textStyle = TextStyle(
                                fontSize = 14.sp,
                                color = Charcoal
                            ),
                            decorationBox = { innerTextField ->
                                Box {
                                    if (ticketInput.isEmpty()) {
                                        Text(
                                            "NS-20250428-0042",
                                            color = GreyLight,
                                            fontSize = 14.sp
                                        )
                                    }
                                    innerTextField()
                                }
                            }
                        )
                        Box(
                            Modifier
                                .padding(4.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(GreenPrimary)
                                .clickable {
                                    if (ticketInput.isNotBlank()) {
                                        // REAL DB QUERY via ViewModel
                                        reportViewModel.trackReport(ticketInput.trim().uppercase())
                                    }
                                }
                                .padding(
                                    horizontal = 18.dp,
                                    vertical = 10.dp
                                )
                        ) {
                            Text(
                                "Track",
                                color = White,
                                fontSize = 13.sp,
                                fontWeight = Bold
                            )
                        }
                    }
                    Text(
                        "Format: NS-YYYYMMDD-XXXX",
                        color = GreyText,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(top = 6.dp)
                    )
                }

                // RESULT AREA
                Spacer(Modifier.height(16.dp))

                when (val state = trackerState) {
                    is UiState.Loading -> {
                        Box(
                            Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = GreenPrimary)
                        }
                    }
                    is UiState.Success -> {
                        val report = state.data
                        NagarSevaCard(Modifier.fillMaxWidth()) {
                            Row(
                                Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(Modifier.weight(1f)) {
                                    Text(
                                        "TICKET ID",
                                        color = GreyText,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        letterSpacing = 1.sp
                                    )
                                    Text(
                                        report.ticketId,
                                        fontSize = 16.sp,
                                        fontWeight = Bold,
                                        color = Charcoal,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                                StatusBadge(report.status)
                            }

                            HorizontalDivider(
                                Modifier.padding(vertical = 12.dp),
                                color = GreyDivider
                            )

                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(GreySurface)
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    Modifier
                                        .size(56.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(GreyDivider),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        when (report.defectType.lowercase()) {
                                            "pothole" -> Icons.Default.Warning
                                            "streetlight" -> Icons.Default.WbIncandescent
                                            "garbage" -> Icons.Default.Delete
                                            else -> Icons.Default.Description
                                        },
                                        null,
                                        tint = GreyText,
                                        modifier = Modifier.size(28.dp)
                                    )
                                }
                                Column {
                                    Text(
                                        report.issueTitle,
                                        fontSize = 14.sp,
                                        fontWeight = Bold,
                                        color = Charcoal
                                    )
                                    Text(
                                        report.address,
                                        fontSize = 12.sp,
                                        color = GreyText
                                    )
                                }
                            }

                            Spacer(Modifier.height(16.dp))

                            val stepState = when (report.status) {
                                "SUBMITTED" -> Pair(1, "submitted")
                                "IN_REVIEW" -> Pair(2, "in_review")
                                "RESOLVED" -> Pair(3, "resolved")
                                else -> Pair(1, "submitted")
                            }

                            Row(
                                Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                StepDot(
                                    label = "Submitted",
                                    subLabel = report.getFormattedDate(),
                                    state = if (stepState.first > 1) "done" else if (stepState.first == 1) "active" else "pending"
                                )
                                StepConnector(filled = stepState.first > 1)
                                StepDot(
                                    label = "In Review",
                                    subLabel = if (stepState.first > 2) "Verified" else if (stepState.first == 2) "Processing" else "Waiting",
                                    state = if (stepState.first > 2) "done" else if (stepState.first == 2) "active" else "pending"
                                )
                                StepConnector(filled = stepState.first > 2)
                                StepDot(
                                    label = "Resolved",
                                    subLabel = if (stepState.first == 3) "Completed" else "Expected 48h",
                                    state = if (stepState.first == 3) "done" else "pending"
                                )
                            }

                            Spacer(Modifier.height(16.dp))

                            Text(
                                "View Full History →",
                                color = GreenPrimary,
                                fontSize = 13.sp,
                                fontWeight = Bold,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .wrapContentWidth(Alignment.End)
                                    .clickable {
                                        navController.navigate(Screen.ReportDetail.createRoute(report.id))
                                    }
                            )
                        }
                    }
                    is UiState.Error -> {
                        NagarSevaCard(
                            Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Icon(
                                    Icons.Filled.SearchOff, null,
                                    tint = GreyText,
                                    modifier = Modifier.size(32.dp)
                                )
                                Column {
                                    Text(
                                        "Not Found", fontSize = 15.sp,
                                        fontWeight = Bold,
                                        color = Charcoal
                                    )
                                    Text(
                                        state.message,
                                        fontSize = 13.sp,
                                        color = GreyText,
                                        lineHeight = 20.sp
                                    )
                                }
                            }
                        }
                    }
                    else -> {}
                }

                Spacer(Modifier.height(16.dp))

                Card(
                    Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = GreenLight),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            Modifier
                                .size(40.dp)
                                .background(GreenPrimary, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Filled.HelpOutline, null,
                                tint = White,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Column(Modifier.weight(1f)) {
                            Text(
                                "Need assistance?",
                                fontSize = 14.sp,
                                fontWeight = Bold,
                                color = GreenPrimary
                            )
                            Text(
                                "Connect with a citizen officer",
                                fontSize = 12.sp,
                                color = GreyText
                            )
                        }
                        Icon(
                            Icons.Default.ChevronRight, null,
                            tint = GreenPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(Modifier.height(80.dp))
            }
        }
    }
}

@Composable
fun StepDot(
    label: String,
    subLabel: String,
    state: String
) {
    Column(
        horizontalAlignment = CenterHorizontally,
        modifier = Modifier.width(80.dp)
    ) {
        val bgColor = when (state) {
            "done" -> GreenPrimary
            "active" -> Saffron
            else -> GreyDivider
        }
        val iconTint = when (state) {
            "done", "active" -> White
            else -> GreyText
        }
        val size = if (state == "active") 34.dp else 28.dp

        Box(contentAlignment = Alignment.Center) {
            if (state == "active") {
                val infiniteTransition = rememberInfiniteTransition(label = "pulse")
                val scale by infiniteTransition.animateFloat(
                    initialValue = 1f,
                    targetValue = 1.3f,
                    animationSpec = infiniteRepeatable(
                        tween(800),
                        RepeatMode.Reverse
                    ),
                    label = "scale"
                )
                Box(
                    Modifier
                        .size(size * scale)
                        .background(Saffron.copy(alpha = 0.3f), CircleShape)
                )
            }
            Box(
                Modifier
                    .size(size)
                    .background(bgColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (state == "done")
                        Icons.Filled.Check
                    else if (state == "active")
                        Icons.Filled.Schedule
                    else Icons.Filled.RadioButtonUnchecked,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
        Text(
            text = label,
            color = if (state == "pending") GreyText else Charcoal,
            fontSize = 11.sp,
            fontWeight = Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 6.dp)
        )
        Text(
            text = subLabel,
            color = GreyText,
            fontSize = 10.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun RowScope.StepConnector(filled: Boolean) {
    Box(
        modifier = Modifier
            .weight(1f)
            .height(3.dp)
            .clip(RoundedCornerShape(2.dp))
            .background(if (filled) GreenSecondary else GreyDivider)
    )
}
