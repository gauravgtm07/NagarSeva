package com.nagarseva.app.ui.confirmation

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.nagarseva.app.ui.components.NagarSevaButton
import com.nagarseva.app.ui.components.NagarSevaOutlinedButton
import com.nagarseva.app.ui.navigation.Screen
import com.nagarseva.app.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun ConfirmationScreen(
    navController: NavController,
    ticketId: String
) {
    val context = LocalContext.current

    // Animate checkmark on entry
    var checkmarkVisible by remember {
        mutableStateOf(false)
    }
    LaunchedEffect(Unit) {
        delay(200)
        checkmarkVisible = true
    }

    Column(
        Modifier
            .fillMaxSize()
            .background(White)
            .padding(horizontal = 24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = CenterHorizontally
    ) {

        // CONFETTI DOTS (decorative)
        Box(
            Modifier
                .fillMaxWidth()
                .height(40.dp)) {
            // Use Canvas to draw small scattered dots
            Canvas(Modifier.fillMaxSize()) {
                val dotPositions = listOf(
                    Offset(50f, 15f), Offset(120f, 25f),
                    Offset(200f, 10f), Offset(280f, 30f),
                    Offset(size.width - 50f, 12f),
                    Offset(size.width - 120f, 28f)
                )
                val colors = listOf(
                    GreenSecondary, Saffron, GreenPrimary,
                    Saffron, GreenSecondary, GreenPrimary
                )
                dotPositions.forEachIndexed { i, pos ->
                    drawCircle(colors[i % colors.size], 5f, pos)
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        // CHECKMARK ANIMATION
        AnimatedVisibility(
            visible = checkmarkVisible,
            enter = scaleIn(
                spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            ) + fadeIn()
        ) {
            Box(
                Modifier.size(96.dp),
                contentAlignment = Alignment.Center
            ) {
                // Outer glow ring
                Box(
                    Modifier
                        .size(96.dp)
                        .background(GreenLight.copy(alpha = 0.4f), CircleShape))
                // Middle ring
                Box(
                    Modifier
                        .size(72.dp)
                        .background(GreenLight.copy(alpha = 0.7f), CircleShape))
                // Inner filled circle
                Box(
                    Modifier
                        .size(60.dp)
                        .background(GreenPrimary, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = null,
                        tint = White,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        Text(
            text = "Report Submitted!",
            fontSize = 26.sp,
            fontWeight = Bold,
            color = GreenPrimary
        )

        Text(
            text = "Thank you for contributing to your neighborhood.",
            fontSize = 14.sp,
            color = GreyText,
            textAlign = TextAlign.Center,
            lineHeight = 22.sp,
            modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 8.dp)
        )

        Spacer(Modifier.height(28.dp))

        // TICKET ID CARD (hero element)
        Card(
            Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = GreenMint),
            border = BorderStroke(2.dp, GreenPrimary),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = CenterHorizontally
            ) {
                Text(
                    text = "TICKET ID",
                    fontSize = 11.sp,
                    color = GreyText,
                    fontWeight = Bold,
                    letterSpacing = 2.sp
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    text = ticketId,
                    fontSize = 28.sp,
                    fontWeight = Bold,
                    color = GreenPrimary,
                    fontFamily = FontFamily.Monospace
                )

                HorizontalDivider(
                    Modifier.padding(vertical = 12.dp),
                    color = GreenSecondary.copy(alpha = 0.3f)
                )

                // Status row
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.AccessTime,
                        contentDescription = null,
                        tint = GreyText,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "Status: Processing",
                        color = GreyText,
                        fontSize = 13.sp
                    )
                }

                Spacer(Modifier.height(10.dp))

                // Copy tap hint
                Row(
                    Modifier.clickable {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("Ticket ID", ticketId)
                        clipboard.setPrimaryClip(clip)
                    },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = null,
                        tint = Saffron,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "Tap to copy",
                        color = Saffron,
                        fontSize = 12.sp
                    )
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        // BACK TO HOME BUTTON
        NagarSevaButton(
            text = "Back to Home",
            leadingIcon = Icons.Filled.Home,
            onClick = {
                navController.navigate(Screen.Home.route) {
                    popUpTo(Screen.Home.route) {
                        inclusive = true
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        // SHARE BUTTON
        NagarSevaOutlinedButton(
            text = "Share Ticket ID",
            leadingIcon = Icons.Filled.Share,
            onClick = {
                val intent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(
                        Intent.EXTRA_TEXT,
                        "I reported a civic issue. Track it with Ticket ID: $ticketId on NagarSeva app.\nYour City. Your Voice. One Tap Away."
                    )
                }
                context.startActivity(Intent.createChooser(intent, "Share Ticket ID"))
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(24.dp))

        Text(
            text = "Your City. Your Voice. One Tap Away.",
            color = Saffron,
            fontSize = 12.sp,
            fontStyle = FontStyle.Italic
        )

        Spacer(Modifier.height(40.dp))
    }
}
