package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Storefront
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.CropProfile
import com.example.data.MarketOption
import com.example.ui.components.SubtleFarmBackground
import com.example.ui.theme.AgriCardSurface
import com.example.ui.theme.AgriEarthBrown
import com.example.ui.theme.AgriForestGreen
import com.example.ui.theme.AgriLineSubtle
import com.example.ui.theme.AgriSageGreen
import com.example.ui.theme.AgriSageLight
import com.example.ui.theme.AgriSurfaceSubtle
import com.example.ui.theme.AgriTextPrimary
import com.example.ui.theme.AgriTextSecondary
import com.example.ui.theme.AgriWarmBg
import com.example.ui.theme.AgriWarmClay

@Composable
fun SmartSellingScreen(
    cropProfile: CropProfile?,
    markets: List<MarketOption>,
    onBack: () -> Unit,
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
                    modifier = Modifier.testTag("selling_back_button")
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
                        text = "06 SELL",
                        style = MaterialTheme.typography.labelSmall,
                        color = AgriForestGreen,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Smart Selling & Markets",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = AgriTextPrimary
                    )
                }
            }

            // Hand-drawn illustration: Indian farmer carrying basket of tomatoes + buyer with subtle handshake
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = AgriCardSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, AgriLineSubtle),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("smart_selling_illustration_card")
            ) {
                Column {
                    Image(
                        painter = painterResource(id = R.drawable.img_smart_selling),
                        contentDescription = "Indian farmer carrying basket of harvested tomatoes meeting buyer with subtle handshake",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(210.dp)
                            .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)),
                        contentScale = ContentScale.Crop
                    )

                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Selling",
                            style = MaterialTheme.typography.labelSmall,
                            color = AgriEarthBrown,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "“Where should I sell it?”",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = AgriTextPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Connecting harvest directly to remunerative agricultural channels with verified logistics costs.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = AgriTextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Storytelling Flow: Farm → Harvest basket → Transport → Market
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = AgriCardSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, AgriLineSubtle),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("selling_storytelling_flow")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "The Harvest-to-Market Journey",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = AgriTextPrimary
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val steps = listOf(
                            "🌱 Farm" to "Coimbatore (2 Ac)",
                            "🧺 Basket" to "Cratered Breaker",
                            "🚚 Transport" to "Light Mini-truck",
                            "🏛️ Market" to "Direct / Mandi"
                        )

                        steps.forEachIndexed { idx, (stepName, subtext) ->
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = stepName,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = AgriForestGreen
                                )
                                Text(
                                    text = subtext,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontSize = 11.sp,
                                    color = AgriTextSecondary
                                )
                            }
                            if (idx < steps.size - 1) {
                                Text(
                                    text = "→",
                                    fontWeight = FontWeight.Bold,
                                    color = AgriForestGreen.copy(alpha = 0.6f)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            // Section Headline
            Text(
                text = "Market Options & Rates",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = AgriTextPrimary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Clean comparison factoring per-kg distance and transport overhead.",
                style = MaterialTheme.typography.bodySmall,
                color = AgriTextSecondary
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Clean list rather than large card grid
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("market_options_list"),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                markets.forEach { market ->
                    val netRealization = market.pricePerKg - market.transportCostNum
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = AgriCardSurface,
                        border = androidx.compose.foundation.BorderStroke(1.dp, AgriLineSubtle),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Storefront,
                                        contentDescription = null,
                                        tint = AgriForestGreen,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = market.marketName,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = AgriTextPrimary
                                    )
                                }

                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = AgriSageLight
                                ) {
                                    Text(
                                        text = market.suitabilityBadge,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = AgriForestGreen,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Market data row: Distance, Price, Demand, Transport
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text("Distance", style = MaterialTheme.typography.bodySmall, color = AgriTextSecondary)
                                    Text("${market.distanceKm} km", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = AgriTextPrimary)
                                }
                                Column {
                                    Text("Market Price", style = MaterialTheme.typography.bodySmall, color = AgriTextSecondary)
                                    Text("₹${market.pricePerKg}/kg", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = AgriForestGreen)
                                }
                                Column {
                                    Text("Demand", style = MaterialTheme.typography.bodySmall, color = AgriTextSecondary)
                                    Text(market.demandLevel, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = AgriTextPrimary)
                                }
                                Column {
                                    Text("Transport", style = MaterialTheme.typography.bodySmall, color = AgriTextSecondary)
                                    Text(market.transportCost, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = AgriTextPrimary)
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Net realization & recommendation note
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(AgriSurfaceSubtle, RoundedCornerShape(8.dp))
                                    .padding(horizontal = 10.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = market.recommendationNote,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontSize = 11.sp,
                                    color = AgriTextSecondary,
                                    modifier = Modifier.weight(1f)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Net: ₹${"%.1f".format(netRealization)}/kg",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AgriForestGreen
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Guidance Note
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = AgriSurfaceSubtle,
                border = androidx.compose.foundation.BorderStroke(1.dp, AgriLineSubtle),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Market principle",
                        tint = AgriForestGreen,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "“Market suitability considers price, demand and transportation.” Selling at the highest sticker quote does not always yield the highest net cash if transport and mandi deductions are high.",
                        style = MaterialTheme.typography.bodySmall,
                        color = AgriTextSecondary,
                        lineHeight = 18.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}
