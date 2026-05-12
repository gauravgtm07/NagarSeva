package com.nagarseva.app.ui.report

import android.Manifest
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.nagarseva.app.ui.theme.*
import java.io.File

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraScreen(navController: NavController) {
    val context = LocalContext.current
    val permissionState = rememberPermissionState(Manifest.permission.CAMERA)

    LaunchedEffect(Unit) {
        if (!permissionState.status.isGranted) {
            permissionState.launchPermissionRequest()
        }
    }

    if (permissionState.status.isGranted) {
        CameraCaptureContent(navController)
    } else {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Black),
            contentAlignment = Alignment.Center
        ) {
            Text("Camera permission is required to capture defects.", color = White)
        }
    }
}

@Composable
fun CameraCaptureContent(navController: NavController) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember {
        ProcessCameraProvider.getInstance(context)
    }
    var imageCapture: ImageCapture? by remember {
        mutableStateOf(null)
    }
    var flashEnabled by remember { mutableStateOf(false) }

    Box(Modifier.fillMaxSize().background(Black)) {

        // CAMERA PREVIEW
        AndroidView(
            factory = { ctx ->
                val previewView = PreviewView(ctx)
                cameraProviderFuture.addListener({
                    val cameraProvider = cameraProviderFuture.get()
                    val preview = Preview.Builder().build()
                        .also { it.setSurfaceProvider(previewView.surfaceProvider) }
                    
                    val imgCapture = ImageCapture.Builder()
                        .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                        .setFlashMode(if (flashEnabled) ImageCapture.FLASH_MODE_ON else ImageCapture.FLASH_MODE_OFF)
                        .build()
                    imageCapture = imgCapture

                    try {
                        cameraProvider.unbindAll()
                        cameraProvider.bindToLifecycle(
                            lifecycleOwner,
                            CameraSelector.DEFAULT_BACK_CAMERA,
                            preview,
                            imgCapture
                        )
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }, ContextCompat.getMainExecutor(ctx))
                previewView
            },
            modifier = Modifier.fillMaxSize(),
            update = {
                imageCapture?.flashMode = if (flashEnabled) ImageCapture.FLASH_MODE_ON else ImageCapture.FLASH_MODE_OFF
            }
        )

        // TOP OVERLAY (gradient scrim)
        Box(
            Modifier.fillMaxWidth().height(100.dp)
                .background(
                    Brush.verticalGradient(
                        listOf(Black.copy(0.7f), Transparent)
                    )
                )
                .align(Alignment.TopCenter)
        ) {
            Row(
                Modifier.fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .align(Alignment.Center),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {
                    navController.popBackStack()
                }) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack, null,
                        tint = White,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Text(
                    "Capture Defect",
                    color = White, fontSize = 17.sp,
                    fontWeight = Bold,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
                IconButton(onClick = {
                    flashEnabled = !flashEnabled
                }) {
                    Icon(
                        if (flashEnabled) Icons.Filled.FlashOn
                        else Icons.Filled.FlashOff,
                        null, tint = White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }

        // STEP PROGRESS PILL
        Surface(
            Modifier.align(Alignment.TopCenter)
                .padding(top = 72.dp),
            shape = RoundedCornerShape(50.dp),
            color = Black.copy(0.55f)
        ) {
            Row(
                Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    Modifier.size(8.dp).background(
                        GreenSecondary, CircleShape
                    )
                )
                Text("Step 1: Photo", color = White, fontSize = 12.sp)
                Box(
                    Modifier.width(24.dp).height(3.dp)
                        .background(White.copy(0.4f), RoundedCornerShape(2.dp))
                )
                Box(
                    Modifier.width(24.dp).height(3.dp)
                        .background(White.copy(0.4f), RoundedCornerShape(2.dp))
                )
            }
        }

        // FOCUS GUIDE BRACKETS (center of screen)
        Canvas(
            Modifier.size(220.dp).align(Alignment.Center)
        ) {
            val strokeWidth = 3.dp.toPx()
            val bracketLen = 30.dp.toPx()
            val color = White

            // Top-left bracket
            drawLine(color, Offset(0f, bracketLen), Offset(0f, 0f), strokeWidth, cap = StrokeCap.Round)
            drawLine(color, Offset(0f, 0f), Offset(bracketLen, 0f), strokeWidth, cap = StrokeCap.Round)
            // Top-right bracket
            drawLine(color, Offset(size.width - bracketLen, 0f), Offset(size.width, 0f), strokeWidth, cap = StrokeCap.Round)
            drawLine(color, Offset(size.width, 0f), Offset(size.width, bracketLen), strokeWidth, cap = StrokeCap.Round)
            // Bottom-left bracket
            drawLine(color, Offset(0f, size.height - bracketLen), Offset(0f, size.height), strokeWidth, cap = StrokeCap.Round)
            drawLine(color, Offset(0f, size.height), Offset(bracketLen, size.height), strokeWidth, cap = StrokeCap.Round)
            // Bottom-right bracket
            drawLine(color, Offset(size.width, size.height - bracketLen), Offset(size.width, size.height), strokeWidth, cap = StrokeCap.Round)
            drawLine(color, Offset(size.width - bracketLen, size.height), Offset(size.width, size.height), strokeWidth, cap = StrokeCap.Round)

            // Center dot
            drawCircle(White, radius = 4.dp.toPx(), center = Offset(size.width / 2, size.height / 2))
        }

        // TIP CARD
        Surface(
            Modifier.align(Alignment.Center)
                .padding(top = 160.dp),
            shape = RoundedCornerShape(50.dp),
            color = White.copy(0.92f)
        ) {
            Row(
                Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    Modifier.size(8.dp)
                        .background(GreenSecondary, CircleShape)
                )
                Text(
                    "Center the pothole and tap to focus before capturing.",
                    fontSize = 12.sp, color = Charcoal
                )
            }
        }

        // BOTTOM OVERLAY
        Box(
            Modifier.fillMaxWidth().height(160.dp)
                .background(
                    Brush.verticalGradient(
                        listOf(Transparent, Black.copy(0.8f))
                    )
                )
                .align(Alignment.BottomCenter)
        ) {
            Row(
                Modifier.fillMaxWidth()
                    .align(Alignment.Center)
                    .padding(horizontal = 40.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Gallery button
                Column(horizontalAlignment = CenterHorizontally) {
                    Box(
                        Modifier.size(52.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Black.copy(0.5f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.PhotoLibrary, null,
                            tint = White,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                    Text(
                        "Gallery", color = White, fontSize = 10.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                // CAPTURE BUTTON
                Box(
                    Modifier.size(76.dp)
                        .clickable {
                            val photoFile = File(
                                context.filesDir,
                                "NS_${System.currentTimeMillis()}.jpg"
                            )
                            val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
                            imageCapture?.takePicture(
                                outputOptions,
                                ContextCompat.getMainExecutor(context),
                                object : ImageCapture.OnImageSavedCallback {
                                    override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                                        // Always overwrite — works for both first photo AND retake
                                        navController.previousBackStackEntry
                                            ?.savedStateHandle
                                            ?.set("photo_uri", photoFile.toUri().toString())
                                        navController.popBackStack()
                                    }

                                    override fun onError(exc: ImageCaptureException) {
                                        exc.printStackTrace()
                                    }
                                }
                            )
                        },
                    contentAlignment = Alignment.Center
                ) {
                    // Outer ring
                    Box(
                        Modifier.fillMaxSize()
                            .border(3.dp, GreenSecondary, CircleShape)
                    )
                    // Inner filled circle
                    Box(
                        Modifier.size(58.dp)
                            .background(White, CircleShape)
                    )
                }

                // Retake button (Refresh/Reset scenario - not technically "retake from preview")
                Column(horizontalAlignment = CenterHorizontally) {
                    Box(
                        Modifier.size(52.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Black.copy(0.5f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Refresh, null,
                            tint = White,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                    Text(
                        "Retake", color = White, fontSize = 10.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
    }
}
