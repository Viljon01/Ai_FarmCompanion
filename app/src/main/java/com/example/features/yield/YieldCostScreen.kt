package com.example.features.yield

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.CropContext
import com.example.ui.components.PageHeader
import com.example.ui.components.SubtleFarmBackground
import com.example.ui.theme.*
import com.example.data.localization.*

/**
 * FEATURE 08 — YIELD ESTIMATION & COST BREAKDOWN (Interactive & Editable)
 * Real-time editable estimation for Acres, Tonnes per Acre, Market Price, and Production Cost.
 */
@Composable
fun YieldCostScreen(
    cropContext: CropContext,
    onNavigateSelling: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    // Interactive & Editable Estimation States
    var acresInput by remember { mutableStateOf("${cropContext.farm.landSizeAcres}") }
    var yieldPerAcreInput by remember { mutableStateOf("2.8") }
    var pricePerKgInput by remember { mutableStateOf("28") }
    var totalCostInput by remember { mutableStateOf("${(cropContext.farm.landSizeAcres * 31500).toInt()}") }

    // Live Calculations
    val acres = acresInput.toDoubleOrNull() ?: cropContext.farm.landSizeAcres
    val yieldPerAcre = yieldPerAcreInput.toDoubleOrNull() ?: 2.8
    val pricePerKg = pricePerKgInput.toDoubleOrNull() ?: 28.0
    val totalCost = totalCostInput.toDoubleOrNull() ?: (acres * 31500)

    val totalYieldTonnes = acres * yieldPerAcre
    val totalCrates = (totalYieldTonnes * 40).toInt() // 25kg standard crates
    val projectedRevenue = totalYieldTonnes * 1000 * pricePerKg
    val netProfit = projectedRevenue - totalCost
    val netRoiPercent = if (totalCost > 0) ((netProfit / totalCost) * 100).toInt() else 0

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 18.dp, vertical = 12.dp)
    ) {
        PageHeader(
            stageTag = st("STAGE 05 • HARVEST"),
            title = st("Interactive Yield & Profit Estimator"),
            subtitle = st("Edit parameters below to simulate different harvest & profit scenarios"),
            onBack = onBack
        )

        // Hero Card: Harvest Preview
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = AgriCardSurface),
            border = BorderStroke(1.2.dp, AgriLineSubtle),
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
                        .height(160.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(AgriWarmBg)
                ) {
                    SubtleFarmBackground(modifier = Modifier.fillMaxSize())
                    Image(
                        painter = painterResource(id = R.drawable.img_before_harvest),
                        contentDescription = "Mature crop ready for harvest",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "${st("Active Crop")}: ${st(cropContext.cropCycle.cropName)} (${cropContext.cropCycle.variety})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = AgriForestGreen
                )
                Text(
                    text = st("Customize yield tonnage, price per kg, and cost to calculate expected net profit:"),
                    style = MaterialTheme.typography.bodySmall,
                    color = AgriTextSecondary,
                    fontSize = 11.5.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Editable Input Parameters Card
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = AgriCardSurface),
            border = androidx.compose.foundation.BorderStroke(1.2.dp, AgriForestGreen.copy(alpha = 0.4f)),
            modifier = Modifier.fillMaxWidth().testTag("editable_yield_parameters")
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null, tint = AgriForestGreen, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = st("Try Your Own Yield & Financial Estimations"),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = AgriForestGreen
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = acresInput,
                        onValueChange = { acresInput = it },
                        label = { Text(st("Land Size (Acres)"), maxLines = 1, overflow = TextOverflow.Ellipsis) },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = yieldPerAcreInput,
                        onValueChange = { yieldPerAcreInput = it },
                        label = { Text(st("Yield (Tonnes/Acre)"), maxLines = 1, overflow = TextOverflow.Ellipsis) },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = pricePerKgInput,
                        onValueChange = { pricePerKgInput = it },
                        label = { Text(st("Selling Price (₹/kg)"), maxLines = 1, overflow = TextOverflow.Ellipsis) },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = totalCostInput,
                        onValueChange = { totalCostInput = it },
                        label = { Text(st("Total Cost (₹)"), maxLines = 1, overflow = TextOverflow.Ellipsis) },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Live Calculated Yield Summary Card
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = AgriSageLight),
            border = androidx.compose.foundation.BorderStroke(1.2.dp, AgriForestGreen),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "LIVE ESTIMATED YIELD ($acres ACRES)",
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
                            text = "${st("Dynamic Calculation")}",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = AgriForestGreen,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "${String.format("%.1f", totalYieldTonnes)} ${st("Tonnes")}",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = AgriForestGreen
                )

                Text(
                    text = "${st("Equivalent to")} ~$totalCrates ${st("crates (25kg standard crates)")}",
                    style = MaterialTheme.typography.bodySmall,
                    color = AgriTextPrimary,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "${st("Expected Harvest Window")}: Day 75 – 85.",
                    style = MaterialTheme.typography.bodySmall,
                    color = AgriTextSecondary,
                    fontSize = 11.5.sp
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
                    text = st("ESTIMATED FINANCIAL RETURN"),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = AgriTextSecondary,
                    letterSpacing = 0.8.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(st("Total Production Cost"), fontWeight = FontWeight.Bold, color = AgriTextPrimary, fontSize = 13.sp)
                    Text("₹${String.format("%,d", totalCost.toLong())}", fontWeight = FontWeight.Bold, color = AgriTomatoRed, fontSize = 13.sp)
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(st("Projected Revenue (@ ₹$pricePerKg/kg)"), fontWeight = FontWeight.Bold, color = AgriTextPrimary, fontSize = 13.sp)
                    Text("₹${String.format("%,d", projectedRevenue.toLong())}", fontWeight = FontWeight.Bold, color = AgriForestGreen, fontSize = 13.sp)
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = AgriLineSubtle)

                // Net Profit Highlight
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (netProfit >= 0) AgriPaleYellow else AgriTomatoRed.copy(alpha = 0.1f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (netProfit >= 0) AgriOchre.copy(alpha = 0.5f) else AgriTomatoRed),
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
                            Text(
                                text = "₹${String.format("%,d", netProfit.toLong())}",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (netProfit >= 0) AgriForestGreen else AgriTomatoRed
                            )
                        }
                        Surface(
                            color = if (netProfit >= 0) AgriForestGreen else AgriTomatoRed,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "${if (netRoiPercent >= 0) "+" else ""}$netRoiPercent% ROI",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Harvest Timing Recommendations
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
