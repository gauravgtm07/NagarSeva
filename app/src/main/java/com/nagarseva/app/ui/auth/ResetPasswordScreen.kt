package com.nagarseva.app.ui.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.nagarseva.app.ui.components.*
import com.nagarseva.app.ui.theme.*

@Composable
fun ResetPasswordScreen(navController: NavController) {
    Column(
        Modifier
            .fillMaxSize()
            .background(White)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp)
    ) {

        // TOP BAR
        Row(
            Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CircleBorderBackButton {
                navController.popBackStack()
            }
            Text(
                "Reset Password",
                color = GreenPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.size(36.dp))
        }

        Text(
            "Secure Your Account",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Charcoal,
            modifier = Modifier.padding(top = 28.dp)
        )

        Text(
            "Enter your current password and choose a strong new one to keep your account safe.",
            fontSize = 13.sp,
            color = GreyText,
            lineHeight = 20.sp,
            modifier = Modifier.padding(top = 6.dp)
        )

        Spacer(Modifier.height(24.dp))

        // FIELDS
        var currentPwd by remember { mutableStateOf("") }
        var newPwd by remember { mutableStateOf("") }
        var confirmPwd by remember { mutableStateOf("") }
        var strength by remember { mutableStateOf(0) }

        NagarSevaTextField(
            value = currentPwd,
            onValueChange = { currentPwd = it },
            label = "Current Password",
            leadingIcon = Icons.Default.Lock,
            isPassword = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(14.dp))

        NagarSevaTextField(
            value = newPwd,
            onValueChange = {
                newPwd = it
                strength = calculateStrength(it)
            },
            label = "New Password",
            leadingIcon = Icons.Default.Lock,
            isPassword = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        // Strength bar + char count row
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            PasswordStrengthBar(
                strength = strength,
                modifier = Modifier.weight(1f)
            )
            Text(
                "${newPwd.length}/8 characters",
                color = GreyText,
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        Spacer(Modifier.height(14.dp))

        NagarSevaTextField(
            value = confirmPwd,
            onValueChange = { confirmPwd = it },
            label = "Confirm New Password",
            leadingIcon = Icons.Default.Refresh,
            isPassword = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(28.dp))

        NagarSevaButton(
            text = "Reset Password",
            onClick = { /* call viewModel */ },
            modifier = Modifier.fillMaxWidth()
        )

        // Contact Support link
        Row(
            Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                "Can't remember your old password? ",
                color = GreyText,
                fontSize = 12.sp
            )
            Text(
                "Contact Support",
                color = Saffron,
                fontSize = 12.sp,
                modifier = Modifier.clickable { }
            )
        }

        // TWO BADGES ROW
        Row(
            Modifier
                .fillMaxWidth()
                .padding(top = 32.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Data Encrypted — green filled
            Card(
                modifier = Modifier
                    .weight(1f)
                    .height(72.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = GreenLight),
                border = BorderStroke(1.dp, GreenSecondary)
            ) {
                Column(
                    Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Filled.Security, null,
                        tint = GreenPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        "Data Encrypted",
                        color = GreenPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            // Identity Verified — grey outlined
            Card(
                modifier = Modifier
                    .weight(1f)
                    .height(72.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = White),
                border = BorderStroke(1.dp, GreyLight)
            ) {
                Column(
                    Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Filled.VerifiedUser, null,
                        tint = GreyLight,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        "Identity Verified",
                        color = GreyText,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }

        Spacer(Modifier.height(40.dp))
    }
}
