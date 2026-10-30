package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import com.example.ui.theme.AgriWarmClay

data class JourneyStageItem(
    val stageNumber: Int,
    val stageCode: String,
    val title: String,
    val subtitle: String,
    val statusLabel: String
)

val journeyStagesList = listOf(
    JourneyStageItem(1, "01 PLAN", "Choose the right crop", "Soil suitability, seed selection & field budgeting", "Completed"),
    JourneyStageItem(2, "02 GROW", "Follow stage-wise guidance", "Day 34 vegetative care, drip irrigation & organic feed", "In Progress"),
    JourneyStageItem(3, "03 MONITOR", "Track crop changes", "Upload crop photos & detect early leaf symptoms", "Action Needed"),
    JourneyStageItem(4, "04 PROTECT", "Identify possible risks", "High humidity advisory & organic foliar precautions", "Alert"),
    JourneyStageItem(5, "05 HARVEST", "Estimate your yield", "5.2 – 6.1 tonnes forecast & picking schedule", "Upcoming"),
    JourneyStageItem(6, "06 SELL", "Find suitable markets", "Local Uzhavar Sandhai vs Mandi price comparison", "Upcoming")
)

@Composable
fun VisualJourneyTimeline(
    activeStageIndex: Int,
    onStageSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    // Subtle pulsating movement for active stage arrow
    val infiniteTransition = rememberInfiniteTransition(label = "journey_arrow_pulse")
    val arrowOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 6f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "arrow_offset"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("crop_journey_timeline")
    ) {
        journeyStagesList.forEachIndexed { index, stage ->
            val isCurrent = stage.stageNumber == activeStageIndex
            val isPast = stage.stageNumber < activeStageIndex
            val isLast = index == journeyStagesList.size - 1

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { onStageSelected(stage.stageNumber) }
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.Top
            ) {
                // Vertical Timeline spine & icon node
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.width(54.dp)
                ) {
                    // Node circle containing hand-drawn stage icon
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(
                                when {
                                    isCurrent -> AgriSageLight
                                    isPast -> AgriSurfaceSubtle
                                    else -> AgriCardSurface
                                }
                            )
                            .border(
                                width = if (isCurrent) 2.dp else 1.2.dp,
                                color = if (isCurrent) AgriForestGreen else AgriLineSubtle,
                                shape = CircleShape
                            )
                    ) {
                        StageAgriIcon(
                            stageNumber = stage.stageNumber,
                            size = 32.dp,
                            tintColor = if (isCurrent) AgriForestGreen else AgriLineArtGreen.copy(alpha = 0.85f),
                            fillColor = if (isCurrent) AgriSageGreen.copy(alpha = 0.35f) else Color.Transparent
                        )

                        if (isPast) {
                            // Completed tick
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .size(16.dp)
                                    .background(AgriForestGreen, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Completed",
                                    tint = Color.White,
                                    modifier = Modifier.size(11.dp)
                                )
                            }
                        }
                    }

                    // Vertical connecting vine line (if not last)
                    if (!isLast) {
                        Box(
                            modifier = Modifier
                                .width(2.dp)
                                .height(56.dp)
                                .background(
                                    if (isPast) AgriForestGreen.copy(alpha = 0.6f)
                                    else AgriLineSubtle
                                )
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Stage Information Card
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = if (isCurrent) AgriCardSurface else AgriCardSurface.copy(alpha = 0.85f),
                    border = androidx.compose.foundation.BorderStroke(
                        width = if (isCurrent) 1.5.dp else 1.dp,
                        color = if (isCurrent) AgriForestGreen.copy(alpha = 0.4f) else AgriLineSubtle
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .padding(bottom = if (isLast) 0.dp else 14.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = com.example.data.localization.st(stage.stageCode),
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isCurrent) AgriForestGreen else AgriTextSecondary,
                                fontWeight = FontWeight.Bold
                            )

                            // Status badge
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = when (stage.statusLabel) {
                                    "In Progress" -> AgriSageLight
                                    "Action Needed", "Alert" -> AgriSurfaceSubtle
                                    "Completed" -> AgriForestGreen.copy(alpha = 0.1f)
                                    else -> AgriSurfaceSubtle
                                },
                                modifier = Modifier.padding(start = 6.dp)
                            ) {
                                Text(
                                    text = com.example.data.localization.st(stage.statusLabel),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = when (stage.statusLabel) {
                                        "In Progress" -> AgriForestGreen
                                        "Action Needed", "Alert" -> AgriWarmClay
                                        "Completed" -> AgriForestGreen
                                        else -> AgriTextSecondary
                                    },
                                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(3.dp))

                        Text(
                            text = "“${com.example.data.localization.st(stage.title)}”",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = AgriTextPrimary
                        )

                        Spacer(modifier = Modifier.height(3.dp))

                        Text(
                            text = com.example.data.localization.st(stage.subtitle),
                            style = MaterialTheme.typography.bodySmall,
                            color = AgriTextSecondary
                        )

                        if (isCurrent) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.offset(x = arrowOffset.dp)
                            ) {
                                Text(
                                    text = com.example.data.localization.st("View current stage details"),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = AgriForestGreen
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = null,
                                    tint = AgriForestGreen,
                                    modifier = Modifier.size(13.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
