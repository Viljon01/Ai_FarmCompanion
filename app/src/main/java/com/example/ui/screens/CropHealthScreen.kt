package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CropObservation
import com.example.data.CropProfile
import com.example.ui.components.StageAgriIcon
import com.example.ui.components.SubtleFarmBackground
import com.example.ui.theme.AgriCardSurface
import com.example.ui.theme.AgriEarthBrown
import com.example.ui.theme.AgriForestGreen
import com.example.ui.theme.AgriLineArtGreen
import com.example.ui.theme.AgriLineSubtle
import com.example.ui.theme.AgriSageGreen
import com.example.ui.theme.AgriSageLight
import com.example.ui.theme.AgriSurfaceSubtle
import com.example.ui.theme.AgriTextPrimary
import com.example.ui.theme.AgriTextSecondary
import com.example.ui.theme.AgriTomatoLight
import com.example.ui.theme.AgriWarmBg
import com.example.ui.theme.AgriWarmClay
import com.example.ui.theme.AgriWarningAmber
import com.example.ui.theme.AgriWarningLight

@Composable
fun CropHealthScreen(
    cropProfile: CropProfile?,
    observations: List<CropObservation>,
    onSimulateScan: () -> Unit,
    onBack: () -> Unit,
    onNavigateRiskAlert: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }
    val scrollState = rememberScrollState()

    var isScanningMode by remember { mutableStateOf(false) }

    // Subtle AI scan line animation
    val transition = rememberInfiniteTransition(label = "scan_line")
    val scanProgress by transition.animateFloat(
        initialValue = 0.1f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scan_progress"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(AgriWarmBg)
    ) {
        SubtleFarmBackground()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp, bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.testTag("crop_health_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = AgriTextPrimary
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "03 MONITOR",
                        style = MaterialTheme.typography.labelSmall,
                        color = AgriForestGreen,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Crop Health Monitor",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = AgriTextPrimary
                    )
                }
            }

            // Top small illustration: Farmer using a smartphone to inspect a crop
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = AgriCardSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, AgriLineSubtle),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Hand-drawn illustration: farmer holding smartphone inspecting crop
                    Canvas(modifier = Modifier.size(72.dp)) {
                        val w = size.width
                        val h = size.height

                        // Tomato plant
                        val stem = Path().apply {
                            moveTo(w * 0.72f, h * 0.9f)
                            quadraticTo(w * 0.75f, h * 0.5f, w * 0.70f, h * 0.25f)
                        }
                        drawPath(stem, color = AgriForestGreen, style = Stroke(width = 2.2f, cap = StrokeCap.Round))

                        // Leaf
                        val leaf = Path().apply {
                            moveTo(w * 0.72f, h * 0.5f)
                            cubicTo(w * 0.88f, h * 0.45f, w * 0.90f, h * 0.35f, w * 0.82f, h * 0.32f)
                            cubicTo(w * 0.74f, h * 0.34f, w * 0.72f, h * 0.42f, w * 0.72f, h * 0.5f)
                        }
                        drawPath(leaf, color = AgriSageGreen.copy(alpha = 0.3f))
                        drawPath(leaf, color = AgriForestGreen, style = Stroke(width = 1.6f))

                        // Farmer silhouette
                        drawCircle(AgriLineArtGreen, radius = 7f, center = Offset(w * 0.28f, h * 0.28f)) // head with turban
                        drawLine(AgriLineArtGreen, Offset(w * 0.28f, h * 0.35f), Offset(w * 0.28f, h * 0.82f), strokeWidth = 3f, cap = StrokeCap.Round) // body
                        drawLine(AgriLineArtGreen, Offset(w * 0.28f, h * 0.48f), Offset(w * 0.48f, h * 0.45f), strokeWidth = 2.2f, cap = StrokeCap.Round) // arm

                        // Smartphone in farmer's hand
                        drawRoundRect(
                            color = AgriForestGreen,
                            topLeft = Offset(w * 0.46f, h * 0.36f),
                            size = androidx.compose.ui.geometry.Size(12f, 18f),
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(3f, 3f),
                            style = Stroke(width = 1.8f)
                        )

                        // Subtle dashed alignment beam towards plant
                        drawLine(
                            color = AgriSageGreen,
                            start = Offset(w * 0.55f, h * 0.42f),
                            end = Offset(w * 0.68f, h * 0.42f),
                            strokeWidth = 1.4f,
                            pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(4f, 4f))
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Daily Crop Inspection",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = AgriTextPrimary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Upload today's crop image to compare changes with Day 28 observation.",
                            style = MaterialTheme.typography.bodySmall,
                            color = AgriTextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Upload Box Area
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = AgriCardSurface),
                border = androidx.compose.foundation.BorderStroke(1.2.dp, AgriForestGreen.copy(alpha = 0.35f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("upload_image_section")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Upload today's crop image",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = AgriTextPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Focus closely on representative middle or lower foliage.",
                        style = MaterialTheme.typography.bodySmall,
                        color = AgriTextSecondary
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Scanner Viewport Box
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(AgriSurfaceSubtle)
                            .border(1.dp, AgriLineSubtle, RoundedCornerShape(14.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val w = size.width
                            val h = size.height

                            // Draw subtle crop leaves illustration inside viewport
                            val leafL = Path().apply {
                                moveTo(w * 0.42f, h * 0.65f)
                                quadraticTo(w * 0.35f, h * 0.35f, w * 0.48f, h * 0.28f)
                                quadraticTo(w * 0.58f, h * 0.40f, w * 0.42f, h * 0.65f)
                            }
                            drawPath(leafL, color = AgriSageGreen.copy(alpha = 0.2f))
                            drawPath(leafL, color = AgriForestGreen.copy(alpha = 0.6f), style = Stroke(width = 1.5f))

                            // Subtle slow scanning line
                            val scanY = h * scanProgress
                            drawLine(
                                color = AgriForestGreen.copy(alpha = 0.7f),
                                start = Offset(w * 0.15f, scanY),
                                end = Offset(w * 0.85f, scanY),
                                strokeWidth = 2f,
                                cap = StrokeCap.Round
                            )
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.AddAPhoto,
                                contentDescription = "Camera inspection",
                                tint = AgriForestGreen,
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Scan or select leaf observation",
                                fontSize = 12.sp,
                                color = AgriForestGreen,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            isScanningMode = true
                            onSimulateScan()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AgriForestGreen),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .testTag("upload_image_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.AddAPhoto,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Upload Image",
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // AFTER UPLOAD: Visual Comparison
            // "Previous Observation vs Today's Observation"
            // "Health Score 86% → 71%"
            // "Status: Possible health change detected"
            Text(
                text = "Observation Analysis",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = AgriTextPrimary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Visual shift between Day 28 and today's leaf sampling.",
                style = MaterialTheme.typography.bodySmall,
                color = AgriTextSecondary
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Score Transition Header Card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = AgriWarningLight),
                border = androidx.compose.foundation.BorderStroke(1.2.dp, AgriWarningAmber.copy(alpha = 0.4f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("health_score_transition_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "HEALTH SCORE CHANGE",
                                style = MaterialTheme.typography.labelSmall,
                                color = AgriEarthBrown,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "86%",
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = AgriForestGreen
                                )
                                Text(
                                    text = "  →  ",
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = AgriTextSecondary
                                )
                                Text(
                                    text = "71%",
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = AgriWarningAmber
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color.White
                        ) {
                            Text(
                                text = "-15% Shift",
                                color = AgriWarningAmber,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = AgriWarningAmber,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Status: Possible health change detected",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = AgriTextPrimary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Comparison Grid: Previous Observation vs Today's Observation
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Previous observation (Day 28)
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = AgriCardSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, AgriLineSubtle),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "Day 28 (Previous)",
                            style = MaterialTheme.typography.labelSmall,
                            color = AgriForestGreen,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "86% Healthy",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = AgriForestGreen
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Clean green lamina with crisp margins. No moisture spotting.",
                            style = MaterialTheme.typography.bodySmall,
                            color = AgriTextSecondary
                        )
                    }
                }

                // Today's observation (Day 34)
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = AgriCardSurface),
                    border = androidx.compose.foundation.BorderStroke(1.2.dp, AgriWarningAmber.copy(alpha = 0.5f)),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "Day 34 (Today)",
                            style = MaterialTheme.typography.labelSmall,
                            color = AgriWarningAmber,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "71% Active",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = AgriWarningAmber
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Mild lower leaf chlorosis and brown margin specks detected.",
                            style = MaterialTheme.typography.bodySmall,
                            color = AgriTextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Small hand-drawn illustration: Farmer examining affected plant
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = AgriCardSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, AgriLineSubtle),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Small vector illustration: farmer gently bending down to check affected plant
                    Canvas(modifier = Modifier.size(64.dp)) {
                        val w = size.width
                        val h = size.height

                        // Affected plant with one yellowed leaf
                        val stem = Path().apply {
                            moveTo(w * 0.70f, h * 0.88f)
                            lineTo(w * 0.70f, h * 0.35f)
                        }
                        drawPath(stem, color = AgriForestGreen, style = Stroke(width = 2f, cap = StrokeCap.Round))

                        // Normal leaf
                        val leafTop = Path().apply {
                            moveTo(w * 0.70f, h * 0.45f)
                            cubicTo(w * 0.85f, h * 0.38f, w * 0.85f, h * 0.28f, w * 0.75f, h * 0.30f)
                            close()
                        }
                        drawPath(leafTop, color = AgriSageGreen.copy(alpha = 0.4f))
                        drawPath(leafTop, color = AgriForestGreen, style = Stroke(width = 1.5f))

                        // Affected lower leaf (amber tint)
                        val leafBottom = Path().apply {
                            moveTo(w * 0.70f, h * 0.65f)
                            cubicTo(w * 0.85f, h * 0.65f, w * 0.88f, h * 0.75f, w * 0.72f, h * 0.72f)
                            close()
                        }
                        drawPath(leafBottom, color = AgriWarningAmber.copy(alpha = 0.35f))
                        drawPath(leafBottom, color = AgriWarningAmber, style = Stroke(width = 1.5f))

                        // Farmer kneeling / bending observing
                        drawCircle(AgriLineArtGreen, radius = 6f, center = Offset(w * 0.32f, h * 0.42f)) // head
                        // Bending torso
                        val body = Path().apply {
                            moveTo(w * 0.32f, h * 0.48f)
                            lineTo(w * 0.36f, h * 0.70f)
                            lineTo(w * 0.25f, h * 0.88f)
                        }
                        drawPath(body, color = AgriLineArtGreen, style = Stroke(width = 2.5f, cap = StrokeCap.Round))

                        // Extended inspecting hand
                        drawLine(AgriLineArtGreen, Offset(w * 0.34f, h * 0.58f), Offset(w * 0.62f, h * 0.64f), strokeWidth = 2f, cap = StrokeCap.Round)
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Examine Affected Lower Leaves",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = AgriTextPrimary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Check leaf undersides for damp spots before applying organic foliar spray.",
                            style = MaterialTheme.typography.bodySmall,
                            color = AgriTextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            // Action to 04 PROTECT (Risk Alert Screen)
            Button(
                onClick = onNavigateRiskAlert,
                colors = ButtonDefaults.buttonColors(containerColor = AgriForestGreen),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("view_risk_assessment_button")
            ) {
                Text("Open Risk Alert & Advisory", fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}
