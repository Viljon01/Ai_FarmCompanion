package com.example.features.yield

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
import com.example.ui.components.PageHeader
import com.example.ui.components.SubtleFarmBackground
import com.example.ui.theme.*
import com.example.data.localization.*

/**
 * FEATURE 08 — YIELD ESTIMATION & COST BREAKDOWN
 * Transparent cost vs profit calculations, expected tonnage, harvest readiness,
 * and smart selling routing.
 */
@Composable
fun YieldCostScreen(
    cropContext: CropContext,
    onNavigateSelling: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val yield = cropContext.yieldEstimate

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 18.dp, vertical = 12.dp)
    ) {
        PageHeader(
            stageTag = st("STAGE 05 • HARVEST"),
            title = st("Yield & Cost Forecast"),
            subtitle = st("Estimated harvest volume and financial return"),
            onBack = onBack
        )

        // Hand-drawn Illustration Hero Card: Before Harvest
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
                        .height(180.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(AgriWarmBg)
                ) {
                    SubtleFarmBackground(modifier = Modifier.fillMaxSize())
                    Image(
                        painter = painterResource(id = R.drawable.img_before_harvest),
                        contentDescription = "Healthy mature tomato crop with ripe red fruits",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "“${st("Before Harvest")}”",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = AgriForestGreen
                )
                Text(
                    text = "“${st("Will I get a good yield?")}”",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = AgriTextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = st("Based on canopy coverage, flower-to-fruit conversion rates, and 2-acre plot telemetry in Coimbatore."),
                    style = MaterialTheme.typography.bodySmall,
                    color = AgriTextSecondary,
                    fontSize = 12.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Big Metric Card: Estimated Harvest Range
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = AgriSageLight),
            border = androidx.compose.foundation.BorderStroke(1.2.dp, AgriForestGreen.copy(alpha = 0.3f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = st("PROJECTED YIELD (2 ACRES)"),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = AgriForestGreen,
                        letterSpacing = 0.8.sp
                    )
                    Surface(
                        color = AgriCardSurface,
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, AgriLineSubtle)
                    ) {
                        Text(
                            text = "${st("Confidence")}: ${yield.confidencePercent}%",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = AgriForestGreen,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "${yield.expectedYieldTonnesMin} – ${yield.expectedYieldTonnesMax} ${st("Tonnes")}",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = AgriForestGreen
                )

                Text(
                    text = "${st("Estimated")} ${yield.expectedYieldBagsMin} ${st("to")} ${yield.expectedYieldBagsMax} ${st("crates (25kg standard farm crates)")}",
                    style = MaterialTheme.typography.bodySmall,
                    color = AgriTextPrimary,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "${st("Harvest window opens in")} ${yield.harvestWindowDays} ${t("common.days")}.",
                    style = MaterialTheme.typography.bodySmall,
                    color = AgriTextSecondary,
                    fontSize = 12.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Financial Visibility: Cost vs Revenue
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = AgriCardSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, AgriLineSubtle),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = st("COST & PROFIT VISIBILITY"),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = AgriTextSecondary,
                    letterSpacing = 0.8.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Breakdown items
                val costItems = listOf(
                    st("Hybrid Seedlings & Nursery") to "₹12,000",
                    st("Fertilizer, Compost & Bio-nutrients") to "₹18,500",
                    st("Labor, Trellising & Weeding") to "₹24,000",
                    st("Drip Irrigation & Electric Pump") to "₹8,500"
                )

                costItems.forEach { (item, cost) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(item, fontSize = 12.sp, color = AgriTextSecondary)
                        Text(cost, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = AgriTextPrimary)
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = AgriLineSubtle)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(st("Total Production Cost"), fontWeight = FontWeight.Bold, color = AgriTextPrimary, fontSize = 13.sp)
                    Text("₹${yield.estimatedCostTotal}", fontWeight = FontWeight.Bold, color = AgriTomatoRed, fontSize = 13.sp)
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(st("Projected Revenue (@ ₹28/kg)"), fontWeight = FontWeight.Bold, color = AgriTextPrimary, fontSize = 13.sp)
                    Text("₹${yield.projectedRevenue}", fontWeight = FontWeight.Bold, color = AgriForestGreen, fontSize = 13.sp)
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Net Profit Highlight
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = AgriPaleYellow,
                    border = androidx.compose.foundation.BorderStroke(1.dp, AgriOchre.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(st("Estimated Net Profit"), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = AgriEarthBrown)
                            Text("₹${yield.projectedRevenue - yield.estimatedCostTotal}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold, color = AgriForestGreen)
                        }
                        Surface(color = AgriForestGreen, shape = RoundedCornerShape(8.dp)) {
                            Text("+151% Net ROI", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Harvest Readiness Tips
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = AgriCardSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, AgriLineSubtle),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = st("HARVEST TIMING RECOMMENDATIONS"),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = AgriTextSecondary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "• ${st("Pick at Breaker stage (pink blossom end) if transporting >30 km to market.")}\n• ${st("Harvest early morning before 9:00 AM to prevent sun scald and keep fruit pulp cool.")}\n• ${st("Use plastic stackable ventilated crates instead of gunny bags to avoid compression bruising.")}",
                    fontSize = 12.sp,
                    color = AgriTextPrimary,
                    lineHeight = 18.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Proceed to Smart Selling Action Button
        Button(
            onClick = onNavigateSelling,
            colors = ButtonDefaults.buttonColors(containerColor = AgriForestGreen),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("proceed_to_selling_button")
        ) {
            Text("${st("Proceed to")} 06 ${t("journey.sell")} (${st("Smart Selling & Markets")})", fontSize = 13.sp)
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
