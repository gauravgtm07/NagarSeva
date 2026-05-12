package com.nagarseva.app.ui.report

import android.Manifest
import android.content.Context
import android.location.Geocoder
import android.os.Build
import android.os.Looper
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.location.*
import com.nagarseva.app.ui.components.*
import com.nagarseva.app.ui.navigation.Screen
import com.nagarseva.app.ui.theme.*
import java.util.*

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ReportStep1Screen(navController: NavController) {
    // State
    var issueTitle by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf("POTHOLE") }
    var currentAddress by remember { mutableStateOf("Fetching location...") }
    var currentLat by remember { mutableStateOf(0.0) }
    var currentLng by remember { mutableStateOf(0.0) }
    var gpsFixed by remember { mutableStateOf(false) }
    var accuracy by remember { mutableStateOf(0f) }

    // Location permission
    val context = LocalContext.current
    val permissionState = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)

    LaunchedEffect(permissionState.status) {
        if (permissionState.status.isGranted) {
            // Start location fetch
            fetchLocation(context) { lat, lng, addr, acc ->
                currentLat = lat
                currentLng = lng
                currentAddress = addr
                accuracy = acc
                gpsFixed = true
            }
        } else {
            permissionState.launchPermissionRequest()
        }
    }

    // Layout
    Scaffold(
        bottomBar = {
            // Sticky continue button
            Surface(
                Modifier.fillMaxWidth(),
                shadowElevation = 8.dp,
                color = White
            ) {
                NagarSevaButton(
                    text = "Continue →",
                    onClick = {
                        if (issueTitle.isNotBlank() && gpsFixed) {
                            navController.navigate(
                                Screen.ReportStep2.createRoute(
                                    issueTitle, selectedType,
                                    currentLat.toFloat(),
                                    currentLng.toFloat(),
                                    currentAddress
                                )
                            )
                        }
                    },
                    enabled = issueTitle.isNotBlank() && gpsFixed,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
            }
        }
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .background(GreySurface)
                .verticalScroll(rememberScrollState())
                .padding(padding)
        ) {

            // TOP BAR (white)
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
                        textAlign = TextAlign.Center
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
                Text(
                    "Step 1 of 2",
                    color = GreyText,
                    fontSize = 13.sp
                )
                // Progress segments
                Row(
                    Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(
                        Modifier
                            .weight(1f)
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(GreenPrimary)
                    )
                    Box(
                        Modifier
                            .weight(1f)
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(GreyDivider)
                    )
                }
            }

            // ISSUE TITLE CARD
            NagarSevaCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                SectionLabel(
                    "Issue Title",
                    Modifier.padding(bottom = 8.dp)
                )
                BasicTextField(
                    value = issueTitle,
                    onValueChange = { issueTitle = it },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = TextStyle(
                        fontSize = 15.sp,
                        color = Charcoal
                    ),
                    decorationBox = { innerTextField ->
                        Box {
                            if (issueTitle.isEmpty()) {
                                Text(
                                    "e.g., Large Pothole",
                                    color = GreyLight,
                                    fontSize = 15.sp
                                )
                            }
                            innerTextField()
                        }
                    }
                )
                HorizontalDivider(
                    Modifier.padding(top = 8.dp),
                    color = GreyDivider
                )
            }

            // ISSUE TYPE CARD
            NagarSevaCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 12.dp)
            ) {
                SectionLabel(
                    "Issue Type",
                    Modifier.padding(bottom = 12.dp)
                )

                // Define issue types
                data class IssueType(
                    val id: String,
                    val label: String,
                    val icon: ImageVector
                )

                val types = listOf(
                    IssueType("POTHOLE", "Pothole", Icons.Filled.Warning),
                    IssueType("STREETLIGHT", "Street Light", Icons.Filled.WbIncandescent),
                    IssueType("GARBAGE", "Garbage", Icons.Filled.Delete),
                    IssueType("WATERLEAK", "Water Leak", Icons.Filled.WaterDrop)
                )

                // 2x2 grid
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    types.chunked(2).forEach { rowTypes ->
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            rowTypes.forEach { type ->
                                val isSelected = selectedType == type.id
                                Card(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(90.dp)
                                        .clickable { selectedType = type.id },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (isSelected) GreenMint else White
                                    ),
                                    border = BorderStroke(
                                        if (isSelected) 1.5.dp else 1.dp,
                                        if (isSelected) GreenPrimary else GreyDivider
                                    ),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                                ) {
                                    Box(Modifier.fillMaxSize()) {
                                        Column(
                                            Modifier
                                                .fillMaxSize()
                                                .padding(8.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.Center
                                        ) {
                                            Box(
                                                Modifier
                                                    .size(44.dp)
                                                    .background(
                                                        if (isSelected) GreenLight else GreySurface,
                                                        RoundedCornerShape(50.dp)
                                                    ),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    type.icon,
                                                    null,
                                                    tint = if (isSelected) GreenPrimary else GreyText,
                                                    modifier = Modifier.size(22.dp)
                                                )
                                            }
                                            Text(
                                                type.label,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isSelected) GreenPrimary else GreyText,
                                                modifier = Modifier.padding(top = 8.dp),
                                                textAlign = TextAlign.Center
                                            )
                                        }
                                        // Checkmark badge
                                        if (isSelected) {
                                            Icon(
                                                Icons.Filled.CheckCircle,
                                                null,
                                                tint = GreenPrimary,
                                                modifier = Modifier
                                                    .size(18.dp)
                                                    .align(Alignment.TopEnd)
                                                    .padding(6.dp)
                                            )
                                        }
                                    }
                                }
                            }
                            // Fill empty slot if odd number
                            if (rowTypes.size == 1) {
                                Spacer(Modifier.weight(1f))
                            }
                        }
                    }
                }
            }

            // DETECTED LOCATION CARD
            NagarSevaCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 16.dp)
            ) {
                SectionLabel(
                    "Detected Location",
                    Modifier.padding(bottom = 12.dp)
                )
                Row(
                    Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        Icons.Filled.LocationOn,
                        null,
                        tint = GreenPrimary,
                        modifier = Modifier.size(22.dp)
                    )
                    Column(
                        Modifier
                            .weight(1f)
                            .padding(start = 8.dp)
                    ) {
                        Text(
                            currentAddress,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Charcoal
                        )
                    }
                    // GPS Fixed badge
                    Surface(
                        shape = RoundedCornerShape(50.dp),
                        color = GreenLight
                    ) {
                        Text(
                            if (gpsFixed) "● GPS FIXED" else "⟳ Fetching...",
                            color = if (gpsFixed) GreenPrimary else GreyText,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(
                                horizontal = 10.dp,
                                vertical = 4.dp
                            )
                        )
                    }
                }
                Text(
                    "⊙ Accuracy: ±${accuracy.toInt()} metres",
                    color = GreyText,
                    fontSize = 11.sp,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}

// fetchLocation utility function
@SuppressWarnings("MissingPermission")
fun fetchLocation(
    context: Context,
    onResult: (Double, Double, String, Float) -> Unit
) {
    val fusedClient = LocationServices.getFusedLocationProviderClient(context)
    val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000L).build()
    
    try {
        fusedClient.requestLocationUpdates(
            request,
            object : LocationCallback() {
                override fun onLocationResult(result: LocationResult) {
                    val location = result.lastLocation ?: return
                    val geocoder = Geocoder(context, Locale.getDefault())
                    
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        geocoder.getFromLocation(location.latitude, location.longitude, 1) { addresses ->
                            val address = addresses.firstOrNull()?.getAddressLine(0) ?: "Location detected"
                            onResult(location.latitude, location.longitude, address, location.accuracy)
                        }
                    } else {
                        @Suppress("DEPRECATION")
                        val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                        val address = addresses?.firstOrNull()?.getAddressLine(0) ?: "Location detected"
                        onResult(location.latitude, location.longitude, address, location.accuracy)
                    }
                    
                    fusedClient.removeLocationUpdates(this)
                }
            },
            Looper.getMainLooper()
        )
    } catch (e: SecurityException) {
        // Handle case where permission was revoked
    }
}
