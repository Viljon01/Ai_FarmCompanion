package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Info
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
import com.example.data.CropProfile
import com.example.ui.components.SubtleFarmBackground
import com.example.ui.theme.AgriCardSurface
import com.example.ui.theme.AgriEarthBrown
import com.example.ui.theme.AgriForestGreen
import com.example.ui.theme.AgriLineArtGreen
import com.example.ui.theme.AgriLineSubtle
import com.example.ui.theme.AgriSageGreen
import com.example.ui.theme.AgriSurfaceSubtle
import com.example.ui.theme.AgriTextPrimary
import com.example.ui.theme.AgriTextSecondary
import com.example.ui.theme.AgriWarmBg
import com.example.ui.theme.AgriWarmClay
import com.example.ui.theme.AgriWarningAmber
import com.example.ui.theme.AgriWarningLight

@Composable
fun RiskAlertScreen(
    cropProfile: CropProfile?,
    onBack: () -> Unit,
    onNavigateYield: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }
    val scrollState = rememberScrollState()

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
                    .padding(top = 4.dp, bottom = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.testTag("risk_back_button")
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
                        text = "04 PROTECT",
                        style = MaterialTheme.typography.labelSmall,
                        color = AgriForestGreen,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Crop Protection & Risk Advisory",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = AgriTextPrimary
                    )
                }
            }

            // Farmer + Crop Illustration Card with Alert Indicator
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = AgriCardSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, AgriLineSubtle),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("risk_farmer_crop_illustration_card")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Hand-drawn illustration: Farmer standing beside tomato crop inspecting leaf with warning badge
                    Canvas(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp)
                    ) {
                        val w = size.width
                        val h = size.height

                        // Rolling furrow line
                        val ground = Path().apply {
                            moveTo(w * 0.1f, h * 0.85f)
                            quadraticTo(w * 0.5f, h * 0.82f, w * 0.9f, h * 0.86f)
                        }
                        drawPath(ground, color = AgriLineArtGreen.copy(alpha = 0.5f), style = Stroke(width = 1.5f))

                        // Tomato plant
                        val stem = Path().apply {
                            moveTo(w * 0.62f, h * 0.84f)
                            cubicTo(w * 0.60f, h * 0.55f, w * 0.65f, h * 0.35f, w * 0.62f, h * 0.20f)
                        }
                        drawPath(stem, color = AgriForestGreen, style = Stroke(width = 2.4f, cap = StrokeCap.Round))

                        // Normal leaves
                        val topLeaf = Path().apply {
                            moveTo(w * 0.62f, h * 0.35f)
                            cubicTo(w * 0.76f, h * 0.30f, w * 0.78f, h * 0.18f, w * 0.68f, h * 0.20f)
                            close()
                        }
                        drawPath(topLeaf, color = AgriSageGreen.copy(alpha = 0.35f))
                        drawPath(topLeaf, color = AgriForestGreen, style = Stroke(width = 1.6f))

                        // Affected lower leaf with amber edge
                        val botLeaf = Path().apply {
                            moveTo(w * 0.61f, h * 0.62f)
                            cubicTo(w * 0.78f, h * 0.64f, w * 0.80f, h * 0.78f, w * 0.66f, h * 0.74f)
                            close()
                        }
                        drawPath(botLeaf, color = AgriWarningAmber.copy(alpha = 0.3f))
                        drawPath(botLeaf, color = AgriWarningAmber, style = Stroke(width = 1.8f))

                        // Farmer standing observing
                        drawCircle(AgriForestGreen, radius = 8f, center = Offset(w * 0.36f, h * 0.32f)) // head with turban
                        drawLine(AgriForestGreen, Offset(w * 0.36f, h * 0.40f), Offset(w * 0.36f, h * 0.82f), strokeWidth = 3f, cap = StrokeCap.Round) // body
                        drawLine(AgriForestGreen, Offset(w * 0.36f, h * 0.52f), Offset(w * 0.52f, h * 0.60f), strokeWidth = 2.2f, cap = StrokeCap.Round) // arm pointing to leaf

                        // Floating alert badge
                        drawCircle(AgriWarningAmber.copy(alpha = 0.15f), radius = 18f, center = Offset(w * 0.78f, h * 0.30f))
                        drawCircle(AgriWarningAmber, radius = 18f, center = Offset(w * 0.78f, h * 0.30f), style = Stroke(width = 1.5f))
                        drawLine(AgriWarningAmber, Offset(w * 0.78f, h * 0.22f), Offset(w * 0.78f, h * 0.32f), strokeWidth = 2.2f, cap = StrokeCap.Round)
                        drawCircle(AgriWarningAmber, radius = 1.8f, center = Offset(w * 0.78f, h * 0.36f))
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = AgriWarningAmber,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "⚠️ Possible Risk Detected",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = AgriWarningAmber
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "“Your latest crop observation shows a change compared with the previous observation.”",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = AgriTextPrimary,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Risk Assessment Metrics
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = AgriCardSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, AgriLineSubtle),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("risk_assessment_details_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Assessment Parameters",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = AgriTextPrimary
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    val params = listOf(
                        "Possible Risk" to "Moderate",
                        "Crop Stage" to "Vegetative (Day 34)",
                        "Weather Condition" to "High Humidity (78%)",
                        "Next Step" to "Check affected leaves and continue monitoring."
                    )

                    params.forEach { (label, value) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 5.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.bodyMedium,
                                color = AgriTextSecondary
                            )
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (label == "Possible Risk") AgriWarningLight else AgriSurfaceSubtle
                            ) {
                                Text(
                                    text = value,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (label == "Possible Risk") AgriWarningAmber else AgriTextPrimary,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Recommended Action Steps
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = AgriCardSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, AgriLineSubtle),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Recommended Agronomic Actions",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = AgriTextPrimary
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    val actions = listOf(
                        "1. Aeration Pruning" to "Remove lowest 2-3 leaves touching wet soil to stop splash-borne fungal inoculation.",
                        "2. Preventative Foliar Spray" to "Mix 50ml Neem Seed Kernel Extract (NSKE 5%) or 30ml Panchagavya per 10L water. Spray underside of leaves.",
                        "3. Irrigation Timing" to "Restrict drip cycles strictly to morning hours (7 AM - 9 AM) to allow soil surfaces to dry by dusk."
                    )

                    actions.forEach { (title, detail) ->
                        Column(modifier = Modifier.padding(vertical = 4.dp)) {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = AgriForestGreen
                            )
                            Text(
                                text = detail,
                                style = MaterialTheme.typography.bodySmall,
                                color = AgriTextSecondary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // AI Disclaimer Box
            // "Make it clear that this is an AI-assisted assessment and not a confirmed diagnosis."
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = AgriSurfaceSubtle,
                border = androidx.compose.foundation.BorderStroke(1.dp, AgriLineSubtle),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("ai_assessment_disclaimer")
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Notice",
                        tint = AgriForestGreen,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "AI-Assisted Assessment Notice: This analysis is an early advisory based on visual pattern changes and local weather conditions. It is not a confirmed laboratory diagnosis. Please verify symptoms in your field before chemical treatments.",
                        style = MaterialTheme.typography.bodySmall,
                        fontSize = 12.sp,
                        color = AgriTextSecondary,
                        lineHeight = 17.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Button to 05 HARVEST (Yield Screen)
            Button(
                onClick = onNavigateYield,
                colors = ButtonDefaults.buttonColors(containerColor = AgriForestGreen),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("proceed_to_yield_button")
            ) {
                Text("View 05 HARVEST & Yield Forecast", fontWeight = FontWeight.SemiBold)
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
