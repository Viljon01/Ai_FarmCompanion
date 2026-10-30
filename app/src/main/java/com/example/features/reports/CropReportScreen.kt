package com.example.features.reports

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CropContext
import com.example.ui.components.PageHeader
import com.example.ui.theme.*

import com.example.data.localization.*

/**
 * FEATURE 11 — COMPREHENSIVE CROP REPORT
 * Complete dossier covering farm parcels, health progression trajectory,
 * interventions log, cost audit, and export action.
 */
@Composable
fun CropReportScreen(
    cropContext: CropContext,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    var isExported by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 18.dp, vertical = 12.dp)
    ) {
        PageHeader(
            stageTag = st("RECORDS & AUDIT"),
            title = t("report.title"),
            subtitle = t("report.subtitle"),
            onBack = onBack
        )

        // Dossier Identification Banner
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = AgriWarmBg,
            border = androidx.compose.foundation.BorderStroke(1.2.dp, AgriLineSubtle),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = t("report.dossierTag"),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = AgriTextSecondary,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "${st(cropContext.cropCycle.cropName)} (${st(cropContext.cropCycle.variety)})",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = AgriForestGreen
                        )
                    }

                    Surface(
                        color = AgriSageLight,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = t("report.verified"),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = AgriForestGreen,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(t("report.farmerName"), fontSize = 10.sp, color = AgriTextSecondary)
                        Text(cropContext.farmer.name, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AgriTextPrimary)
                    }
                    Column {
                        Text(t("report.location"), fontSize = 10.sp, color = AgriTextSecondary)
                        Text(cropContext.farm.location, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AgriTextPrimary)
                    }
                    Column {
                        Text(t("report.parcelSize"), fontSize = 10.sp, color = AgriTextSecondary)
                        Text("${cropContext.farm.landSizeAcres} ${t("common.acres")}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AgriTextPrimary)
                    }
                    Column {
                        Text(t("report.age"), fontSize = 10.sp, color = AgriTextSecondary)
                        Text("${t("common.day")} ${cropContext.cropCycle.cropAgeDays}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AgriTextPrimary)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Health Trajectory Progression
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = AgriCardSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, AgriLineSubtle),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = t("report.healthScoreProgression"),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = AgriTextSecondary,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(12.dp))

                val trajectory = listOf(
                    Triple("${t("common.day")} 0 • Sowing", "100%", "Certified hybrid germination in raised nursery bed"),
                    Triple("${t("common.day")} 15 • Transplant", "94%", "Active seedling root establishment in 2-acre plot"),
                    Triple("${t("common.day")} 28 • Vegetative", "86%", "Clean leaf lamina, branching and trellising setup"),
                    Triple("${t("common.day")} 35 • Current Check", "71%", "Lower tier margin chlorosis detected during high humidity")
                )

                trajectory.forEachIndexed { idx, (stage, score, notes) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(if (idx == trajectory.lastIndex) AgriTomatoRed.copy(alpha = 0.2f) else AgriSageLight),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${idx + 1}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (idx == trajectory.lastIndex) AgriTomatoRed else AgriForestGreen
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(st(stage), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AgriTextPrimary)
                                Text(score, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, color = if (idx == trajectory.lastIndex) AgriTomatoRed else AgriForestGreen)
                            }
                            Text(st(notes), fontSize = 11.sp, color = AgriTextSecondary)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Financial & Inputs Summary
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = AgriCardSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, AgriLineSubtle),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = t("report.economics"),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = AgriTextSecondary
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(t("report.totalInputs"), fontSize = 11.sp, color = AgriTextSecondary)
                        Text("₹63,000", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = AgriTomatoRed)
                    }
                    Column {
                        Text(t("report.expectedYield"), fontSize = 11.sp, color = AgriTextSecondary)
                        Text("5.2 - 6.1 Tonnes", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = AgriTextPrimary)
                    }
                    Column {
                        Text(t("report.estNetRevenue"), fontSize = 11.sp, color = AgriTextSecondary)
                        Text("₹95,000", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = AgriForestGreen)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Export Actions
        Button(
            onClick = { isExported = true },
            colors = ButtonDefaults.buttonColors(containerColor = AgriForestGreen),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("export_dossier_button")
        ) {
            Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(t("report.exportBtn"), fontSize = 13.sp)
        }

        if (isExported) {
            Spacer(modifier = Modifier.height(8.dp))
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = AgriSageLight,
                border = androidx.compose.foundation.BorderStroke(1.dp, AgriForestGreen),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = AgriForestGreen, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = t("report.exportedNotice"),
                        fontSize = 11.sp,
                        color = AgriTextPrimary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
