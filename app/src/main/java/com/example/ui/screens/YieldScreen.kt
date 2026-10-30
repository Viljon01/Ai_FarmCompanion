package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.CropProfile
import com.example.ui.components.SubtleFarmBackground
import com.example.ui.theme.AgriCardSurface
import com.example.ui.theme.AgriEarthBrown
import com.example.ui.theme.AgriForestGreen
import com.example.ui.theme.AgriLineSubtle
import com.example.ui.theme.AgriSageLight
import com.example.ui.theme.AgriSurfaceSubtle
import com.example.ui.theme.AgriTextPrimary
import com.example.ui.theme.AgriTextSecondary
import com.example.ui.theme.AgriTomatoLight
import com.example.ui.theme.AgriTomatoRed
import com.example.ui.theme.AgriWarmBg
import com.example.ui.theme.AgriWarmClay

@Composable
fun YieldScreen(
    cropProfile: CropProfile?,
    onBack: () -> Unit,
    onNavigateSelling: () -> Unit,
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
                    modifier = Modifier.testTag("yield_back_button")
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
                        text = "05 HARVEST",
                        style = MaterialTheme.typography.labelSmall,
                        color = AgriForestGreen,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Yield & Financial Estimate",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = AgriTextPrimary
                    )
                }
            }

            // Hand-drawn illustration: Indian farmer standing beside a mature tomato crop examining harvest
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = AgriCardSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, AgriLineSubtle),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("before_harvest_illustration_card")
            ) {
                Column {
                    Image(
                        painter = painterResource(id = R.drawable.img_before_harvest),
                        contentDescription = "Indian farmer examining healthy mature tomato crop in field thinking about yield",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(210.dp)
                            .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)),
                        contentScale = ContentScale.Crop
                    )

                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Before Harvest",
                            style = MaterialTheme.typography.labelSmall,
                            color = AgriEarthBrown,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "“Will I get a good yield?”",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = AgriTextPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Projected output based on plant density (4,800 plants across 2 acres) and historical flowering fruit-set rates.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = AgriTextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Primary Financial & Yield Cards (All explicitly labeled as estimates)
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = AgriCardSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, AgriLineSubtle),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("yield_estimates_card")
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "FIELD HARVEST FORECAST",
                            style = MaterialTheme.typography.labelSmall,
                            color = AgriTextSecondary,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = AgriSurfaceSubtle
                        ) {
                            Text(
                                text = "All values are estimates",
                                fontSize = 11.sp,
                                color = AgriTextSecondary,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Expected Yield
                    Column {
                        Text(
                            text = "Expected Yield (Estimate)",
                            style = MaterialTheme.typography.bodySmall,
                            color = AgriTextSecondary
                        )
                        Text(
                            text = "5.2 – 6.1 tonnes",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = AgriForestGreen
                        )
                        Text(
                            text = "Calculated for 2 Acres in Coimbatore region",
                            style = MaterialTheme.typography.bodySmall,
                            color = AgriTextSecondary
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(AgriLineSubtle))
                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Estimated Cost
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Estimated Cost",
                                style = MaterialTheme.typography.bodySmall,
                                color = AgriTextSecondary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "₹42,000",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = AgriTextPrimary
                            )
                            Text(
                                text = "Seeds, Drip, Labor & Organic Feed",
                                style = MaterialTheme.typography.bodySmall,
                                fontSize = 11.sp,
                                color = AgriTextSecondary
                            )
                        }

                        // Potential Revenue
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Potential Revenue",
                                style = MaterialTheme.typography.bodySmall,
                                color = AgriTextSecondary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "₹1,40,000 – ₹1,85,000",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = AgriWarmClay
                            )
                            Text(
                                text = "Based on ₹28 - ₹36/kg Mandi trend",
                                style = MaterialTheme.typography.bodySmall,
                                fontSize = 11.sp,
                                color = AgriTextSecondary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Net Margin Estimation Banner
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = AgriSageLight,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Estimated Net Return:",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                                color = AgriForestGreen
                            )
                            Text(
                                text = "₹98,000 – ₹1,43,000",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = AgriForestGreen
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Harvesting Guidance Card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = AgriCardSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, AgriLineSubtle),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Optimal Picking Guidance",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = AgriTextPrimary
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    val tips = listOf(
                        "Breaker Stage Picking" to "Harvest when the blossom end turns slightly pink/orange. This extends market shelf life by 4-5 days during transport.",
                        "Morning Harvest Timing" to "Pick between 6:00 AM – 10:00 AM while fruit internal temperature is cool.",
                        "Gentle Twist Detachment" to "Retain the green calyx on the fruit to reduce entry vectors for post-harvest rots."
                    )

                    tips.forEach { (title, desc) ->
                        Column(modifier = Modifier.padding(vertical = 4.dp)) {
                            Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, color = AgriForestGreen)
                            Text(desc, style = MaterialTheme.typography.bodySmall, color = AgriTextSecondary)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Button to 06 SELL (Smart Selling Screen)
            Button(
                onClick = onNavigateSelling,
                colors = ButtonDefaults.buttonColors(containerColor = AgriForestGreen),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("yield_to_selling_button")
            ) {
                Text("Proceed to 06 SELL & Market Intelligence", fontWeight = FontWeight.SemiBold)
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
