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
import androidx.compose.material.icons.filled.CheckCircle
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.ui.components.StageAgriIcon
import com.example.ui.components.SubtleFarmBackground
import com.example.ui.theme.AgriCardSurface
import com.example.ui.theme.AgriEarthBrown
import com.example.ui.theme.AgriForestGreen
import com.example.ui.theme.AgriLineSubtle
import com.example.ui.theme.AgriSageLight
import com.example.ui.theme.AgriSurfaceSubtle
import com.example.ui.theme.AgriTextPrimary
import com.example.ui.theme.AgriTextSecondary
import com.example.ui.theme.AgriWarmBg

@Composable
fun PlanScreen(
    cropProfile: CropProfile?,
    onBack: () -> Unit,
    onProceedToGrow: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }
    val scrollState = rememberScrollState()

    var selectedCrop by remember { mutableStateOf("Tomato") }

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
            // Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp, bottom = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.testTag("plan_back_button")
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
                        text = "01 PLAN",
                        style = MaterialTheme.typography.labelSmall,
                        color = AgriForestGreen,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Crop Planning & Soil Setup",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = AgriTextPrimary
                    )
                }
            }

            // Hand-drawn illustration: Indian farmer standing in empty farmland / crop rows thinking/planning
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = AgriCardSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, AgriLineSubtle),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("before_cultivation_illustration_card")
            ) {
                Column {
                    Image(
                        painter = painterResource(id = R.drawable.img_before_cultivation),
                        contentDescription = "Indian farmer planning in empty farmland with crop rows and gentle hills",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(210.dp)
                            .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)),
                        contentScale = ContentScale.Crop
                    )

                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Before Cultivation",
                            style = MaterialTheme.typography.labelSmall,
                            color = AgriEarthBrown,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "“Which crop should I grow?”",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = AgriTextPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Evaluating soil texture, water availability, and historical market demand in Coimbatore.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = AgriTextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Field Baseline Assessment
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = AgriCardSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, AgriLineSubtle),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Location & Soil Intelligence",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = AgriTextPrimary
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Location", style = MaterialTheme.typography.bodySmall, color = AgriTextSecondary)
                            Text("Coimbatore, TN", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = AgriTextPrimary)
                        }
                        Column {
                            Text("Soil Profile", style = MaterialTheme.typography.bodySmall, color = AgriTextSecondary)
                            Text("Red Sandy Loam (pH 6.8)", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = AgriTextPrimary)
                        }
                        Column {
                            Text("Water Source", style = MaterialTheme.typography.bodySmall, color = AgriTextSecondary)
                            Text("Borewell + Drip", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = AgriTextPrimary)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Crop Suitability Options
            Text(
                text = "Crop Evaluation for This Season",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = AgriTextPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Select a crop to view season requirements and financial estimates.",
                style = MaterialTheme.typography.bodySmall,
                color = AgriTextSecondary
            )

            Spacer(modifier = Modifier.height(12.dp))

            val cropOptions = listOf(
                Triple("Tomato", "High Yield • 75-85 Days", "Estimated Cost: ₹42,000 | Return: ₹1.4L - ₹1.8L (Recommended)"),
                Triple("Chilli", "Extended Harvest • 110-130 Days", "Estimated Cost: ₹55,000 | Return: ₹1.6L - ₹2.1L"),
                Triple("Brinjal", "Resilient • 80-100 Days", "Estimated Cost: ₹38,000 | Return: ₹1.1L - ₹1.4L")
            )

            cropOptions.forEach { (name, cycle, finance) ->
                val isSelected = selectedCrop == name
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) AgriSageLight else AgriCardSurface
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        width = if (isSelected) 1.8.dp else 1.dp,
                        color = if (isSelected) AgriForestGreen else AgriLineSubtle
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 5.dp)
                        .clip(RoundedCornerShape(14.dp))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = if (name == "Tomato") "🍅 $name" else if (name == "Chilli") "🌶️ $name" else "🍆 $name",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = AgriTextPrimary
                                )
                                if (name == "Tomato") {
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Surface(
                                        color = AgriForestGreen,
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            text = "Selected",
                                            color = Color.White,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(cycle, style = MaterialTheme.typography.bodySmall, color = AgriTextSecondary)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(finance, style = MaterialTheme.typography.bodySmall, color = AgriForestGreen, fontWeight = FontWeight.Medium)
                        }

                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Active Crop",
                                tint = AgriForestGreen,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Action to proceed to 02 GROW
            Button(
                onClick = onProceedToGrow,
                colors = ButtonDefaults.buttonColors(containerColor = AgriForestGreen),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("plan_proceed_to_grow_button")
            ) {
                Text("Proceed to 02 GROW Guidance", fontWeight = FontWeight.SemiBold)
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
