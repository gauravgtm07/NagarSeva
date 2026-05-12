package com.nagarseva.app.ui.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.nagarseva.app.ui.navigation.Screen
import com.nagarseva.app.ui.theme.GreenPrimary
import com.nagarseva.app.ui.theme.GreenSecondary
import com.nagarseva.app.ui.theme.Saffron
import com.nagarseva.app.ui.theme.White
import com.nagarseva.app.util.SessionManager
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController) {
    val context = LocalContext.current
    var visible by remember { mutableStateOf(false) }
    val sessionManager = remember { SessionManager(context) }

    LaunchedEffect(Unit) {
        visible = true
        delay(2000) // show splash for 2 seconds
        
        // Check REAL session from DataStore
        val isLoggedIn = sessionManager.isLoggedIn()
        
        if (isLoggedIn) {
            // User was previously logged in
            navController.navigate(Screen.Home.route) {
                popUpTo(Screen.Splash.route) { 
                    inclusive = true 
                }
            }
        } else {
            // No session — go to Login
            navController.navigate(Screen.Login.route) {
                popUpTo(Screen.Splash.route) { 
                    inclusive = true 
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(GreenPrimary),
        contentAlignment = Alignment.Center
    ) {
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(tween(600))
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.weight(1f))

                // APP ICON
                Box(
                    modifier = Modifier.size(120.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .background(White.copy(alpha = 0.15f))
                    )
                    Icon(
                        imageVector = Icons.Filled.Apartment,
                        contentDescription = "App Icon",
                        tint = White,
                        modifier = Modifier.size(64.dp)
                    )
                }

                // APP NAME
                Text(
                    text = "NagarSeva",
                    color = White,
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp,
                    modifier = Modifier.padding(top = 20.dp)
                )

                // HINDI SUBTITLE
                Text(
                    text = "नगर सेवा",
                    color = White.copy(alpha = 0.75f),
                    fontSize = 14.sp,
                    modifier = Modifier.padding(top = 6.dp)
                )

                // TAGLINE
                Text(
                    text = "Your City. Your Voice. One Tap Away.",
                    color = Saffron,
                    fontSize = 13.sp,
                    fontStyle = FontStyle.Italic,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(top = 14.dp)
                        .padding(horizontal = 40.dp)
                )

                Spacer(modifier = Modifier.weight(1f))

                // BOTTOM SECTION
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(bottom = 48.dp)
                ) {
                    LinearProgressIndicator(
                        modifier = Modifier
                            .width(180.dp)
                            .height(3.dp)
                            .clip(RoundedCornerShape(2.dp)),
                        color = GreenSecondary,
                        trackColor = White.copy(alpha = 0.3f)
                    )
                    Text(
                        text = "Loading...",
                        color = White.copy(alpha = 0.55f),
                        fontSize = 11.sp,
                        modifier = Modifier.padding(top = 10.dp)
                    )
                }
            }
        }
    }
}
