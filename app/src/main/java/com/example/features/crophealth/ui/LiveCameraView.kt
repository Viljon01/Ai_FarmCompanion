package com.example.features.crophealth.ui

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.core.*
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.ui.theme.*
import java.io.File
import java.util.concurrent.Executors

@Composable
fun LiveCameraView(
    onPhotoCaptured: (Bitmap) -> Unit,
    onUploadFallback: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    var permissionRequested by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasCameraPermission = isGranted
        permissionRequested = true
    }

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    if (!hasCameraPermission) {
        // Fallback UI when permission is denied or pending
        CameraPermissionDeniedView(
            onRequestPermission = { permissionLauncher.launch(Manifest.permission.CAMERA) },
            onUploadPhotoInstead = onUploadFallback,
            onCancel = onCancel,
            modifier = modifier
        )
        return
    }

    // Camera State
    var lensFacing by remember { mutableStateOf(CameraSelector.LENS_FACING_BACK) }
    var imageCapture: ImageCapture? by remember { mutableStateOf(null) }
    var isCapturing by remember { mutableStateOf(false) }
    var cameraError by remember { mutableStateOf<String?>(null) }

    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }

    DisposableEffect(Unit) {
        onDispose {
            cameraExecutor.shutdown()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .testTag("live_camera_view")
    ) {
        // Camera Preview
        if (cameraError == null) {
            AndroidView(
                factory = { ctx ->
                    val previewView = PreviewView(ctx).apply {
                        scaleType = PreviewView.ScaleType.FILL_CENTER
                    }

                    val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                    cameraProviderFuture.addListener({
                        try {
                            val cameraProvider = cameraProviderFuture.get()
                            val preview = Preview.Builder().build().also {
                                it.setSurfaceProvider(previewView.surfaceProvider)
                            }

                            val capture = ImageCapture.Builder()
                                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                                .build()
                            imageCapture = capture

                            val cameraSelector = CameraSelector.Builder()
                                .requireLensFacing(lensFacing)
                                .build()

                            cameraProvider.unbindAll()
                            cameraProvider.bindToLifecycle(
                                lifecycleOwner,
                                cameraSelector,
                                preview,
                                capture
                            )
                        } catch (exc: Exception) {
                            Log.e("LiveCameraView", "Camera binding failed: ${exc.message}", exc)
                            cameraError = "Camera preview unavailable: ${exc.localizedMessage}"
                        }
                    }, ContextCompat.getMainExecutor(ctx))

                    previewView
                },
                update = {
                    // Update if lensFacing changes
                },
                modifier = Modifier.fillMaxSize()
            )
        } else {
            // Camera unavailable fallback view
            CameraUnavailableView(
                errorMessage = cameraError ?: "Camera could not be started",
                onUploadInstead = onUploadFallback,
                onCancel = onCancel
            )
        }

        // Overlay Scanning Reticle with subtle animated scanning beam
        CropScanningReticleOverlay(
            modifier = Modifier.fillMaxSize()
        )

        // Top Navigation Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onCancel,
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.5f))
                    .testTag("camera_cancel_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Cancel camera scan",
                    tint = Color.White
                )
            }

            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color.Black.copy(alpha = 0.6f),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.3f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.size(8.dp).background(AgriForestGreen, CircleShape))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Scan Your Crop",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Flip Camera Button
            IconButton(
                onClick = {
                    lensFacing = if (lensFacing == CameraSelector.LENS_FACING_BACK) {
                        CameraSelector.LENS_FACING_FRONT
                    } else {
                        CameraSelector.LENS_FACING_BACK
                    }
                },
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.5f))
                    .testTag("camera_flip_button")
            ) {
                Icon(
                    imageVector = Icons.Default.FlipCameraAndroid,
                    contentDescription = "Switch camera facing",
                    tint = Color.White
                )
            }
        }

        // Bottom Controls: Instruction banner + Capture button
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp, start = 16.dp, end = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Farmer Guidance Pill
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = Color.Black.copy(alpha = 0.7f),
                border = androidx.compose.foundation.BorderStroke(1.dp, AgriLineSubtle.copy(alpha = 0.4f)),
                modifier = Modifier.padding(bottom = 20.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.FilterCenterFocus,
                        contentDescription = null,
                        tint = AgriGoldenOchre,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Place the affected leaf or plant inside the frame.",
                        color = Color.White,
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Capture Shutter Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Secondary Photo Upload option
                IconButton(
                    onClick = onUploadFallback,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.2f))
                        .testTag("camera_gallery_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.PhotoLibrary,
                        contentDescription = "Upload from gallery",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Main Capture Trigger
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.3f))
                        .border(3.dp, Color.White, CircleShape)
                        .clickable(enabled = !isCapturing) {
                            val capture = imageCapture
                            if (capture != null && !isCapturing) {
                                isCapturing = true
                                val photoFile = File(
                                    context.cacheDir,
                                    "crop_scan_${System.currentTimeMillis()}.jpg"
                                )
                                val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

                                capture.takePicture(
                                    outputOptions,
                                    cameraExecutor,
                                    object : ImageCapture.OnImageSavedCallback {
                                        override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                                            val bitmap = BitmapFactory.decodeFile(photoFile.absolutePath)
                                            isCapturing = false
                                            if (bitmap != null) {
                                                // Adjust orientation if needed
                                                ContextCompat.getMainExecutor(context).execute {
                                                    onPhotoCaptured(bitmap)
                                                }
                                            } else {
                                                ContextCompat.getMainExecutor(context).execute {
                                                    onUploadFallback()
                                                }
                                            }
                                        }

                                        override fun onError(exc: ImageCaptureException) {
                                            Log.e("LiveCameraView", "Photo capture failed: ${exc.message}", exc)
                                            isCapturing = false
                                            ContextCompat.getMainExecutor(context).execute {
                                                onUploadFallback()
                                            }
                                        }
                                    }
                                )
                            } else {
                                onUploadFallback()
                            }
                        }
                        .testTag("capture_photo_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(62.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                    )
                }

                // Cancel Button
                IconButton(
                    onClick = onCancel,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.2f))
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close camera",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

