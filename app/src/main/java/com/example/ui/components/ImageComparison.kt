package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Compare
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
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
import com.example.data.CropObservation
import com.example.ui.theme.*

/**
 * Reusable ImageComparison Component
 * Displays Previous Observation vs Current Observation with side-by-side or toggle mode,
 * delta indicators, and AI confidence badges.
 */
@Composable
fun ImageComparison(
    previousObservation: CropObservation?,
    currentObservation: CropObservation,
    modifier: Modifier = Modifier,
    onCompareToggle: (() -> Unit)? = null
) {
    var viewMode by remember { mutableStateOf("side_by_side") } // "side_by_side" or "slider"
    var selectedTab by remember { mutableIntStateOf(1) } // 0: Previous, 1: Current

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = AgriCardSurface),
        border = androidx.compose.foundation.BorderStroke(1.2.dp, AgriLineSubtle),
        modifier = modifier
            .fillMaxWidth()
            .testTag("image_comparison_component")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "OBSERVATION COMPARISON",
                        style = MaterialTheme.typography.labelSmall,
                        color = AgriTextSecondary,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp
                    )
                    Text(
                        text = "Day 28 vs Day 35 Foliar Shift",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = AgriTextPrimary
                    )
                }

                // View Mode Toggle
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = AgriSurfaceSubtle,
                    border = androidx.compose.foundation.BorderStroke(1.dp, AgriLineSubtle),
                    modifier = Modifier.clickable {
                        viewMode = if (viewMode == "side_by_side") "tabs" else "side_by_side"
                    }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Compare,
                            contentDescription = "Switch compare mode",
                            tint = AgriForestGreen,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (viewMode == "side_by_side") "Side-by-Side" else "Tabbed",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = AgriForestGreen
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Score Transition Highlight
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = AgriWarningLight,
                border = androidx.compose.foundation.BorderStroke(1.dp, AgriWarningAmber.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Health Score",
                            style = MaterialTheme.typography.labelSmall,
                            color = AgriEarthBrown,
                            fontWeight = FontWeight.Medium
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "${previousObservation?.healthScore ?: 86}%",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = AgriForestGreen
                            )
                            Text(
                                text = "  →  ",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = AgriTextSecondary
                            )
                            Text(
                                text = "${currentObservation.healthScore}%",
                                style = MaterialTheme.typography.titleLarge,
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
                            text = "Shift: -15%",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = AgriWarningAmber,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            if (viewMode == "side_by_side") {
                // Side-by-Side Visual Cards
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Previous Card
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = AgriSurfaceSubtle),
                        border = androidx.compose.foundation.BorderStroke(1.dp, AgriLineSubtle),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column {
                            Image(
                                painter = painterResource(id = R.drawable.img_during_cultivation),
                                contentDescription = "Day 28 Baseline Observation",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(100.dp)
                                    .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)),
                                contentScale = ContentScale.Crop
                            )
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = "Day 28 • Previous",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = AgriForestGreen,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "86% Healthy",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = AgriForestGreen
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = previousObservation?.leafCondition ?: "Clean lamina",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontSize = 11.sp,
                                    color = AgriTextSecondary,
                                    lineHeight = 14.sp
                                )
                            }
                        }
                    }

                    // Current Card
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = AgriCardSurface),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, AgriWarningAmber.copy(alpha = 0.6f)),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column {
                            Image(
                                painter = painterResource(id = R.drawable.img_during_cultivation),
                                contentDescription = "Day 35 Today's Observation",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(100.dp)
                                    .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)),
                                contentScale = ContentScale.Crop
                            )
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = "Day 35 • Current",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = AgriWarningAmber,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${currentObservation.healthScore}% Active",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = AgriWarningAmber
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = currentObservation.observedChanges,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontSize = 11.sp,
                                    color = AgriTextSecondary,
                                    lineHeight = 14.sp
                                )
                            }
                        }
                    }
                }
            } else {
                // Tabbed comparison
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = AgriSurfaceSubtle,
                    contentColor = AgriForestGreen,
                    modifier = Modifier.clip(RoundedCornerShape(8.dp))
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("Previous (Day 28 • 86%)", fontSize = 12.sp) }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("Today (Day 35 • 71%)", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                val obsToShow = if (selectedTab == 0) previousObservation else currentObservation
                if (obsToShow != null) {
                    Image(
                        painter = painterResource(id = R.drawable.img_during_cultivation),
                        contentDescription = obsToShow.dateLabel,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = obsToShow.detail,
                        style = MaterialTheme.typography.bodySmall,
                        color = AgriTextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Analysis Metrics Footer
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(AgriSurfaceSubtle, RoundedCornerShape(10.dp))
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = AgriForestGreen,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Confidence: ${currentObservation.confidencePercent}%",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold,
                        color = AgriTextPrimary
                    )
                }

                Text(
                    text = "AI-Assisted Assessment",
                    style = MaterialTheme.typography.bodySmall,
                    fontSize = 11.sp,
                    color = AgriTextSecondary
                )
            }
        }
    }
}
