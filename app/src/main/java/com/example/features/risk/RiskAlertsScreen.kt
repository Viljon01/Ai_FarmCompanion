package com.example.features.risk

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
import com.example.data.localization.st
import com.example.data.localization.t
import com.example.ui.components.AgriWeatherIllustration
import com.example.ui.components.PageHeader
import com.example.ui.components.SubtleFarmBackground
import com.example.ui.theme.*

import com.example.data.localization.*

/**
 * FEATURE 07 — EARLY WARNING & RISK ADVISORY
 * Actionable mitigation checklist, weather impact correlation, resolution tracking,
 * and next-stage routing to Stage 05 (Yield & Harvest).
 */
@Composable
fun RiskAlertsScreen(
    cropContext: CropContext,
    onResolveRisk: (String) -> Unit,
    onNavigateYield: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    var checkedActions by remember {
        mutableStateOf(
            mapOf(
                "action_1" to false,
                "action_2" to false,
                "action_3" to false,
                "action_4" to false
            )
        )
    }

    var isResolved by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 18.dp, vertical = 12.dp)
    ) {
        PageHeader(
            stageTag = st("STAGE 04 • PROTECT"),
            title = t("risk.title"),
            subtitle = t("risk.subtitle"),
            onBack = onBack
        )

        // Hero Illustration Card: Risk Observation
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = AgriCardSurface),
            border = androidx.compose.foundation.BorderStroke(1.2.dp, AgriLineSubtle),
            modifier = Modifier.fillMaxWidth()
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
                        .height(170.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(AgriWarmBg)
                ) {
                    SubtleFarmBackground(modifier = Modifier.fillMaxSize())
                    Image(
                        painter = painterResource(id = R.drawable.img_during_cultivation),
                        contentDescription = "Indian farmer observing crop risks with caution",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "“${st("Identify Possible Risks")}”",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = AgriTomatoRed
                )
                Text(
                    text = st("Pre-emptive Advisory • Tomato Field"),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = AgriTextPrimary
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // High Humidity Warning Box
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = AgriPaleYellow,
            border = androidx.compose.foundation.BorderStroke(1.2.dp, AgriOchre.copy(alpha = 0.5f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AgriWeatherIllustration("humidity", size = 28.dp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = st("Weather Alert: 78% Humidity + Rain in 36 Hours"),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = AgriEarthBrown
                        )
                        Text(
                            text = st("Coimbatore Rural microclimate reading"),
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 11.sp,
                            color = AgriTextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = st("High relative humidity combined with dense 35-day vegetative foliage creates favorable conditions for fungal spores (Early Blight / Alternaria). Moisture lingering on lower leaves for >4 hours triggers infection."),
                    style = MaterialTheme.typography.bodySmall,
                    fontSize = 12.sp,
                    color = AgriTextPrimary
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Active Risk Details Card
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = AgriCardSurface),
            border = androidx.compose.foundation.BorderStroke(1.2.dp, if (isResolved) AgriForestGreen else AgriTomatoRed.copy(alpha = 0.5f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(if (isResolved) AgriForestGreen else AgriTomatoRed)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isResolved) st("RESOLVED / SAFE") else st("MODERATE RISK DETECTED"),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (isResolved) AgriForestGreen else AgriTomatoRed
                        )
                    }

                    Surface(
                        color = if (isResolved) AgriSageLight else AgriPaleYellow,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = if (isResolved) st("Status: Protected") else "${st("Stage")}: ${st("Vegetative")} (${t("common.day")} 35)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = AgriTextPrimary,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = st("Foliar Moisture Retention & Early Blight Risk"),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = AgriTextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = st("Foliage in 2-acre plot exhibits lower leaf margin chlorosis. Without proactive intervention, yield may decrease by 12-18% upon fruit formation."),
                    style = MaterialTheme.typography.bodySmall,
                    fontSize = 12.sp,
                    color = AgriTextSecondary
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = st("ACTIONABLE MITIGATION CHECKLIST"),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = AgriForestGreen,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Action items with checkboxes
                val actions = listOf(
                    "action_1" to st("Prune lower 2 rows of yellowing leaves touching wet soil to eliminate splash infection."),
                    "action_2" to st("Clear silt from furrow drainage channels to prevent standing root puddles."),
                    "action_3" to st("Apply organic 5% NSKE (Neem Seed Kernel Extract) foliar spray before upcoming rain."),
                    "action_4" to st("Pause nitrogen-heavy foliar fertilizers until relative humidity drops below 65%.")
                )

                actions.forEach { (id, desc) ->
                    val isChecked = checkedActions[id] == true
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .clickable {
                                val current = checkedActions.toMutableMap()
                                current[id] = !isChecked
                                checkedActions = current
                            }
                            .background(if (isChecked) AgriSageLight.copy(alpha = 0.5f) else Color.Transparent)
                            .padding(horizontal = 6.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = isChecked,
                            onCheckedChange = {
                                val current = checkedActions.toMutableMap()
                                current[id] = it
                                checkedActions = current
                            },
                            colors = CheckboxDefaults.colors(checkedColor = AgriForestGreen)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = desc,
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 12.sp,
                            color = if (isChecked) AgriTextSecondary else AgriTextPrimary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Mark as Resolved button
                OutlinedButton(
                    onClick = {
                        isResolved = !isResolved
                        onResolveRisk("risk_tomato_foliar_fungal")
                    },
                    border = androidx.compose.foundation.BorderStroke(1.2.dp, AgriForestGreen),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = if (isResolved) Icons.Default.Replay else Icons.Default.Check,
                        contentDescription = null,
                        tint = AgriForestGreen,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isResolved) "Re-open Risk Review" else "Mark Actions Completed",
                        color = AgriForestGreen,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Advisory Note
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = AgriWarmBg,
            border = androidx.compose.foundation.BorderStroke(1.dp, AgriLineSubtle),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.Top
            ) {
                Icon(Icons.Default.Info, contentDescription = null, tint = AgriForestGreen, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Agri-Tech Notice: Recommendations align with TNAU (Tamil Nadu Agricultural University) tomato integrated pest management protocols.",
                    fontSize = 11.sp,
                    color = AgriTextSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Next Stage Navigation Button
        Button(
            onClick = onNavigateYield,
            colors = ButtonDefaults.buttonColors(containerColor = AgriForestGreen),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("proceed_to_yield_button")
        ) {
            Text("${st("Proceed to")} 05 ${t("journey.harvest")} (${st("Yield & Cost Forecast")})", fontSize = 13.sp)
            Spacer(modifier = Modifier.width(6.dp))
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
