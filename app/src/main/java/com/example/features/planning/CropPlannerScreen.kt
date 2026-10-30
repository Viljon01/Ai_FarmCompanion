package com.example.features.planning

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
 * FEATURE 04 — CROP PLANNER
 * Input parameters -> Recommended Crop with high suitability and agronomic rationale
 */
@Composable
fun CropPlannerScreen(
    cropContext: CropContext,
    onBack: () -> Unit,
    onProceedToJourney: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    var locationInput by remember { mutableStateOf(cropContext.farm.location) }
    var landSizeInput by remember { mutableStateOf("${cropContext.farm.landSizeAcres} Acres") }
    var seasonInput by remember { mutableStateOf("Current Season (Kharif/Rabi)") }
    var waterInput by remember { mutableStateOf("Moderate Water (Borewell + Drip)") }
    var soilInput by remember { mutableStateOf(cropContext.farm.soilType) }

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
                stageTag = st("01 PLAN"),
                title = st("Plan Your Crop"),
                subtitle = st("Soil suitability, seasonal water & crop recommendation"),
                onBack = onBack
            )

            // Hand-drawn illustration: Indian farmer in empty farmland planning crop
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = AgriCardSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, AgriLineSubtle),
                modifier = Modifier.fillMaxWidth().testTag("crop_planner_illustration_card")
            ) {
                Column {
                    Image(
                        painter = painterResource(id = R.drawable.img_before_cultivation),
                        contentDescription = "Indian farmer planning in empty farmland",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clip(RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp)),
                        contentScale = ContentScale.Crop
                    )

                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = st("Before Cultivation"),
                            style = MaterialTheme.typography.labelSmall,
                            color = AgriEarthBrown,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "“${st("Which crop should I grow?")}”",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = AgriTextPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = st("Assessing your field parameters to match regional pest resilience and Mandi offtake."),
                            style = MaterialTheme.typography.bodySmall,
                            color = AgriTextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Planner Inputs Card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = AgriCardSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, AgriLineSubtle),
                modifier = Modifier.fillMaxWidth().testTag("crop_planner_inputs_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = st("Field & Resource Inputs"),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = AgriTextPrimary
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = locationInput,
                        onValueChange = { locationInput = it },
                        label = { Text(st("Location")) },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = landSizeInput,
                            onValueChange = { landSizeInput = it },
                            label = { Text(st("Land Size")) },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = seasonInput,
                            onValueChange = { seasonInput = it },
                            label = { Text(st("Season")) },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1.5f)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = waterInput,
                        onValueChange = { waterInput = it },
                        label = { Text(st("Water Availability")) },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = soilInput,
                        onValueChange = { soilInput = it },
                        label = { Text(st("Soil Type")) },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Recommended Crop Card (Crucial: labeled as recommendation, not guarantee)
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = AgriSageLight),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, AgriForestGreen),
                modifier = Modifier.fillMaxWidth().testTag("recommended_crop_card")
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = st("RECOMMENDED CROP"),
                            style = MaterialTheme.typography.labelSmall,
                            color = AgriForestGreen,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = AgriForestGreen
                        ) {
                            Text(
                                text = st("Suitability: High"),
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "🍅", fontSize = 32.sp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Tomato (Shivam / Pusa Ruby)",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = AgriTextPrimary
                            )
                            Text(
                                text = st("Growing Period: 75 – 85 Days"),
                                style = MaterialTheme.typography.bodyMedium,
                                color = AgriForestGreen,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Explicit reason per requirement
                    Text(
                        text = st("Reason: “Recommended based on the available location (Coimbatore), season, water availability and red sandy loam soil information.”"),
                        style = MaterialTheme.typography.bodySmall,
                        color = AgriTextPrimary,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Estimated Economics
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(st("Estimated Cost"), style = MaterialTheme.typography.bodySmall, color = AgriTextSecondary)
                            Text("₹42,000", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = AgriTextPrimary)
                        }
                        Column {
                            Text(st("Expected Yield"), style = MaterialTheme.typography.bodySmall, color = AgriTextSecondary)
                            Text(st("5.2 – 6.1 Tonnes"), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = AgriForestGreen)
                        }
                        Column {
                            Text(st("Net Return (Est.)"), style = MaterialTheme.typography.bodySmall, color = AgriTextSecondary)
                            Text("₹98k – ₹143k", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = AgriWarmClay)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Non-Guaranteed Disclaimer Notice
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = AgriSurfaceSubtle,
                border = androidx.compose.foundation.BorderStroke(1.dp, AgriLineSubtle),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.Top) {
                    Icon(Icons.Default.Info, contentDescription = null, tint = AgriForestGreen, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = st("Agronomic Notice: Crop suggestions are recommendations based on available inputs and regional historical trends. Local microclimates and pest pressures may vary."),
                        style = MaterialTheme.typography.bodySmall,
                        fontSize = 11.sp,
                        color = AgriTextSecondary,
                        lineHeight = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = onProceedToJourney,
                colors = ButtonDefaults.buttonColors(containerColor = AgriForestGreen),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().height(48.dp).testTag("planner_proceed_button")
            ) {
                Text(st("Start Crop Journey for Tomato"), fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(8.dp))
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}
