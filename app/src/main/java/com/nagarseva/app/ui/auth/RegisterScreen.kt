package com.nagarseva.app.ui.auth

import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.nagarseva.app.ui.navigation.Screen
import com.nagarseva.app.ui.theme.*
import com.nagarseva.app.util.UiState
import com.nagarseva.app.viewmodel.AuthViewModel

@Composable
fun RegisterScreen(navController: NavController) {
    
    val authViewModel: AuthViewModel = hiltViewModel()
    val registerState by authViewModel.registerState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    
    // Form state
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordStrength by remember { mutableStateOf(0) }
    
    // Error states
    var fullNameError by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf("") }
    var confirmError by remember { mutableStateOf("") }
    
    val passwordsMatch = password == confirmPassword && confirmPassword.isNotEmpty()
    
    // Handle register state changes
    LaunchedEffect(registerState) {
        when (registerState) {
            is UiState.Success -> {
                navController.navigate(Screen.Home.route) {
                    popUpTo(Screen.Login.route) { inclusive = true }
                }
                authViewModel.resetRegisterState()
            }
            is UiState.Error -> {
                snackbarHostState.showSnackbar(
                    message = (registerState as UiState.Error).message,
                    duration = SnackbarDuration.Long
                )
                authViewModel.resetRegisterState()
            }
            else -> {}
        }
    }
    
    fun validateAndRegister() {
        var hasError = false
        fullNameError = ""
        emailError = ""
        passwordError = ""
        confirmError = ""
        
        if (fullName.isBlank()) {
            fullNameError = "Please enter your name"
            hasError = true
        }
        if (email.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailError = "Enter a valid email address"
            hasError = true
        }
        if (password.length < 8) {
            passwordError = "Password must be at least 8 characters"
            hasError = true
        }
        if (confirmPassword.isBlank()) {
            confirmError = "Please confirm your password"
            hasError = true
        }
        if (password != confirmPassword && confirmPassword.isNotEmpty()) {
            confirmError = "Passwords do not match"
            hasError = true
        }
        
        if (!hasError) {
            authViewModel.register(
                fullName = fullName.trim(),
                email = email.trim(),
                password = password,
                confirmPassword = confirmPassword
            )
        }
    }
    
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = White
    ) { paddingValues ->
        
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(White)
                    .verticalScroll(scrollState)
                    .padding(horizontal = 24.dp)
            ) {
                
                // ── TOP BAR ─────────────────────
                Spacer(Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Dashed circle back button
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .drawBehind {
                                drawCircle(
                                    color = GreenSecondary,
                                    radius = size.minDimension / 2,
                                    style = Stroke(
                                        width = 1.5.dp.toPx(),
                                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 4f), 0f)
                                    )
                                )
                            }
                            .clickable {
                                navController.popBackStack()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = GreenPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    
                    Text(
                        text = "Create Account",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Charcoal,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center
                    )
                    
                    // Spacer to balance back button
                    Spacer(Modifier.size(36.dp))
                }
                
                // ── GREEN INFO BANNER ────────────
                Spacer(Modifier.height(20.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = GreenMint),
                    elevation = CardDefaults.cardElevation(0.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(GreenPrimary, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.VerifiedUser,
                                contentDescription = null,
                                tint = White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Text(
                            text = "Join thousands of citizens making their city better.",
                            color = GreenPrimary,
                            fontSize = 13.sp,
                            lineHeight = 20.sp,
                            modifier = Modifier.padding(start = 12.dp)
                        )
                    }
                }
                
                // ── FULL NAME FIELD ──────────────
                Spacer(Modifier.height(20.dp))
                Text(
                    "Full Name",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = Charcoal,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
                OutlinedTextField(
                    value = fullName,
                    onValueChange = { 
                        fullName = it
                        fullNameError = ""
                    },
                    placeholder = {
                        Text("e.g. Alex", color = GreyLight)
                    },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = null,
                            tint = GreenPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    isError = fullNameError.isNotEmpty(),
                    supportingText = if (fullNameError.isNotEmpty()) {
                        { Text(fullNameError, color = ErrorRed, fontSize = 11.sp) }
                    } else null,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GreenPrimary,
                        unfocusedBorderColor = GreyDivider,
                        errorBorderColor = ErrorRed,
                        focusedLabelColor = GreenPrimary,
                        cursorColor = GreenPrimary,
                        focusedContainerColor = White,
                        unfocusedContainerColor = White
                    ),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    )
                )
                
                // ── EMAIL FIELD ──────────────────
                Spacer(Modifier.height(12.dp))
                Text(
                    "Email Address",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = Charcoal,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
                OutlinedTextField(
                    value = email,
                    onValueChange = { 
                        email = it
                        emailError = ""
                    },
                    placeholder = {
                        Text("you@example.com", color = GreyLight)
                    },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Email,
                            contentDescription = null,
                            tint = GreenPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    isError = emailError.isNotEmpty(),
                    supportingText = if (emailError.isNotEmpty()) {
                        { Text(emailError, color = ErrorRed, fontSize = 11.sp) }
                    } else null,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GreenPrimary,
                        unfocusedBorderColor = GreyDivider,
                        errorBorderColor = ErrorRed,
                        focusedContainerColor = White,
                        unfocusedContainerColor = White
                    ),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next
                    )
                )
                
                // ── PASSWORD FIELD ───────────────
                Spacer(Modifier.height(12.dp))
                Text(
                    "Password",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = Charcoal,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
                var passwordVisible by remember { mutableStateOf(false) }
                OutlinedTextField(
                    value = password,
                    onValueChange = { 
                        password = it
                        passwordError = ""
                        passwordStrength = calculateStrength(it)
                    },
                    placeholder = {
                        Text("Minimum 8 characters", color = GreyLight)
                    },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Lock,
                            contentDescription = null,
                            tint = GreenPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = if (passwordVisible) "Hide password" else "Show password",
                                tint = GreyText
                            )
                        }
                    },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    isError = passwordError.isNotEmpty(),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GreenPrimary,
                        unfocusedBorderColor = GreyDivider,
                        errorBorderColor = ErrorRed,
                        focusedContainerColor = White,
                        unfocusedContainerColor = White
                    ),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Next
                    )
                )
                
                // ── PASSWORD STRENGTH BAR ────────
                Spacer(Modifier.height(8.dp))
                
                val strengthColors = listOf(
                    Color(0xFFF44336), // Red
                    Saffron,           // Orange
                    Color(0xFF8BC34A), // Light green
                    GreenSecondary     // Green
                )
                val strengthLabels = listOf("", "Weak", "Fair", "Good", "Strong")
                val strengthLabelColors = listOf(
                    Color.Transparent,
                    Color(0xFFF44336),
                    Saffron,
                    Color(0xFF8BC34A),
                    GreenSecondary
                )
                
                Row(
                    Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        (0..3).forEach { index ->
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(4.dp)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(
                                        if (index < passwordStrength)
                                            strengthColors.getOrElse(index) { GreyDivider }
                                        else GreyDivider
                                    )
                            )
                        }
                    }
                    
                    Spacer(Modifier.width(8.dp))
                    
                    Text(
                        text = strengthLabels.getOrElse(passwordStrength) { "" },
                        color = strengthLabelColors.getOrElse(passwordStrength) { Color.Transparent },
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                
                if (passwordError.isNotEmpty()) {
                    Text(
                        passwordError,
                        color = ErrorRed,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
                
                // ── CONFIRM PASSWORD FIELD ───────
                Spacer(Modifier.height(12.dp))
                Text(
                    "Confirm Password",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = Charcoal,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
                var confirmVisible by remember { mutableStateOf(false) }
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { 
                        confirmPassword = it
                        confirmError = ""
                    },
                    placeholder = {
                        Text("Re-enter your password", color = GreyLight)
                    },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Refresh,
                            contentDescription = null,
                            tint = GreenPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    trailingIcon = {
                        if (passwordsMatch) {
                            Icon(
                                Icons.Filled.CheckCircle,
                                contentDescription = "Passwords match",
                                tint = GreenSecondary,
                                modifier = Modifier.size(22.dp)
                            )
                        } else {
                            IconButton(onClick = { confirmVisible = !confirmVisible }) {
                                Icon(
                                    imageVector = if (confirmVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = null,
                                    tint = GreyText
                                )
                            }
                        }
                    },
                    visualTransformation = if (confirmVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    isError = confirmError.isNotEmpty(),
                    supportingText = if (confirmError.isNotEmpty()) {
                        { Text(confirmError, color = ErrorRed, fontSize = 11.sp) }
                    } else null,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = if (passwordsMatch) GreenSecondary else GreenPrimary,
                        unfocusedBorderColor = if (passwordsMatch) GreenSecondary else GreyDivider,
                        errorBorderColor = ErrorRed,
                        focusedContainerColor = White,
                        unfocusedContainerColor = White
                    ),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(onDone = { validateAndRegister() })
                )
                
                // ── CREATE ACCOUNT BUTTON ────────
                Spacer(Modifier.height(24.dp))
                Button(
                    onClick = { validateAndRegister() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .drawBehind {
                            drawRoundRect(
                                color = GreenSecondary,
                                cornerRadius = CornerRadius(14.dp.toPx()),
                                style = Stroke(
                                    width = 1.5.dp.toPx(),
                                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 5f), 0f)
                                )
                            )
                        },
                    shape = RoundedCornerShape(14.dp),
                    enabled = registerState !is UiState.Loading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GreenPrimary,
                        disabledContainerColor = GreyLight
                    ),
                    elevation = ButtonDefaults.buttonElevation(0.dp)
                ) {
                    if (registerState is UiState.Loading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(22.dp),
                            color = White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(
                            Icons.Default.PersonAdd,
                            contentDescription = null,
                            tint = White,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(10.dp))
                        Text(
                            "Create My Account",
                            color = White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                
                // ── TERMS TEXT ───────────────────
                Spacer(Modifier.height(14.dp))
                
                val termsAnnotated = buildAnnotatedString {
                    withStyle(SpanStyle(color = GreyText, fontSize = 12.sp)) {
                        append("By clicking Create My Account, you agree to our ")
                    }
                    pushStringAnnotation(tag = "TERMS", annotation = "terms")
                    withStyle(
                        SpanStyle(
                            color = Saffron,
                            fontSize = 12.sp,
                            textDecoration = TextDecoration.Underline,
                            fontWeight = FontWeight.Medium
                        )
                    ) {
                        append("Terms of Service")
                    }
                    pop()
                    withStyle(SpanStyle(color = GreyText, fontSize = 12.sp)) {
                        append(" and ")
                    }
                    pushStringAnnotation(tag = "PRIVACY", annotation = "privacy")
                    withStyle(
                        SpanStyle(
                            color = Saffron,
                            fontSize = 12.sp,
                            textDecoration = TextDecoration.Underline,
                            fontWeight = FontWeight.Medium
                        )
                    ) {
                        append("Privacy Policy")
                    }
                    pop()
                    withStyle(SpanStyle(color = GreyText, fontSize = 12.sp)) {
                        append(".")
                    }
                }
                
                ClickableText(
                    text = termsAnnotated,
                    modifier = Modifier.fillMaxWidth(),
                    style = TextStyle(textAlign = TextAlign.Center, lineHeight = 18.sp),
                    onClick = { offset ->
                        termsAnnotated.getStringAnnotations(tag = "TERMS", start = offset, end = offset)
                            .firstOrNull()?.let {
                                Toast.makeText(context, "Terms of Service coming soon", Toast.LENGTH_SHORT).show()
                            }
                        termsAnnotated.getStringAnnotations(tag = "PRIVACY", start = offset, end = offset)
                            .firstOrNull()?.let {
                                Toast.makeText(context, "Privacy Policy coming soon", Toast.LENGTH_SHORT).show()
                            }
                    }
                )
                
                // ── MOTIVATIONAL PHOTO BANNER ────
                Spacer(Modifier.height(24.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(0.dp)
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(GreenPrimary, Color(0xFF2E7D32), Color(0xFF388E3C))
                                    )
                                )
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(Color.Transparent, Black.copy(alpha = 0.6f))
                                    )
                                )
                        )
                        Row(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(bottom = 24.dp),
                            horizontalArrangement = Arrangement.spacedBy((-8).dp)
                        ) {
                            repeat(5) {
                                Icon(
                                    Icons.Filled.Person,
                                    contentDescription = null,
                                    tint = White.copy(0.7f),
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }
                        Text(
                            text = "Building a better tomorrow, together.",
                            color = White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(start = 16.dp, bottom = 12.dp, end = 60.dp),
                            lineHeight = 20.sp
                        )
                        Icon(
                            Icons.Filled.Security,
                            contentDescription = null,
                            tint = White.copy(0.5f),
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(12.dp)
                                .size(28.dp)
                        )
                    }
                }
                
                // ── ALREADY HAVE ACCOUNT ─────────
                Spacer(Modifier.height(20.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Already have an account?",
                        color = GreyText,
                        fontSize = 14.sp
                    )
                    
                    Surface(
                        modifier = Modifier.clickable {
                            navController.navigate(Screen.Login.route) {
                                popUpTo(Screen.Register.route) { inclusive = true }
                            }
                        },
                        shape = RoundedCornerShape(6.dp),
                        border = BorderStroke(1.dp, GreenPrimary),
                        color = Color.Transparent
                    ) {
                        Text(
                            "Login",
                            color = GreenPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                        )
                    }
                }
                
                // ── FOOTER TEXT ──────────────────
                Spacer(Modifier.height(16.dp))
                Text(
                    "NAGARSEVA CIVIC PLATFORM",
                    color = GreyLight,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 2.sp,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
                
                Spacer(Modifier.height(32.dp))
            }
            
            // Loading overlay
            if (registerState is UiState.Loading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Black.copy(0.3f)),
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
