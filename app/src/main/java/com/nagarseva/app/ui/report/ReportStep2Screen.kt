package com.nagarseva.app.ui.report

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.nagarseva.app.data.local.entity.ReportEntity
import com.nagarseva.app.ui.components.*
import com.nagarseva.app.ui.navigation.Screen
import com.nagarseva.app.ui.theme.*
import com.nagarseva.app.util.UiState
import com.nagarseva.app.viewmodel.ReportViewModel

@Composable
fun ReportStep2Screen(
    navController: NavController,
    issueTitle: String,
    issueType: String,
    latitude: Float,
    longitude: Float,
    address: String
) {
    val reportViewModel: ReportViewModel = hiltViewModel()
    val submitState by reportViewModel.submitState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    var description by remember { mutableStateOf("") }
    var photoUri by remember { mutableStateOf<Uri?>(null) }
    
    // Add state variable at top of composable:
    var selectedSeverity by remember { mutableStateOf("MEDIUM") }
    
    val lifecycleOwner = LocalLifecycleOwner.current

    // Observe submit state
    LaunchedEffect(submitState) {
        when (submitState) {
            is UiState.Success -> {
                val report = (submitState as UiState.Success<ReportEntity>).data
                // Navigate to Confirmation with REAL ticket ID
                navController.navigate("confirmation/${report.ticketId}") {
                    popUpTo(Screen.ReportStep1.route) { inclusive = true }
                }
                reportViewModel.resetSubmitState()
            }
            is UiState.Error -> {
                snackbarHostState.showSnackbar(
                    (submitState as UiState.Error).message,
                    duration = SnackbarDuration.Long
                )
                reportViewModel.resetSubmitState()
            }
            else -> {}
        }
    }

    // Listen for photo result from CameraScreen
    LaunchedEffect(Unit) {
        navController.currentBackStackEntry
            ?.savedStateHandle
            ?.getLiveData<String>("photo_uri")
            ?.observe(lifecycleOwner) { uriString ->
                photoUri = Uri.parse(uriString)
            }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            Surface(
                Modifier.fillMaxWidth(),
                shadowElevation = 8.dp,
                color = White
            ) {
                NagarSevaButton(
                    text = "Continue →",
                    enabled = photoUri != null && submitState !is UiState.Loading,
                    isLoading = submitState is UiState.Loading,
                    onClick = {
                        val photoFilePath = photoUri?.path ?: ""
                        
                        // SAVE TO ROOM DB via ViewModel
                        reportViewModel.submitReport(
                            issueTitle = issueTitle,
                            defectType = issueType,
                            severity = selectedSeverity,  // ← REAL VALUE
                            latitude = latitude.toDouble(),
                            longitude = longitude.toDouble(),
                            locationAccuracy = 8f,
                            address = address,
                            photoPath = photoFilePath,
                            description = description
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
            }
        }
    ) { padding ->

        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            Column(
                Modifier
                    .fillMaxSize()
                    .background(GreySurface)
                    .verticalScroll(rememberScrollState())
            ) {

                // TOP BAR
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
                        CircleBorderBackButton {
                            navController.popBackStack()
                        }
                        Text(
                            "Report Issue",
                            color = GreenPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Box(
                            Modifier
                                .size(36.dp)
                                .background(GreenAccentLight, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "RK",
                                color = White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // STEP INDICATOR
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Step 2 of 2", color = GreyText, fontSize = 13.sp)
                    Row(
                        Modifier.weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        repeat(2) {
                            Box(
                                Modifier
                                    .weight(1f)
                                    .height(4.dp)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(GreenPrimary)
                            )
                        }
                    }
                }

                // DETECTED LOCATION (read-only)
                NagarSevaCard(
                    Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp)
                ) {
                    SectionLabel("Detected Location", Modifier.padding(bottom = 12.dp))
                    Row(
                        Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Filled.LocationOn,
                            null,
                            tint = GreenPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            address,
                            fontSize = 13.sp,
                            color = Charcoal,
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 8.dp)
                        )
                        Surface(
                            shape = RoundedCornerShape(50.dp),
                            color = GreenLight
                        ) {
                            Text(
                                "● GPS FIXED",
                                color = GreenPrimary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }
                    Text(
                        "⊙ Accuracy: ±8 metres",
                        color = GreyText,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(top = 6.dp)
                    )
                }

                // Severity selector UI:
                NagarSevaCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, top = 12.dp, end = 16.dp)
                ) {
                    SectionLabel(
                        "Severity Level",
                        Modifier.padding(bottom = 12.dp)
                    )
                    
                    Text(
                        "How dangerous is this defect?",
                        fontSize = 12.sp,
                        color = Color(0xFF757575),
                        modifier = Modifier.padding(bottom = 10.dp)
                    )
                    
                    // 3 severity options in a row
                    val severityOptions = listOf(
                        Triple("LOW", "🟢 Low", Color(0xFF2E7D32)),
                        Triple("MEDIUM", "🟡 Medium", Color(0xFFFF8F00)),
                        Triple("HIGH", "🔴 High", Color(0xFFB00020))
                    )
                    
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        severityOptions.forEach { (value, label, color) ->
                            val isSelected = selectedSeverity == value
                            
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(
                                        if (isSelected) color.copy(alpha = 0.15f)
                                        else Color(0xFFF5F5F5)
                                    )
                                    .border(
                                        width = if (isSelected) 1.5.dp else 0.dp,
                                        color = if (isSelected) color else Color.Transparent,
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                    .clickable { selectedSeverity = value },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 13.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) color else Color(0xFF757575)
                                )
                            }
                        }
                    }
                }

                // DESCRIPTION
                NagarSevaCard(
                    Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, top = 12.dp)
                ) {
                    SectionLabel("Detailed Description", Modifier.padding(bottom = 12.dp))
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        placeholder = {
                            Text(
                                "Describe the issue and its location in detail...",
                                color = GreyLight,
                                fontSize = 13.sp
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GreenPrimary,
                            unfocusedBorderColor = GreyDivider
                        ),
                        shape = RoundedCornerShape(10.dp),
                        maxLines = 4
                    )
                }

                // ATTACH PHOTO CARD
                NagarSevaCard(
                    Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 16.dp)
                ) {
                    SectionLabel("Attach Photo", Modifier.padding(bottom = 12.dp))

                    Box(
                        Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .drawBehind {
                                drawRoundRect(
                                    color = GreenSecondary,
                                    cornerRadius = CornerRadius(12.dp.toPx()),
                                    style = Stroke(
                                        width = 1.5.dp.toPx(),
                                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 6f), 0f)
                                    )
                                )
                            }
                            .clickable {
                                navController.navigate(Screen.Camera.route)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (photoUri == null) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    Icons.Filled.CameraAlt,
                                    null,
                                    tint = GreyLight,
                                    modifier = Modifier.size(48.dp)
                                )
                                Text(
                                    "Tap to capture or upload photo",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Charcoal,
                                    modifier = Modifier.padding(top = 12.dp)
                                )
                                Text(
                                    "Clear photos help us resolve faster",
                                    fontSize = 12.sp,
                                    color = GreyText,
                                    modifier = Modifier.padding(top = 6.dp)
                                )
                            }
                        } else {
                            Box(Modifier.fillMaxSize()) {
                                AsyncImage(
                                    model = photoUri,
                                    contentDescription = "Captured photo",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                                Surface(
                                    Modifier
                                        .align(Alignment.BottomEnd)
                                        .padding(8.dp),
                                    shape = RoundedCornerShape(50.dp),
                                    color = Color.Black.copy(alpha = 0.6f)
                                ) {
                                    Text(
                                        "📷 Retake",
                                        color = White,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            LoadingOverlay(isLoading = submitState is UiState.Loading)
        }
    }
}