/**
 * Animated Scanning Reticle Overlay
 */
@Composable
fun CropScanningReticleOverlay(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "scan_beam")
    val beamOffset by transition.animateFloat(
        initialValue = 0.25f,
        targetValue = 0.75f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "beam_pos"
    )

    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        val frameWidth = w * 0.76f
        val frameHeight = h * 0.44f
        val left = (w - frameWidth) / 2f
        val top = (h - frameHeight) / 2.3f
        val right = left + frameWidth
        val bottom = top + frameHeight

        // Dark dim outer area
        val cornerLen = 32.dp.toPx()
        val strokeW = 3.5.dp.toPx()
        val cornerColor = Color(0xFFD99B26) // AgriGoldenOchre

        // Draw 4 corner brackets
        // Top-Left
        drawLine(cornerColor, Offset(left, top), Offset(left + cornerLen, top), strokeWidth = strokeW)
        drawLine(cornerColor, Offset(left, top), Offset(left, top + cornerLen), strokeWidth = strokeW)

        // Top-Right
        drawLine(cornerColor, Offset(right, top), Offset(right - cornerLen, top), strokeWidth = strokeW)
        drawLine(cornerColor, Offset(right, top), Offset(right, top + cornerLen), strokeWidth = strokeW)

        // Bottom-Left
        drawLine(cornerColor, Offset(left, bottom), Offset(left + cornerLen, bottom), strokeWidth = strokeW)
        drawLine(cornerColor, Offset(left, bottom), Offset(left, bottom - cornerLen), strokeWidth = strokeW)

        // Bottom-Right
        drawLine(cornerColor, Offset(right, bottom), Offset(right - cornerLen, bottom), strokeWidth = strokeW)
        drawLine(cornerColor, Offset(right, bottom), Offset(right, bottom - cornerLen), strokeWidth = strokeW)

        // Delicate green scanning laser line
        val scanY = top + (frameHeight * beamOffset)
        drawLine(
            color = Color(0xFF4C7D52).copy(alpha = 0.85f),
            start = Offset(left + 8.dp.toPx(), scanY),
            end = Offset(right - 8.dp.toPx(), scanY),
            strokeWidth = 2.dp.toPx()
        )
    }
}

/**
 * Camera Permission Denied View
 */
@Composable
fun CameraPermissionDeniedView(
    onRequestPermission: () -> Unit,
    onUploadPhotoInstead: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(AgriWarmBg)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = AgriCardSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, AgriLineSubtle),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .clip(CircleShape)
                        .background(AgriWarningLight),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = null,
                        tint = AgriWarningAmber,
                        modifier = Modifier.size(34.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Camera Access Required",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = AgriTextPrimary
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Camera access is required for live crop scanning. Point your camera at crop leaves to evaluate foliar health.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = AgriTextSecondary,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = onRequestPermission,
                    colors = ButtonDefaults.buttonColors(containerColor = AgriForestGreen),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().testTag("permission_retry_button")
                ) {
                    Text("Grant Permission", fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedButton(
                    onClick = onUploadPhotoInstead,
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, AgriForestGreen),
                    modifier = Modifier.fillMaxWidth().testTag("upload_photo_instead_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.PhotoLibrary,
                        contentDescription = null,
                        tint = AgriForestGreen,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Upload Photo Instead", color = AgriForestGreen, fontWeight = FontWeight.SemiBold)
                }

                Spacer(modifier = Modifier.height(10.dp))

                TextButton(onClick = onCancel) {
                    Text("Cancel", color = AgriTextSecondary)
                }
            }
        }
    }
}

/**
 * Camera Unavailable View
 */
@Composable
fun CameraUnavailableView(
    errorMessage: String,
    onUploadInstead: () -> Unit,
    onCancel: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AgriWarmBg)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = AgriCardSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, AgriLineSubtle),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(AgriSageLight),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.NoPhotography,
                        contentDescription = null,
                        tint = AgriForestGreen,
                        modifier = Modifier.size(30.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Live Camera Unavailable",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = AgriTextPrimary
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = errorMessage,
                    style = MaterialTheme.typography.bodySmall,
                    color = AgriTextSecondary
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = onUploadInstead,
                    colors = ButtonDefaults.buttonColors(containerColor = AgriForestGreen),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Upload Crop Photo", fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(8.dp))

                TextButton(onClick = onCancel) {
                    Text("Back to Dashboard", color = AgriTextSecondary)
                }
            }
        }
    }
}
