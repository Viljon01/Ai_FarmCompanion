package com.example.features.journey

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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExpandMore
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
import com.example.services.CropJourneyService
import com.example.ui.components.PageHeader
import com.example.ui.components.StageAgriIcon
import com.example.ui.components.SubtleFarmBackground
import com.example.ui.theme.*
import com.example.data.localization.*

data class TimelineMilestone(
    val dayNumber: Int,
    val dayLabel: String,
    val stageName: String,
    val title: String,
    val healthScore: Int,
    val observation: String,
    val notes: String,
    val isPastOrCurrent: Boolean,
    val imageRes: Int
)

/**
 * FEATURE 05 — CROP JOURNEY
 * Interactive Timeline with Day 0, Day 15, Day 30, Day 35, Day 42, Day 60, Day 90
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CropJourneyScreen(
    cropContext: CropContext,
    stages: List<CropJourneyService.JourneyStage>,
    onSelectStage: (Int) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    var selectedMilestone by remember { mutableStateOf<TimelineMilestone?>(null) }

    val milestones = listOf(
        TimelineMilestone(
            dayNumber = 0,
            dayLabel = "Day 0",
            stageName = "01 PLAN",
            title = "Transplanting in Main Field",
            healthScore = 95,
            observation = "4,800 healthy Shivam tomato nursery seedlings transplanted on raised beds with drip lateral spacing.",
            notes = "Soil solarized; 10 tonnes farmyard manure incorporated.",
            isPastOrCurrent = true,
            imageRes = R.drawable.img_before_cultivation
        ),
        TimelineMilestone(
            dayNumber = 15,
            dayLabel = "Day 15",
            stageName = "02 GROW",
            title = "Early Vegetative Growth & Weeding",
            healthScore = 92,
            observation = "Uniform root establishment and emergence of first lateral branch nodes.",
            notes = "First compost side-dressing completed; drip lines flushed.",
            isPastOrCurrent = true,
            imageRes = R.drawable.img_during_cultivation
        ),
        TimelineMilestone(
            dayNumber = 30,
            dayLabel = "Day 30",
            stageName = "02 GROW",
            title = "Vegetative Staking & Canopy Trellising",
            healthScore = 86,
            observation = "Rapid shoot elongation. Tied growing vines gently to bamboo stakes with jute twine.",
            notes = "Panchagavya organic spray applied for foliar resilience.",
            isPastOrCurrent = true,
            imageRes = R.drawable.img_during_cultivation
        ),
        TimelineMilestone(
            dayNumber = 35,
            dayLabel = "Day 35",
            stageName = "03 MONITOR",
            title = "Crop Observation & Moisture Spotting",
            healthScore = 71,
            observation = "Subtle yellow margins detected on bottom two leaf tiers. High humidity alert active.",
            notes = "Prune bottom leaves touching damp soil; apply 5% NSKE spray.",
            isPastOrCurrent = true,
            imageRes = R.drawable.img_during_cultivation
        ),
        TimelineMilestone(
            dayNumber = 42,
            dayLabel = "Day 42",
            stageName = "04 PROTECT",
            title = "Possible Health Recheck & Protection",
            healthScore = 80,
            observation = "Post-spray evaluation to verify foliar spots are arrested prior to flower truss emergence.",
            notes = "Maintain early morning drip cycles (7 AM - 9 AM).",
            isPastOrCurrent = false,
            imageRes = R.drawable.img_during_cultivation
        ),
        TimelineMilestone(
            dayNumber = 60,
            dayLabel = "Day 60",
            stageName = "04 PROTECT",
            title = "Full Flowering & Fruit Set",
            healthScore = 88,
            observation = "Profuse yellow flower clusters; early marble-sized green tomato fruit set.",
            notes = "Monitor calcium uptake to prevent blossom end rot.",
            isPastOrCurrent = false,
            imageRes = R.drawable.img_before_harvest
        ),
        TimelineMilestone(
            dayNumber = 90,
            dayLabel = "Day 90",
            stageName = "05 HARVEST",
            title = "Peak Breaker Harvest & Market Dispatch",
            healthScore = 85,
            observation = "Breaker pink blush on mature tomatoes. Estimated 5.2 – 6.1 tonnes yield.",
            notes = "Sort in ventilated plastic crates for Coimbatore Uzhavar Sandhai.",
            isPastOrCurrent = false,
            imageRes = R.drawable.img_smart_selling
        )
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
                .padding(horizontal = 20.dp, vertical = 14.dp)
        ) {
            PageHeader(
                stageTag = st("LIFECYCLE TIMELINE"),
                title = t("journey.title"),
                subtitle = t("journey.subtitle"),
                onBack = onBack
            )

            // Current Stage Banner
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = AgriCardSurface),
                border = androidx.compose.foundation.BorderStroke(1.2.dp, AgriForestGreen.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth().testTag("journey_current_stage_banner")
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    StageAgriIcon(stageNumber = cropContext.cropCycle.currentStageIndex, size = 44.dp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "${st("CURRENT STAGE")}: ${st(cropContext.cropCycle.currentStageName)}",
                            style = MaterialTheme.typography.labelSmall,
                            color = AgriForestGreen,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = st(cropContext.cropCycle.currentStageDescription),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = AgriTextPrimary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = st("Interactive Lifecycle Timeline"),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = AgriTextPrimary
            )
            Text(
                text = st("Tap any day milestone to view field observations, photos & agronomic notes."),
                style = MaterialTheme.typography.bodySmall,
                color = AgriTextSecondary
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Connected Vertical Timeline
            Column(modifier = Modifier.fillMaxWidth()) {
                milestones.forEachIndexed { index, ms ->
                    val isLast = index == milestones.size - 1
                    val isToday = ms.dayNumber == 35

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { selectedMilestone = ms }
                            .padding(vertical = 2.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        // Left Node Column
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.width(48.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when {
                                            isToday -> AgriWarningAmber
                                            ms.isPastOrCurrent -> AgriForestGreen
                                            else -> AgriSurfaceSubtle
                                        }
                                    )
                                    .border(
                                        width = if (isToday) 2.dp else 1.dp,
                                        color = if (isToday) AgriWarningAmber else AgriLineSubtle,
                                        shape = CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (ms.isPastOrCurrent && !isToday) {
                                    Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                } else {
                                    Text(
                                        text = "${ms.dayNumber}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isToday) Color.White else AgriTextSecondary
                                    )
                                }
                            }

                            if (!isLast) {
                                Box(
                                    modifier = Modifier
                                        .width(2.dp)
                                        .height(54.dp)
                                        .background(
                                            if (ms.isPastOrCurrent) AgriForestGreen.copy(alpha = 0.5f)
                                            else AgriLineSubtle
                                        )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        // Right Milestone Content Card
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isToday) AgriWarningLight else AgriCardSurface,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isToday) AgriWarningAmber.copy(alpha = 0.5f) else AgriLineSubtle
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .padding(bottom = if (isLast) 0.dp else 12.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "${st(ms.dayLabel)} • ${st(ms.stageName)}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (isToday) AgriWarningAmber else AgriForestGreen,
                                        fontWeight = FontWeight.Bold
                                    )

                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = if (isToday) AgriWarningAmber else AgriSageLight
                                    ) {
                                        Text(
                                            text = if (isToday) "${t("common.day")} 35 ${st("Active")}" else "${ms.healthScore}% ${t("dashboard.health")}",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isToday) Color.White else AgriForestGreen,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = st(ms.title),
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = AgriTextPrimary
                                )

                                Spacer(modifier = Modifier.height(2.dp))

                                Text(
                                    text = st(ms.observation),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = AgriTextSecondary,
                                    maxLines = 2
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
        }

        // Milestone Detail Modal
        if (selectedMilestone != null) {
            val ms = selectedMilestone!!
            ModalBottomSheet(
                onDismissRequest = { selectedMilestone = null },
                containerColor = AgriWarmBg
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 10.dp)
                        .navigationBarsPadding()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "${st(ms.dayLabel)} (${st(ms.stageName)})",
                                style = MaterialTheme.typography.labelSmall,
                                color = AgriForestGreen,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = st(ms.title),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = AgriTextPrimary
                            )
                        }

                        IconButton(onClick = { selectedMilestone = null }) {
                            Icon(Icons.Default.Close, contentDescription = t("common.close"))
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Image(
                        painter = painterResource(id = ms.imageRes),
                        contentDescription = st(ms.title),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                            .clip(RoundedCornerShape(14.dp)),
                        contentScale = ContentScale.Crop
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(st("Field Observation:"), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = AgriTextPrimary)
                    Text(st(ms.observation), style = MaterialTheme.typography.bodyMedium, color = AgriTextSecondary)

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(st("Agronomic Notes & Action:"), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = AgriForestGreen)
                    Text(st(ms.notes), style = MaterialTheme.typography.bodySmall, color = AgriTextPrimary)

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}
