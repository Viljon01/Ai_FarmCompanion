package com.example.features.planning

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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
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

data class CandidateCropOption(
    val id: String,
    val name: String,
    val emoji: String,
    val variety: String,
    val suitabilityBadge: String,
    val suitabilityColor: Color,
    val durationDays: String,
    val estCost: String,
    val estYield: String,
    val estReturn: String,
    val reasoning: String,
    val isRisky: Boolean = false,
    val riskWarning: String? = null
)

/**
 * FEATURE 04 — CONVERSATIONAL CROP PLANNER
 * Interactive crop evaluation & decision support.
 * Farmers conclude for themselves among suitable crops or check custom crops.
 */
@Composable
fun CropPlannerScreen(
    cropContext: CropContext,
    onBack: () -> Unit,
    onProceedToJourney: () -> Unit,
    onConfirmPlantCrop: (cropName: String, variety: String, acres: Double) -> Unit,
    onAskAiSuitability: (cropQuery: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    var locationInput by remember { mutableStateOf(cropContext.farm.location) }
    var landSizeInput by remember { mutableStateOf("${cropContext.farm.landSizeAcres}") }
    var seasonInput by remember { mutableStateOf("Kharif / Current Season") }
    var waterInput by remember { mutableStateOf(cropContext.farm.waterAvailability) }
    var soilInput by remember { mutableStateOf(cropContext.farm.soilType) }

    var customCropQuery by remember { mutableStateOf("") }
    var customCropCheckResult by remember { mutableStateOf<String?>(null) }

    val acresVal = landSizeInput.filter { it.isDigit() || it == '.' }.toDoubleOrNull() ?: 2.0

    val suitableCrops = remember(acresVal) {
        listOf(
            CandidateCropOption(
                id = "tomato",
                name = "Tomato",
                emoji = "🍅",
                variety = "Shivam / Pusa Ruby",
                suitabilityBadge = "High Suitability",
                suitabilityColor = AgriForestGreen,
                durationDays = "75 – 85 Days",
                estCost = "₹${(acresVal * 21000).toInt()}",
                estYield = "${(acresVal * 2.8).toInt()} – ${(acresVal * 3.5).toInt()} Tonnes",
                estReturn = "₹${(acresVal * 48000).toInt()} – ₹${(acresVal * 72000).toInt()}",
                reasoning = "Excellent match for well-drained ${cropContext.farm.soilType} and ${cropContext.farm.waterAvailability}. Strong Mandi demand in local markets."
            ),
            CandidateCropOption(
                id = "chilli",
                name = "Chilli / Pepper",
                emoji = "🌶️",
                variety = "Guntur / Teja",
                suitabilityBadge = "High Suitability",
                suitabilityColor = AgriForestGreen,
                durationDays = "110 – 130 Days",
                estCost = "₹${(acresVal * 18000).toInt()}",
                estYield = "${(acresVal * 1.5).toInt()} – ${(acresVal * 2.2).toInt()} Tonnes",
                estReturn = "₹${(acresVal * 60000).toInt()} – ₹${(acresVal * 95000).toInt()}",
                reasoning = "High profit margin crop for red loam soil with drip irrigation. Monitor early for leaf curl thrips."
            ),
            CandidateCropOption(
                id = "maize",
                name = "Maize / Corn",
                emoji = "🌽",
                variety = "NK-6240 / Hybrid",
                suitabilityBadge = "Moderate Suitability",
                suitabilityColor = AgriGoldenOchre,
                durationDays = "90 – 100 Days",
                estCost = "₹${(acresVal * 14000).toInt()}",
                estYield = "${(acresVal * 3.0).toInt()} – ${(acresVal * 3.8).toInt()} Tonnes",
                estReturn = "₹${(acresVal * 35000).toInt()} – ₹${(acresVal * 55000).toInt()}",
                reasoning = "Low input cost with reliable feed mill off-take. Good alternative if water is restricted."
            ),
            CandidateCropOption(
                id = "sugarcane",
                name = "Sugarcane",
                emoji = "🎋",
                variety = "Co-86032",
                suitabilityBadge = "High Risk / Caution",
                suitabilityColor = AgriTomatoRed,
                durationDays = "360 – 400 Days",
                estCost = "₹${(acresVal * 38000).toInt()}",
                estYield = "${(acresVal * 35).toInt()} – ${(acresVal * 45).toInt()} Tonnes",
                estReturn = "₹${(acresVal * 80000).toInt()} – ₹${(acresVal * 110000).toInt()}",
                reasoning = "Requires very high water (1500+ mm) and 12+ months land lock-in. Unsuitable if borewell water fluctuates during summer months.",
                isRisky = true,
                riskWarning = "High Water & Duration Constraint: Exceeds moderate drip borewell capacity and locks field for a full year."
            )
        )
    }

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
                .padding(horizontal = 18.dp, vertical = 12.dp)
        ) {
            PageHeader(
                stageTag = st("01 PLAN"),
                title = st("Crop Decision Support"),
                subtitle = st("Evaluate suitable crops & select what you decide to plant"),
                onBack = onBack
            )

            // Hero Card
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = AgriCardSurface),
                border = BorderStroke(1.dp, AgriLineSubtle),
                modifier = Modifier.fillMaxWidth().testTag("crop_planner_hero_card")
            ) {
                Column {
                    Image(
                        painter = painterResource(id = R.drawable.img_before_cultivation),
                        contentDescription = "Farmer evaluating crop suitability",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                            .clip(RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp)),
                        contentScale = ContentScale.Crop
                    )

                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = st("Interactive Decision Support"),
                            style = MaterialTheme.typography.labelSmall,
                            color = AgriEarthBrown,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "“${st("Which crop suits my field best?")}”",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = AgriTextPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = st("We evaluate multiple crop options based on your land, water, and soil. You decide and select what you actually plant!"),
                            style = MaterialTheme.typography.bodySmall,
                            color = AgriTextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Planner Inputs
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = AgriCardSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, AgriLineSubtle),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = st("Your Field & Resource Parameters"),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = AgriTextPrimary
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = locationInput,
                            onValueChange = { locationInput = it },
                            label = { Text(st("Location"), maxLines = 1, overflow = TextOverflow.Ellipsis) },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        )

                        OutlinedTextField(
                            value = landSizeInput,
                            onValueChange = { landSizeInput = it },
                            label = { Text(st("Acres"), maxLines = 1, overflow = TextOverflow.Ellipsis) },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(0.8f)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = soilInput,
                            onValueChange = { soilInput = it },
                            label = { Text(st("Soil Type"), maxLines = 1, overflow = TextOverflow.Ellipsis) },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        )

                        OutlinedTextField(
                            value = waterInput,
                            onValueChange = { waterInput = it },
                            label = { Text(st("Water Source"), maxLines = 1, overflow = TextOverflow.Ellipsis) },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Conversational Plant Check Box
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = AgriSageLight),
                border = androidx.compose.foundation.BorderStroke(1.dp, AgriForestGreen.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "💬 " + st("Check Any Custom Plant Suitability"),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = AgriForestGreen
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = st("Want to grow Wheat, Sugarcane, Pepper, or Cotton? Ask the AI to evaluate why it's suitable or why it carries risk:"),
                        style = MaterialTheme.typography.bodySmall,
                        color = AgriTextSecondary,
                        fontSize = 11.5.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = customCropQuery,
                            onValueChange = { customCropQuery = it },
                            placeholder = { Text(st("Type plant name (e.g. Sugarcane, Wheat)..."), fontSize = 12.sp) },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f),
                            maxLines = 1
                        )

                        Button(
                            onClick = {
                                if (customCropQuery.isNotBlank()) {
                                    onAskAiSuitability(customCropQuery)
                                    customCropCheckResult = "AI Decision Evaluation sent for $customCropQuery. Check AI response below or in Assistant sheet."
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = AgriForestGreen),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Icon(Icons.Default.Search, contentDescription = "Check", tint = Color.White)
                        }
                    }

                    if (customCropCheckResult != null) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = customCropCheckResult!!,
                            fontSize = 11.sp,
                            color = AgriForestGreen,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Suitable Crop Candidate Cards
            Text(
                text = st("Suitable Crop Candidates (Compare & Choose)"),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = AgriTextPrimary
            )

            Spacer(modifier = Modifier.height(8.dp))

            suitableCrops.forEach { crop ->
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (crop.isRisky) AgriPaleYellow else AgriCardSurface
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        1.2.dp,
                        if (crop.isRisky) AgriTomatoRed.copy(alpha = 0.5f) else AgriLineSubtle
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(crop.emoji, fontSize = 26.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = crop.name,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = AgriTextPrimary
                                    )
                                    Text(
                                        text = crop.variety,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = AgriTextSecondary
                                    )
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = crop.suitabilityColor.copy(alpha = 0.15f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, crop.suitabilityColor.copy(alpha = 0.4f))
                            ) {
                                Text(
                                    text = crop.suitabilityBadge,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = crop.suitabilityColor,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = crop.reasoning,
                            style = MaterialTheme.typography.bodySmall,
                            color = AgriTextPrimary,
                            lineHeight = 16.sp
                        )

                        if (crop.riskWarning != null) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = AgriTomatoRed.copy(alpha = 0.1f),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "⚠️ " + crop.riskWarning,
                                    fontSize = 11.sp,
                                    color = AgriTomatoRed,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(8.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Economics Grid
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(st("Duration"), fontSize = 10.sp, color = AgriTextSecondary)
                                Text(crop.durationDays, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AgriTextPrimary)
                            }
                            Column {
                                Text(st("Est. Cost"), fontSize = 10.sp, color = AgriTextSecondary)
                                Text(crop.estCost, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AgriTomatoRed)
                            }
                            Column {
                                Text(st("Est. Return"), fontSize = 10.sp, color = AgriTextSecondary)
                                Text(crop.estReturn, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AgriForestGreen)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Confirm & Plant Button
                        Button(
                            onClick = {
                                onConfirmPlantCrop(crop.name, crop.variety, acresVal)
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (crop.isRisky) AgriEarthBrown else AgriForestGreen
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = st("Confirm & Plant ${crop.name}"),
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = Color.White
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}
