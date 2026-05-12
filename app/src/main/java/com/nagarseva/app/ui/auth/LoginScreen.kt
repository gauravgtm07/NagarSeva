package com.nagarseva.app.ui.auth

import android.util.Patterns
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.nagarseva.app.ui.components.LoadingOverlay
import com.nagarseva.app.ui.components.NagarSevaButton
import com.nagarseva.app.ui.components.NagarSevaOutlinedButton
import com.nagarseva.app.ui.components.NagarSevaTextField
import com.nagarseva.app.ui.navigation.Screen
import com.nagarseva.app.ui.theme.*
import com.nagarseva.app.viewmodel.AuthViewModel
import com.nagarseva.app.util.UiState

@Composable
fun LoginScreen(navController: NavController) {
    val authViewModel: AuthViewModel = hiltViewModel()
    val loginState by authViewModel.loginState.collectAsStateWithLifecycle()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf("") }

    val snackbarHostState = remember { SnackbarHostState() }

    // Handle state changes
    LaunchedEffect(loginState) {
        when (loginState) {
            is UiState.Success -> {
                navController.navigate(Screen.Home.route) {
                    popUpTo(Screen.Login.route) { inclusive = true }
                }
                authViewModel.resetLoginState()
            }
            is UiState.Error -> {
                snackbarHostState.showSnackbar(
                    message = (loginState as UiState.Error).message,
                    duration = SnackbarDuration.Long
                )
                authViewModel.resetLoginState()
            }
            else -> {}
        }
    }

    fun onLoginClick() {
        emailError = ""
        passwordError = ""
        var hasError = false

        if (email.isBlank() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailError = "Invalid email address"
            hasError = true
        }
        if (password.isBlank()) {
            passwordError = "Please enter your password"
            hasError = true
        }

        if (!hasError) {
            authViewModel.login(email, password)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                // ── GREEN HEADER ZONE ──────────────────
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                ) {
                    Box(Modifier.fillMaxSize().background(GreenPrimary))

                    Column(
                        Modifier.align(Alignment.Center).padding(horizontal = 40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Apartment,
                            contentDescription = null,
                            tint = White,
                            modifier = Modifier.size(52.dp)
                        )
                        Text(
                            text = "NagarSeva",
                            color = White,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 12.dp)
                        )
                        Text(
                            text = "Your City. Your Voice. One Tap Away.",
                            color = Saffron,
                            fontSize = 13.sp,
                            fontStyle = FontStyle.Italic,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }

                    Canvas(
                        modifier = Modifier.fillMaxWidth().height(40.dp).align(Alignment.BottomCenter)
                    ) {
                        val path = Path()
                        path.moveTo(0f, size.height)
                        path.cubicTo(size.width * 0.25f, 0f, size.width * 0.75f, 0f, size.width, size.height)
                        path.lineTo(size.width, size.height)
                        path.lineTo(0f, size.height)
                        path.close()
                        drawPath(path, color = White)
                    }
                }

                // ── FORM AREA ─────────────────────────
                Column(
                    modifier = Modifier.fillMaxWidth().background(White).padding(horizontal = 24.dp)
                ) {
                    Text(
                        text = "Welcome Back",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Charcoal,
                        modifier = Modifier.padding(top = 28.dp)
                    )
                    Text(
                        text = "Sign in to continue",
                        fontSize = 13.sp,
                        color = GreyText,
                        modifier = Modifier.padding(top = 4.dp)
                    )

                    Spacer(Modifier.height(20.dp))

                    NagarSevaTextField(
                        value = email,
                        onValueChange = {
                            email = it
                            emailError = ""
                        },
                        label = "Email Address",
                        leadingIcon = Icons.Default.Email,
                        isError = emailError.isNotEmpty(),
                        errorMessage = emailError,
                        keyboardType = KeyboardType.Email,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(14.dp))

                    NagarSevaTextField(
                        value = password,
                        onValueChange = {
                            password = it
                            passwordError = ""
                        },
                        label = "Password",
                        leadingIcon = Icons.Default.Lock,
                        isPassword = true,
                        isError = passwordError.isNotEmpty(),
                        errorMessage = passwordError,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Box(Modifier.fillMaxWidth()) {
                        Text(
                            text = "Forgot Password?",
                            color = Saffron,
                            fontSize = 12.sp,
                            modifier = Modifier.align(Alignment.CenterEnd).padding(top = 8.dp).clickable {
                                navController.navigate(Screen.ResetPassword.route)
                            }
                        )
                    }

                    Spacer(Modifier.height(24.dp))

                    NagarSevaButton(
                        text = "Login",
                        onClick = { onLoginClick() },
                        isLoading = loginState is UiState.Loading,
                        enabled = loginState !is UiState.Loading,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        Modifier.fillMaxWidth().padding(vertical = 20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        HorizontalDivider(Modifier.weight(1f), color = GreyDivider)
                        Text(
                            text = " OR ",
                            color = GreyText,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(horizontal = 12.dp)
                        )
                        HorizontalDivider(Modifier.weight(1f), color = GreyDivider)
                    }

                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        NagarSevaOutlinedButton(
                            text = "Google",
                            onClick = { /* Google sign in */ },
                            leadingIcon = Icons.Default.Search,
                            modifier = Modifier.weight(1f)
                        )
                        NagarSevaOutlinedButton(
                            text = "Mobile",
                            onClick = { /* Mobile OTP */ },
                            leadingIcon = Icons.Default.Phone,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // ── REGISTER LINK ────────────────────
                    Spacer(Modifier.height(8.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 32.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Don't have an account? ",
                            color = Color(0xFF757575),
                            fontSize = 14.sp
                        )
                        // Outlined Register Now button
                        Surface(
                            modifier = Modifier.clickable {
                                navController.navigate(
                                    Screen.Register.route)
                            },
                            shape = RoundedCornerShape(6.dp),
                            border = BorderStroke(
                                1.dp, Color(0xFF1B5E20)),
                            color = Color.Transparent
                        ) {
                            Text(
                                "Register Now",
                                color = Color(0xFF1B5E20),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(
                                    horizontal = 10.dp,
                                    vertical = 4.dp
                                )
                            )
                        }
                    }

                    Spacer(Modifier.height(32.dp))
                }
            }

            LoadingOverlay(isLoading = loginState is UiState.Loading)
        }
    }
}
