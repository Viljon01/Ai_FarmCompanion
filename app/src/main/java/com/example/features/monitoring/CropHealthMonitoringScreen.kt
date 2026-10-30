package com.example.features.monitoring

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.CropContext
import com.example.ui.components.ImageComparison
import com.example.ui.components.PageHeader
import com.example.ui.components.StageAgriIcon
import com.example.ui.components.SubtleFarmBackground
import com.example.ui.theme.*

/**
 * FEATURE 06 — CROP HEALTH MONITORING
 * Combines hand-drawn visual storytelling, side-by-side observation comparison,
 * foliar diagnostics, scan simulation, and risk mitigation routing.
 */
@Composable
fun CropHealthMonitoringScreen(
    cropContext: CropContext,
    onSimulateScan: () -> Unit,
    onNavigateRisk: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    var isScanning by remember { mutableStateOf(false) }
    var scanSuccessMessage by remember { mutableStateOf<String?>(null) }

    val currentObs = cropContext.latestObservation
    val prevObs = cropContext.previousObservation

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 18.dp, vertical = 12.dp)
    ) {
        PageHeader(
            stageTag = "STAGE 03 • MONITOR",
            title = "Crop Health & Foliar Shift",
            subtitle = "Track foliar changes and early disease symptoms",
            onBack = onBack
        )

        // Hand-drawn Illustration Hero Card: During Cultivation
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = AgriCardSurface),
            border = androidx.compose.foundation.BorderStroke(1.2.dp, AgriLineSubtle),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(AgriWarmBg)
                ) {
                    SubtleFarmBackground(modifier = Modifier.fillMaxSize())
                    Image(
                        painter = painterResource(id = R.drawable.img_during_cultivation),
                        contentDescription = "Farmer examining young tomato plants",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "“During Cultivation”",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = AgriForestGreen
                )
                Text(
                    text = "“Is my crop growing normally?”",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = AgriTextPrimary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "AI monitors leaf tissue texture, humidity condensation, and edge discoloration across key growth intervals.",
                    style = MaterialTheme.typography.bodySmall,
                    color = AgriTextSecondary,
                    fontSize = 12.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Scan Action Bar
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = AgriSageLight,
            border = androidx.compose.foundation.BorderStroke(1.2.dp, AgriForestGreen.copy(alpha = 0.3f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Field Camera Observation",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = AgriForestGreen
                    )
                    Text(
                        text = "Capture fresh leaf photos to detect changes",
                        style = MaterialTheme.typography.bodySmall,
                        fontSize = 11.sp,
                        color = AgriTextSecondary
                    )
                }

                Button(
                    onClick = {
                        isScanning = true
                        onSimulateScan()
                        scanSuccessMessage = "Leaf scan analyzed: Mild bottom chlorosis registered at Day 35"
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AgriForestGreen),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("scan_crop_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.AddAPhoto,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Scan Leaf", fontSize = 12.sp)
                }
            }
        }

        if (scanSuccessMessage != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = AgriPaleYellow,
                border = androidx.compose.foundation.BorderStroke(1.dp, AgriOchre),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = AgriForestGreen, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = scanSuccessMessage ?: "",
                        fontSize = 11.sp,
                        color = AgriTextPrimary,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Core Image Comparison Component: Day 28 vs Day 35
        ImageComparison(
            previousObservation = prevObs,
            currentObservation = currentObs
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Diagnostic Details Card
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = AgriCardSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, AgriLineSubtle),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "FOLIAR DIAGNOSIS",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = AgriTextSecondary
                    )
                    Surface(
                        color = AgriPaleYellow,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "Confidence: ${currentObs.confidencePercent}%",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = AgriEarthBrown,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Observed change bullet
                Row(verticalAlignment = Alignment.Top) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(AgriTomatoRed)
                            .padding(top = 4.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Observed Foliar Shift",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = AgriTextPrimary
                        )
                        Text(
                            text = currentObs.observedChanges,
                            style = MaterialTheme.typography.bodySmall,
                            color = AgriTextSecondary,
                            fontSize = 12.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Suggested action bullet
                Row(verticalAlignment = Alignment.Top) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(AgriForestGreen)
                            .padding(top = 4.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Immediate Recommendation",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = AgriTextPrimary
                        )
                        Text(
                            text = currentObs.suggestedNextSteps,
                            style = MaterialTheme.typography.bodySmall,
                            color = AgriTextSecondary,
                            fontSize = 12.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Direct Button to Early Warning / Risk Screen
                Button(
                    onClick = onNavigateRisk,
                    colors = ButtonDefaults.buttonColors(containerColor = AgriForestGreen),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("view_risk_advisory_button")
                ) {
                    Text("View Early Warning & Risk Advisory", fontSize = 13.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
