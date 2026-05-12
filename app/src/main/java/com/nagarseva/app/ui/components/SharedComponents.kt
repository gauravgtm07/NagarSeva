package com.nagarseva.app.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.nagarseva.app.ui.theme.*

// COMPONENT 1 — NagarSevaButton
@Composable
fun NagarSevaButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    leadingIcon: ImageVector? = null
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        enabled = enabled && !isLoading,
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = GreenPrimary,
            contentColor = White,
            disabledContainerColor = GreyLight,
            disabledContentColor = White
        )
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = White,
                strokeWidth = 2.dp
            )
        } else {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                if (leadingIcon != null) {
                    Icon(
                        imageVector = leadingIcon,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(
                    text = text,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

// COMPONENT 2 — NagarSevaOutlinedButton
@Composable
fun NagarSevaOutlinedButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    leadingIcon: ImageVector? = null,
    containerColor: Color = Color.Transparent,
    contentColor: Color = GreenPrimary,
    borderColor: Color = GreenPrimary
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(2.dp, borderColor),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = containerColor,
            contentColor = contentColor
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (leadingIcon != null) {
                Icon(
                    imageVector = leadingIcon,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = text,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

// COMPONENT 3 — NagarSevaTextField
@Composable
fun NagarSevaTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    leadingIcon: ImageVector? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    isPassword: Boolean = false,
    isError: Boolean = false,
    errorMessage: String = "",
    keyboardType: KeyboardType = KeyboardType.Text,
    maxLines: Int = 1,
    enabled: Boolean = true
) {
    var passwordVisible by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text(text = label) },
            leadingIcon = leadingIcon?.let {
                { Icon(imageVector = it, contentDescription = null, tint = GreyText) }
            },
            trailingIcon = if (isPassword) {
                {
                    val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(imageVector = image, contentDescription = null, tint = GreyText)
                    }
                }
            } else {
                trailingIcon
            },
            isError = isError,
            enabled = enabled,
            visualTransformation = if (isPassword && !passwordVisible) PasswordVisualTransformation() else VisualTransformation.None,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            maxLines = maxLines,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = GreenPrimary,
                unfocusedBorderColor = GreyLight,
                errorBorderColor = ErrorRed,
                focusedLabelColor = GreenPrimary,
                cursorColor = GreenPrimary,
                disabledBorderColor = GreyDivider,
                disabledLabelColor = GreyLight,
                disabledTextColor = Charcoal.copy(alpha = 0.6f)
            )
        )
        if (isError && errorMessage.isNotEmpty()) {
            Text(
                text = errorMessage,
                color = ErrorRed,
                style = MaterialTheme.typography.bodySmall,
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 4.dp, top = 4.dp)
            )
        }
    }
}

// COMPONENT 4 — StatusBadge
@Composable
fun StatusBadge(status: String) {
    val (bgColor, textColor, iconText) = when (status.uppercase()) {
        "IN REVIEW", "IN_REVIEW" -> Triple(StatusReviewBg, StatusReview, "● IN REVIEW")
        "SUBMITTED" -> Triple(StatusSubmittedBg, StatusSubmitted, "● SUBMITTED")
        "RESOLVED" -> Triple(StatusResolvedBg, StatusResolved, "✓ RESOLVED")
        else -> Triple(GreySurface, GreyText, status.uppercase())
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(50.dp),
        modifier = Modifier.wrapContentSize()
    ) {
        Text(
            text = iconText,
            color = textColor,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
        )
    }
}

// COMPONENT 5 — StatPill
@Composable
fun StatPill(
    value: String,
    label: String,
    dotColor: Color,
    backgroundColor: Color
) {
    Row(
        modifier = Modifier
            .background(backgroundColor, RoundedCornerShape(50.dp))
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .background(dotColor, CircleShape)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = buildAnnotatedString {
                withStyle(style = SpanStyle(color = Charcoal, fontWeight = FontWeight.Bold, fontSize = 14.sp)) {
                    append(value)
                }
                append("  ")
                withStyle(style = SpanStyle(color = GreyText, fontWeight = FontWeight.Normal, fontSize = 13.sp)) {
                    append(label)
                }
            }
        )
    }
}

// COMPONENT 7 — CircleBorderBackButton
@Composable
fun CircleBorderBackButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(36.dp)
            .border(1.5.dp, GreenSecondary, CircleShape)
            .clip(CircleShape)
            .background(White)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Back",
            tint = GreenPrimary,
            modifier = Modifier.size(18.dp)
        )
    }
}

// COMPONENT 6 — GreenTopBar
@Composable
fun GreenTopBar(
    title: String,
    onBackClick: (() -> Unit)? = null,
    onBellClick: (() -> Unit)? = null,
    showBell: Boolean = false,
    showAvatar: Boolean = false,
    avatarInitials: String = "RK"
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .background(GreenPrimary)
            .padding(horizontal = 16.dp)
    ) {
        if (onBackClick != null) {
            Box(modifier = Modifier.align(Alignment.CenterStart)) {
                CircleBorderBackButton(onClick = onBackClick)
            }
        }

        Text(
            text = title,
            color = White,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.Center)
        )

        Row(
            modifier = Modifier.align(Alignment.CenterEnd),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (showBell) {
                Box(
                    modifier = Modifier
                        .padding(end = if (showAvatar) 12.dp else 0.dp)
                        .clickable { onBellClick?.invoke() }
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Notifications",
                        tint = White,
                        modifier = Modifier.size(22.dp)
                    )
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .background(Saffron, CircleShape)
                            .align(Alignment.TopEnd)
                    )
                }
            }
            if (showAvatar) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(GreenAccentLight, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = avatarInitials,
                        color = White,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}

// COMPONENT 8 — LoadingOverlay
@Composable
fun LoadingOverlay(isLoading: Boolean) {
    AnimatedVisibility(
        visible = isLoading,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(OverlayDark),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                color = GreenSecondary,
                strokeWidth = 3.dp,
                modifier = Modifier.size(48.dp)
            )
        }
    }
}

// COMPONENT 9 — SectionLabel
@Composable
fun SectionLabel(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text.uppercase(),
        color = GreyText,
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = 0.08.em,
        fontSize = 12.sp,
        modifier = modifier
    )
}

// COMPONENT 10 — NagarSevaCard
@Composable
fun NagarSevaCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = if (onClick != null) modifier.clickable { onClick() } else modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            content = content
        )
    }
}

// Helper composables for strength bar
@Composable
fun PasswordStrengthBar(strength: Int, modifier: Modifier = Modifier) {
    val segments = listOf(
        if (strength >= 1) ErrorRed else GreyDivider,
        if (strength >= 2) Saffron else GreyDivider,
        if (strength >= 3) Color(0xFF8BC34A) else GreyDivider,
        if (strength >= 4) GreenSecondary else GreyDivider
    )
    val labels = listOf("", "Weak", "Fair", "Good", "Strong")
    val labelColors = listOf(Transparent, ErrorRed, Saffron, Color(0xFF8BC34A), GreenSecondary)

    Column(modifier = modifier) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            segments.forEach { color ->
                Box(Modifier.weight(1f).height(4.dp).clip(RoundedCornerShape(2.dp)).background(color))
            }
        }
        if (strength > 0) {
            Text(labels[strength], color = labelColors[strength], fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))
        }
    }
}

fun calculateStrength(password: String): Int {
    if (password.isEmpty()) return 0
    if (password.length < 6) return 1
    if (password.length < 8) return 2
    val hasNumber = password.any { it.isDigit() }
    val hasSpecial = password.any { !it.isLetterOrDigit() }
    return if (hasNumber && hasSpecial) 4 else 3
}
