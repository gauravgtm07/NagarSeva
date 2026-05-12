package com.nagarseva.app.ui.services

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.nagarseva.app.ui.components.NagarSevaCard
import com.nagarseva.app.ui.components.SectionLabel
import com.nagarseva.app.ui.navigation.Screen
import com.nagarseva.app.ui.theme.*

// STEP 1: Create a ServiceItem data class
data class ServiceItem(
    val id: String,
    val name: String,
    val description: String,
    val icon: ImageVector,
    val backgroundColor: Color,
    val iconColor: Color,
    val isAvailable: Boolean = false,
    val isDashed: Boolean = false
)

@Composable
fun ServicesScreen(navController: NavHostController) {
    // STEP 3: Add search state
    var searchQuery by remember { mutableStateOf("") }

    // STEP 2: Create services data list 
    val allServices = remember {
        listOf(
            ServiceItem(
                id = "report_issue",
                name = "Report Issue",
                description = "Report potholes, lights, etc.",
                icon = Icons.Filled.Warning,
                backgroundColor = Color(0xFFFFF3E0),
                iconColor = Color(0xFFFF8F00),
                isAvailable = true,
                isDashed = true
            ),
            ServiceItem(
                id = "tax_payment",
                name = "Tax Payment",
                description = "Pay property & water tax",
                icon = Icons.Filled.Receipt,
                backgroundColor = Color(0xFFE8F5E9),
                iconColor = Color(0xFF1B5E20),
                isAvailable = false
            ),
            ServiceItem(
                id = "birth_certificate",
                name = "Birth Certificate",
                description = "Apply for new or copy",
                icon = Icons.Filled.ChildCare,
                backgroundColor = Color(0xFFE3F2FD),
                iconColor = Color(0xFF1565C0),
                isAvailable = false
            ),
            ServiceItem(
                id = "water_connection",
                name = "Water Connection",
                description = "New connection & billing",
                icon = Icons.Filled.WaterDrop,
                backgroundColor = Color(0xFFE3F2FD),
                iconColor = Color(0xFF1565C0),
                isAvailable = false
            ),
            ServiceItem(
                id = "trade_license",
                name = "Trade License",
                description = "Business permits & renewals",
                icon = Icons.Filled.Store,
                backgroundColor = Color(0xFFF3E5F5),
                iconColor = Color(0xFF6A1B9A),
                isAvailable = false
            ),
            ServiceItem(
                id = "grievance",
                name = "Grievance Redressal",
                description = "Submit complaints & track",
                icon = Icons.Filled.Feedback,
                backgroundColor = Color(0xFFFFEBEE),
                iconColor = Color(0xFFB00020),
                isAvailable = false
            ),
            ServiceItem(
                id = "building_plan",
                name = "Building Plan",
                description = "Approvals & NOC certificates",
                icon = Icons.Filled.Apartment,
                backgroundColor = Color(0xFFE8F5E9),
                iconColor = Color(0xFF1B5E20),
                isAvailable = false
            ),
            ServiceItem(
                id = "waste_management",
                name = "Waste Management",
                description = "Garbage pickup & disposal",
                icon = Icons.Filled.Delete,
                backgroundColor = Color(0xFFE8F5E9),
                iconColor = Color(0xFF2E7D32),
                isAvailable = false
            ),
            ServiceItem(
                id = "property_pass",
                name = "e-Property Pass",
                description = "Instant digital ownership card",
                icon = Icons.Filled.Badge,
                backgroundColor = Color(0xFFE8F5E9),
                iconColor = Color(0xFF1B5E20),
                isAvailable = false
            )
        )
    }

    // STEP 3: Filtering logic
    val filteredServices = remember(searchQuery) {
        if (searchQuery.isBlank()) {
            allServices
        } else {
            allServices.filter { service ->
                service.name.contains(searchQuery, ignoreCase = true) ||
                service.description.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(White)
    ) {
        // TOP APP BAR
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = White,
            shadowElevation = 0.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Charcoal
                    )
                }
                Text(
                    text = "Services",
                    style = MaterialTheme.typography.titleLarge,
                    color = Charcoal,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.size(48.dp))
            }
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // STEP 4: functional search bar
            item(span = { GridItemSpan(2) }) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = {
                        Text(
                            "Search municipal services...",
                            color = Color(0xFFBDBDBD),
                            fontSize = 14.sp
                        )
                    },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = null,
                            tint = Color(0xFFBDBDBD)
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(
                                onClick = { searchQuery = "" }
                            ) {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = "Clear",
                                    tint = Color(0xFFBDBDBD)
                                )
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF1B5E20),
                        unfocusedBorderColor = Color(0xFFEEEEEE),
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color(0xFFF5F5F5)
                    ),
                    singleLine = true
                )
            }

            // STEP 5: Show empty state when no results
            if (filteredServices.isEmpty()) {
                item(span = { GridItemSpan(2) }) {
                    Box(
                        Modifier.fillMaxWidth().padding(60.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                Icons.Filled.SearchOff,
                                contentDescription = null,
                                tint = Color(0xFFBDBDBD),
                                modifier = Modifier.size(56.dp)
                            )
                            Text(
                                "No services found for",
                                color = Color(0xFF757575),
                                fontSize = 14.sp,
                                modifier = Modifier.padding(top = 16.dp)
                            )
                            Text(
                                "\"$searchQuery\"",
                                color = Color(0xFF1B5E20),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                            TextButton(
                                onClick = { searchQuery = "" }
                            ) {
                                Text(
                                    "Clear search",
                                    color = Color(0xFF1B5E20)
                                )
                            }
                        }
                    }
                }
            } else {
                // BANNER CARD (Only show when not searching)
                if (searchQuery.isEmpty()) {
                    item(span = { GridItemSpan(2) }) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(140.dp)
                                .padding(bottom = 8.dp),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Box {
                                AsyncImage(
                                    model = "https://images.unsplash.com/photo-1514565131-fce0801e5785",
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(
                                            Brush.horizontalGradient(
                                                colors = listOf(Black.copy(alpha = 0.8f), Color.Transparent),
                                                startX = 0f,
                                                endX = 600f
                                            )
                                        )
                                )
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(16.dp),
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        "Civic Services",
                                        color = GreenSecondary,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        "How can we help today?",
                                        color = White,
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        modifier = Modifier.padding(top = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // SERVICE GRID ITEMS
                items(filteredServices) { service ->
                    val interactionModifier = if (service.isDashed) {
                        Modifier.drawBehind {
                            val stroke = Stroke(
                                width = 1.5.dp.toPx(),
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                            )
                            drawRoundRect(
                                color = Saffron,
                                style = stroke,
                                cornerRadius = CornerRadius(16.dp.toPx())
                            )
                        }
                    } else Modifier

                    NagarSevaCard(
                        modifier = Modifier
                            .height(110.dp)
                            .then(interactionModifier),
                        onClick = {
                            if (service.id == "report_issue") {
                                navController.navigate(Screen.ReportStep1.route)
                            }
                        }
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(service.backgroundColor, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = service.icon,
                                    contentDescription = service.name,
                                    tint = service.iconColor,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = service.name,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Charcoal,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                // FREQUENTLY USED SECTION (Only show when not searching)
                if (searchQuery.isEmpty()) {
                    item(span = { GridItemSpan(2) }) {
                        SectionLabel("FREQUENTLY USED", Modifier.padding(top = 16.dp, bottom = 8.dp))
                    }

                    item(span = { GridItemSpan(2) }) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(80.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = GreenLight),
                            border = BorderStroke(1.dp, GreenSecondary.copy(alpha = 0.3f))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .background(GreenPrimary, RoundedCornerShape(10.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.CreditCard, null, tint = White, modifier = Modifier.size(24.dp))
                                }
                                Column(
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(horizontal = 16.dp)
                                ) {
                                    Text(
                                        "e-Property Pass",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = GreenPrimary,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        "Instant digital ownership card",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = GreyText
                                    )
                                }
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = null,
                                    tint = GreenPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }

            item(span = { GridItemSpan(2) }) {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}
