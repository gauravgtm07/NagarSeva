package com.nagarseva.app.ui.reports

import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.nagarseva.app.data.local.entity.ReportEntity
import com.nagarseva.app.data.local.entity.getDisplayName
import com.nagarseva.app.data.local.entity.getFormattedDate
import com.nagarseva.app.ui.components.*
import com.nagarseva.app.ui.theme.*
import com.nagarseva.app.util.PdfGenerator
import com.nagarseva.app.util.UiState
import com.nagarseva.app.viewmodel.ReportViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

@Composable
fun ReportDetailScreen(
    navController: NavController,
    reportId: Long
) {
    val reportViewModel: ReportViewModel = hiltViewModel()
    val reportDetailState by reportViewModel.reportDetailState.collectAsStateWithLifecycle()
    val deleteState by reportViewModel.deleteState.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Load report on enter:
    LaunchedEffect(reportId) {
        reportViewModel.loadReportDetail(reportId)
    }

    // Handle delete:
    LaunchedEffect(deleteState) {
        when (deleteState) {
            is UiState.Success -> {
                navController.popBackStack()
                reportViewModel.resetDeleteState()
            }
            is UiState.Error -> {
                snackbarHostState.showSnackbar((deleteState as UiState.Error).message)
                reportViewModel.resetDeleteState()
            }
            else -> {}
        }
    }

    // Derived alpha for collapsing effect
    val heroHeight = 220.dp
    val heroHeightPx = with(androidx.compose.ui.platform.LocalDensity.current) { heroHeight.toPx() }
    val appBarAlpha = (scrollState.value / heroHeightPx).coerceIn(0f, 1f)

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        val layoutDirection = LocalLayoutDirection.current
        
        Box(modifier = Modifier
            .fillMaxSize()
            .background(White)
            .padding(
                top = paddingValues.calculateTopPadding(),
                bottom = paddingValues.calculateBottomPadding(),
                start = paddingValues.calculateStartPadding(layoutDirection),
                end = paddingValues.calculateEndPadding(layoutDirection)
            )
        ) {
            
            when (val state = reportDetailState) {
                is UiState.Loading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = GreenPrimary)
                    }
                }
                is UiState.Success -> {
                    val report = state.data
                    ReportDetailContent(
                        report = report,
                        scrollState = scrollState,
                        heroHeight = heroHeight,
                        reportViewModel = reportViewModel,
                        onBack = { navController.popBackStack() }
                    )
                }
                is UiState.Error -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = CenterHorizontally) {
                            Icon(
                                Icons.Filled.ErrorOutline, null,
                                tint = ErrorRed,
                                modifier = Modifier.size(48.dp)
                            )
                            Text(
                                state.message,
                                color = GreyText,
                                modifier = Modifier.padding(top = 16.dp, start = 32.dp, end = 32.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
                else -> {}
            }

            // COLLAPSIBLE APP BAR
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .alpha(appBarAlpha),
                color = GreenPrimary,
                shadowElevation = 4.dp
            ) {
                Row(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = White)
                    }
                    Text(
                        text = "Report Details",
                        color = White,
                        fontSize = 18.sp,
                        fontWeight = Bold,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center
                    )
                    IconButton(onClick = { }) {
                        Icon(Icons.Default.Notifications, null, tint = White)
                    }
                }
            }
            
            // FLOATING BACK BUTTON (When header is visible)
            if (appBarAlpha < 0.5f) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp, start = 16.dp, end = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color.Black.copy(alpha = 0.4f), CircleShape)
                            .clickable { navController.popBackStack() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = White, modifier = Modifier.size(20.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun ReportDetailContent(
    report: ReportEntity,
    scrollState: ScrollState,
    heroHeight: Dp,
    reportViewModel: ReportViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var showDeleteDialog by remember { mutableStateOf(false) }
    var isGeneratingPdf by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Report") },
            text = { 
                Text("Are you sure you want to delete this report? This cannot be undone.") 
            },
            confirmButton = {
                TextButton(onClick = {
                    reportViewModel.deleteReport(report.id)
                    showDeleteDialog = false
                }) {
                    Text("Delete", color = ErrorRed)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        // 1. HERO PHOTO SECTION
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(heroHeight)
        ) {
            AsyncImage(
                model = File(report.photoPath),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                error = rememberVectorPainter(Icons.Default.BrokenImage)
            )
            
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f)),
                            startY = 300f
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(20.dp)
            ) {
                Text(
                    text = "${report.defectType.getDisplayName()} Report",
                    color = White,
                    fontSize = 22.sp,
                    fontWeight = Bold
                )
                Text(
                    text = "ID: #${report.ticketId}",
                    color = GreenSecondary,
                    fontSize = 14.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // 2. CONTENT AREA
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            color = White,
            shadowElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Row(
                    Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    StatusBadge(report.status)
                    Text(
                        report.getFormattedDate(),
                        fontSize = 12.sp,
                        color = GreyText
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))
                
                StatusStepperStrip(status = report.status)
                
                Spacer(modifier = Modifier.height(28.dp))

                DetailRow(Icons.Default.LocationOn, "Location", report.address)
                DetailRow(Icons.Default.PinDrop, "Coordinates", "${report.latitude}°N, ${report.longitude}°E")
                DetailRow(Icons.Default.Event, "Date Reported", report.getFormattedDate())
                
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        SectionLabel("DEFECT TYPE")
                        Text(report.defectType.getDisplayName(), fontSize = 16.sp, fontWeight = Bold, color = Charcoal)
                    }
                    Surface(
                        color = ErrorLight,
                        shape = RoundedCornerShape(50.dp)
                    ) {
                        Text(
                            report.severity,
                            color = ErrorRed,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                        )
                    }
                }

                SectionLabel("DESCRIPTION", Modifier.padding(top = 12.dp))
                Text(
                    text = report.description.ifBlank { "No description provided." },
                    style = MaterialTheme.typography.bodyLarge,
                    color = Charcoal,
                    lineHeight = 22.sp,
                    modifier = Modifier.padding(top = 8.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                SectionLabel("INCIDENT MAP")
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                        .height(120.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Charcoal)
                ) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                        Icon(Icons.Default.LocationOn, null, tint = GreenPrimary, modifier = Modifier.size(32.dp))
                        Text(
                            "Open Maps",
                            color = GreenSecondary,
                            fontSize = 13.sp,
                            fontWeight = Bold,
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(12.dp)
                                .clickable {
                                    val gmmIntentUri = "geo:${report.latitude},${report.longitude}?q=${report.address}".toUri()
                                    val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                                    context.startActivity(mapIntent)
                                }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    NagarSevaOutlinedButton(
                        text = "Share",
                        leadingIcon = Icons.Default.Share,
                        onClick = {
                            val sendIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, "Check this civic issue: ${report.issueTitle} at ${report.address}")
                                type = "text/plain"
                            }
                            context.startActivity(Intent.createChooser(sendIntent, null))
                        },
                        modifier = Modifier.weight(1f)
                    )
                    
                    OutlinedButton(
                        onClick = {
                            isGeneratingPdf = true
                            // Run on IO thread
                            CoroutineScope(Dispatchers.IO).launch {
                                val pdfFile = PdfGenerator.generateReportPdf(context, report)
                                withContext(Dispatchers.Main) {
                                    isGeneratingPdf = false
                                    if (pdfFile != null) {
                                        PdfGenerator.sharePdf(context, pdfFile)
                                    } else {
                                        Toast.makeText(
                                            context,
                                            "Failed to generate PDF. Please try again.",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            }
                        },
                        border = BorderStroke(1.5.dp, Color(0xFF1B5E20)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f).height(48.dp)
                    ) {
                        if (isGeneratingPdf) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                color = Color(0xFF1B5E20),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                Icons.Default.PictureAsPdf,
                                contentDescription = null,
                                tint = Color(0xFF1B5E20),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                "PDF Report",
                                color = Color(0xFF1B5E20),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    "Delete Report",
                    color = ErrorRed,
                    fontWeight = Bold,
                    fontSize = 14.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showDeleteDialog = true }
                        .padding(vertical = 12.dp),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

@Composable
fun DetailRow(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(icon, null, tint = GreenPrimary, modifier = Modifier.size(20.dp))
        Column(modifier = Modifier.padding(start = 12.dp)) {
            SectionLabel(label)
            Text(value, fontSize = 15.sp, color = Charcoal, fontWeight = FontWeight.Medium, modifier = Modifier.padding(top = 2.dp))
        }
    }
}

@Composable
fun StatusStepperStrip(status: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = GreenPrimary.copy(alpha = 0.05f)),
        border = BorderStroke(1.dp, GreenPrimary.copy(alpha = 0.2f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatusStepItem("Submitted", status == "SUBMITTED" || status == "IN_REVIEW" || status == "RESOLVED", status == "SUBMITTED")
            StatusConnector(status == "IN_REVIEW" || status == "RESOLVED")
            StatusStepItem("In Review", status == "IN_REVIEW" || status == "RESOLVED", status == "IN_REVIEW")
            StatusConnector(status == "RESOLVED")
            StatusStepItem("Resolved", status == "RESOLVED", status == "RESOLVED")
        }
    }
}

@Composable
fun StatusStepItem(label: String, isDone: Boolean, isActive: Boolean) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(contentAlignment = Alignment.Center) {
            if (isActive) {
                val infiniteTransition = rememberInfiniteTransition(label = "")
                val scale by infiniteTransition.animateFloat(
                    initialValue = 1f, targetValue = 1.4f,
                    animationSpec = infiniteRepeatable(tween(800), RepeatMode.Reverse), label = ""
                )
                Box(Modifier.size(24.dp * scale).background(Saffron.copy(alpha = 0.3f), CircleShape))
            }
            Box(
                Modifier
                    .size(24.dp)
                    .background(if (isDone) GreenPrimary else if (isActive) Saffron else GreyDivider, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    if (isDone) Icons.Default.Check else Icons.Default.Circle,
                    null, tint = White, modifier = Modifier.size(14.dp)
                )
            }
        }
        Text(label, fontSize = 10.sp, fontWeight = Bold, color = if (isActive) GreenPrimary else Charcoal, modifier = Modifier.padding(top = 4.dp))
    }
}

@Composable
fun RowScope.StatusConnector(isDone: Boolean) {
    Box(
        modifier = Modifier
            .weight(1f)
            .height(2.dp)
            .padding(horizontal = 4.dp)
            .background(if (isDone) GreenPrimary else GreyDivider)
    )
}
