package com.nagarseva.app.ui.profile

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.nagarseva.app.data.local.entity.getInitials
import com.nagarseva.app.ui.components.NagarSevaButton
import com.nagarseva.app.ui.components.NagarSevaTextField
import com.nagarseva.app.ui.theme.*
import com.nagarseva.app.util.UiState
import com.nagarseva.app.viewmodel.AuthViewModel

@Composable
fun EditProfileScreen(
    navController: NavHostController
) {
    val authViewModel: AuthViewModel = hiltViewModel()
    val currentUser by authViewModel.currentUser.collectAsStateWithLifecycle()
    val updateState by authViewModel.updateProfileState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    // State for form fields
    var fullName by remember { 
        mutableStateOf(currentUser?.fullName ?: "") 
    }
    var phone by remember { 
        mutableStateOf(currentUser?.phone ?: "") 
    }
    var address by remember { 
        mutableStateOf(currentUser?.residentialAddress ?: "") 
    }

    // Update fields when currentUser loads
    LaunchedEffect(currentUser) {
        currentUser?.let { user ->
            fullName = user.fullName
            phone = user.phone
            address = user.residentialAddress
        }
    }

    // Handle save state
    LaunchedEffect(updateState) {
        when (updateState) {
            is UiState.Success -> {
                snackbarHostState.showSnackbar("Profile updated successfully!")
                authViewModel.resetUpdateProfileState()
                navController.popBackStack()
            }
            is UiState.Error -> {
                snackbarHostState.showSnackbar((updateState as UiState.Error).message)
                authViewModel.resetUpdateProfileState()
            }
            else -> {}
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(White)
                .padding(padding)
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
                        text = "Edit Profile",
                        style = MaterialTheme.typography.titleLarge,
                        color = Charcoal,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.size(48.dp))
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // AVATAR SECTION
                Box(
                    modifier = Modifier.padding(vertical = 24.dp),
                    contentAlignment = Alignment.BottomEnd
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .background(GreenPrimary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = currentUser?.getInitials() ?: "NS",
                            color = White,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .background(GreenSecondary, CircleShape)
                            .border(2.dp, White, CircleShape)
                            .padding(4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Change Avatar",
                            tint = White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Text(
                    text = fullName.ifBlank { "User" },
                    style = MaterialTheme.typography.titleLarge,
                    color = Charcoal,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = if (currentUser?.isVerified == true) "Verified Resident" else "Resident",
                    style = MaterialTheme.typography.bodySmall,
                    color = GreyText,
                    modifier = Modifier.padding(top = 4.dp)
                )

                Spacer(modifier = Modifier.height(32.dp))

                // FORM FIELDS
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    NagarSevaTextField(
                        value = fullName,
                        onValueChange = { fullName = it },
                        label = "Full Name",
                        leadingIcon = Icons.Default.Person
                    )

                    NagarSevaTextField(
                        value = currentUser?.email ?: "",
                        onValueChange = { },
                        label = "Email",
                        leadingIcon = Icons.Default.Email,
                        enabled = false
                    )

                    NagarSevaTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = "Phone Number",
                        leadingIcon = Icons.Default.Phone
                    )

                    NagarSevaTextField(
                        value = address,
                        onValueChange = { address = it },
                        label = "Residential Address",
                        leadingIcon = Icons.Default.Home,
                        maxLines = 3
                    )
                    
                    if (currentUser?.isVerified == true) {
                        // IDENTITY VERIFIED ROW
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp)
                                .background(GreenLight, RoundedCornerShape(12.dp))
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(GreenPrimary, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Fingerprint,
                                    contentDescription = null,
                                    tint = White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Text(
                                text = "Identity Verified",
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(horizontal = 12.dp),
                                color = GreenPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = GreenPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // PHOTO BANNER (Visual only)
                Card(
                    modifier = Modifier
                        .padding(horizontal = 24.dp)
                        .fillMaxWidth()
                        .height(110.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Box {
                        AsyncImage(
                            model = "https://images.unsplash.com/photo-1441974231531-c6227db76b6e",
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        Box(
                            Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.35f))
                        )
                        Text(
                            text = "Helping our city grow together.",
                            color = White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(horizontal = 24.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // SAVE BUTTON
                NagarSevaButton(
                    text = "Save Changes",
                    leadingIcon = Icons.Default.Save,
                    isLoading = updateState is UiState.Loading,
                    onClick = {
                        authViewModel.updateProfile(fullName, phone, address)
                    },
                    modifier = Modifier.padding(horizontal = 24.dp).fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
