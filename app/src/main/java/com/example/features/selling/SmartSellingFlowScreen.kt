package com.example.features.selling

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
import com.example.data.MarketOption
import com.example.ui.components.PageHeader
import com.example.ui.components.SubtleFarmBackground
import com.example.ui.theme.*
import com.example.data.localization.*

/**
 * FEATURE 09 — SMART SELLING & REGIONAL MARKETS
 * Market price discovery, transport cost deduction, net payout calculation,
 * and handshake illustration storytelling.
 */
@Composable
fun SmartSellingFlowScreen(
    cropContext: CropContext,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    var selectedMarketId by remember { mutableStateOf("market_coimbatore_uzhavar") }

    val defaultMarkets = listOf(
        MarketOption(
            id = "market_coimbatore_uzhavar",
            name = "Coimbatore Uzhavar Sandhai",
            distanceKm = 12,
            ratePerKg = 32,
            commissionPercent = 0,
            transportCostEst = 1200,
            demandLevel = "High",
            netPayoutEst = 166400,
            isRecommended = true,
            notes = "Direct farmer-to-consumer market. Zero commission. Peak buyers between 6:30 AM - 10:00 AM."
        ),
        MarketOption(
            id = "market_ottanchathiram",
            name = "Ottanchathiram Wholesale Mandi",
            distanceKm = 65,
            ratePerKg = 29,
            commissionPercent = 4,
            transportCostEst = 3800,
            demandLevel = "Very High (Bulk)",
            netPayoutEst = 142000,
            isRecommended = false,
            notes = "Major regional terminal mandi. Accepts entire multi-tonne harvest in one transaction. Commission: 4%."
        ),
        MarketOption(
            id = "market_fpo_local",
            name = "Local Agri Producer FPO (Pollachi)",
            distanceKm = 5,
            ratePerKg = 27,
            commissionPercent = 0,
            transportCostEst = 400,
            demandLevel = "Moderate",
            netPayoutEst = 140400,
            isRecommended = false,
            notes = "Farm gate aggregation center. Zero transit loss, immediate electronic bank payment within 24 hours."
        )
    )

    val markets = if (cropContext.marketOptions.isNotEmpty()) cropContext.marketOptions else defaultMarkets

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 18.dp, vertical = 12.dp)
    ) {
        PageHeader(
            stageTag = st("STAGE 06 • SELL"),
            title = st("Smart Selling & Markets"),
            subtitle = st("Direct farmer-buyer linkage & net price transparency"),
            onBack = onBack
        )

        // Hand-drawn Illustration Hero Card: Selling & Handshake
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
                        painter = painterResource(id = R.drawable.img_smart_selling),
                        contentDescription = "Indian farmer with tomato basket shaking hands with market buyer",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "“${st("Selling")}”",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = AgriForestGreen
                )
                Text(
                    text = "“${st("Where should I sell it?")}”",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = AgriTextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = st("Evaluate distance, fuel & transport, middleman commissions, and daily price trends before dispatching harvest."),
                    style = MaterialTheme.typography.bodySmall,
                    color = AgriTextSecondary,
                    fontSize = 12.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Visual Storytelling Flow: Farm -> Basket -> Transport -> Market
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = AgriWarmBg,
            border = androidx.compose.foundation.BorderStroke(1.dp, AgriLineSubtle),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("🌱", fontSize = 18.sp)
                    Text(st("Farm Harvest"), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = AgriForestGreen)
                }
                Text("→", color = AgriTextSecondary)
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("🧺", fontSize = 18.sp)
                    Text(st("Grade & Pack"), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = AgriForestGreen)
                }
                Text("→", color = AgriTextSecondary)
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("🚚", fontSize = 18.sp)
                    Text(st("Direct Transit"), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = AgriForestGreen)
                }
                Text("→", color = AgriTextSecondary)
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("🤝", fontSize = 18.sp)
                    Text(st("Fair Mandi Sale"), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = AgriForestGreen)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = st("REGIONAL MARKET COMPARISON (COIMBATORE REGION)"),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = AgriTextSecondary,
            letterSpacing = 0.5.sp
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Market Cards List
        markets.forEach { market ->
            val isSelected = market.id == selectedMarketId
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = if (isSelected) AgriCardSurface else AgriWarmBg),
                border = androidx.compose.foundation.BorderStroke(
                    if (isSelected) 1.5.dp else 1.dp,
                    if (isSelected) AgriForestGreen else AgriLineSubtle
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp)
                    .clickable { selectedMarketId = market.id }
                    .testTag("market_card_${market.id}")
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = st(market.name),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = AgriTextPrimary
                                )
                                if (market.isRecommended) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        color = AgriForestGreen,
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            text = st("Best Net Value"),
                                            color = Color.White,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                            Text(
                                text = "${market.distanceKm} km away • Demand: ${st(market.demandLevel)}",
                                style = MaterialTheme.typography.bodySmall,
                                fontSize = 11.sp,
                                color = AgriTextSecondary
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "₹${market.ratePerKg}/kg",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.ExtraBold,
                                color = AgriForestGreen
                            )
                            Text(
                                text = if (market.commissionPercent == 0) st("0% Commission") else "${market.commissionPercent}% Comm.",
                                fontSize = 10.sp,
                                color = if (market.commissionPercent == 0) AgriForestGreen else AgriTomatoRed,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(AgriSageLight.copy(alpha = 0.5f))
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("${st("Est. Transport")}: ₹${market.transportCostEst}", fontSize = 11.sp, color = AgriTextSecondary)
                        Text(
                            text = "${st("Est. Net Payout")}: ₹${market.netPayoutEst}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = AgriForestGreen
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = st(market.notes),
                        fontSize = 11.sp,
                        color = AgriTextSecondary,
                        lineHeight = 15.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Practical Selling Checklist
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = AgriCardSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, AgriLineSubtle),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = st("PRACTICAL SELLING CHECKLIST"),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = AgriTextSecondary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "1. ${st("Uzhavar Sandhai gate opens 5:00 AM; arrive before 6:00 AM for prime seller stall.")}\n2. ${st("Ensure Government Farmer ID card is renewed for free entry & electronic scale allotment.")}\n3. ${st("Bring clean plastic crates; loose gunny packing results in 7-10% transit squashing.")}",
                    fontSize = 12.sp,
                    color = AgriTextPrimary,
                    lineHeight = 18.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
