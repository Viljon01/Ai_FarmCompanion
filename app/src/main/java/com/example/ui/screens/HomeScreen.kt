package com.example.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Badge
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.CropProfile
import com.example.data.WeatherConditionData
import com.example.data.localization.st
import com.example.data.localization.t
import com.example.ui.components.AgriWeatherIllustration
import com.example.ui.components.FarmerNeedAgriIcon
import com.example.ui.components.LeafAiSparkIcon
import com.example.ui.components.SubtleFarmBackground
import com.example.ui.components.VisualJourneyTimeline
import com.example.ui.theme.AgriCardSurface
import com.example.ui.theme.AgriEarthBrown
import com.example.ui.theme.AgriForestGreen
import com.example.ui.theme.AgriGoldenOchre
import com.example.ui.theme.AgriLineSubtle
import com.example.ui.theme.AgriPaleYellow
import com.example.ui.theme.AgriSageGreen
import com.example.ui.theme.AgriSageLight
import com.example.ui.theme.AgriSkyLight
import com.example.ui.theme.AgriSurfaceSubtle
import com.example.ui.theme.AgriTextPrimary
import com.example.ui.theme.AgriTextSecondary
import com.example.ui.theme.AgriTomatoLight
import com.example.ui.theme.AgriTomatoRed
import com.example.ui.theme.AgriWarmBg
import com.example.ui.theme.AgriWarningAmber
import com.example.ui.theme.AgriWarningLight

@Composable
fun HomeScreen(
    cropProfile: CropProfile?,
    weather: WeatherConditionData,
    activeStageIndex: Int,
    onNavigateStage: (Int) -> Unit,
    onNavigateWeather: () -> Unit,
    onOpenCompanion: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    // Gentle pulse for AI Companion action
    val infiniteTransition = rememberInfiniteTransition(label = "pulse_ai")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(AgriWarmBg)
    ) {
        // Subtle decorative background
        SubtleFarmBackground()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            // Header Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "${st("Good morning")}, ${st("Farmer")} 👋",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = AgriTextPrimary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = st("Your crop journey is on track."),
                        style = MaterialTheme.typography.bodyLarge,
                        color = AgriForestGreen,
                        fontWeight = FontWeight.Medium
                    )
                }

                // AI Assistant Button (Leaf + Subtle Spark symbol)
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = AgriCardSurface,
                    border = androidx.compose.foundation.BorderStroke(1.2.dp, AgriForestGreen.copy(alpha = 0.4f)),
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .clickable { onOpenCompanion() }
                        .testTag("ai_companion_fab")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp)
                    ) {
                        LeafAiSparkIcon(size = 20.dp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = st("Ask AI"),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = AgriForestGreen
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Small, Non-intrusive Agricultural Weather Card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = AgriCardSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, AgriLineSubtle),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateWeather() }
                    .testTag("agricultural_weather_card_home")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(AgriSageLight),
                                contentAlignment = Alignment.Center
                            ) {
                                AgriWeatherIllustration(
                                    weatherType = weather.sceneType,
                                    size = 26.dp
                                )
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "${weather.temperatureC}°C",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = AgriTextPrimary
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "• ${weather.conditionTitle}",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontSize = 11.5.sp,
                                        color = AgriTextSecondary,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                                Text(
                                    text = "${st("Humidity")}: ${weather.humidityPercent}% • ${st("Wind")}: ${weather.windSpeedKmh} km/h • ${st("Rain")}: ${weather.rainfallChance}%",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontSize = 10.5.sp,
                                    color = AgriTextSecondary
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = AgriPaleYellow,
                            border = androidx.compose.foundation.BorderStroke(0.8.dp, AgriGoldenOchre.copy(alpha = 0.5f))
                        ) {
                            Text(
                                text = st("Planning Tip"),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = AgriEarthBrown,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Planning Advisory Snippet
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(AgriSurfaceSubtle)
                            .padding(horizontal = 8.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Eco,
                            contentDescription = null,
                            tint = AgriForestGreen,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Agri Advice: ${weather.recommendation}",
                            fontSize = 11.sp,
                            color = AgriTextPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Open weather details",
                            tint = AgriTextSecondary,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Large Agricultural Illustration
            // "An Indian farmer standing beside a healthy tomato field, holding a smartphone and observing the crop."
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = AgriCardSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, AgriLineSubtle),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("hero_farmer_illustration_card")
            ) {
                Column {
                    Image(
                        painter = painterResource(id = R.drawable.img_farmer_hero),
                        contentDescription = "Indian farmer observing healthy tomato crop with smartphone in Coimbatore field",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(210.dp)
                            .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)),
                        contentScale = ContentScale.Crop
                    )

                    // Integrated short caption and context tag
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(AgriForestGreen, CircleShape)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = st("Field Observation • Active Sync"),
                                style = MaterialTheme.typography.bodySmall,
                                color = AgriForestGreen,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Text(
                            text = "${st("Day")} 34 ${st("of")} 85",
                            style = MaterialTheme.typography.bodySmall,
                            color = AgriTextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // CURRENT CROP Card
            // "🍅 Tomato | 2 Acres • Coimbatore | Health: 86% Healthy"
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = AgriCardSurface),
                border = androidx.compose.foundation.BorderStroke(1.2.dp, AgriLineSubtle),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("current_crop_card")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Text(
                        text = st("CURRENT CROP"),
                        style = MaterialTheme.typography.labelSmall,
                        color = AgriTextSecondary,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "🍅",
                                fontSize = 28.sp,
                                modifier = Modifier.padding(end = 10.dp)
                            )
                            Column {
                                Text(
                                    text = st(cropProfile?.cropName ?: "Tomato"),
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = AgriTextPrimary
                                )
                                Text(
                                    text = "${cropProfile?.acres ?: 2.0} ${st("Acres")} • ${st(cropProfile?.location ?: "Coimbatore")}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = AgriTextSecondary
                                )
                            }
                        }

                        // Health percentage badge
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = AgriSageLight,
                            border = androidx.compose.foundation.BorderStroke(1.dp, AgriForestGreen.copy(alpha = 0.3f))
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = st("Health"),
                                    fontSize = 10.sp,
                                    color = AgriForestGreen,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = "${cropProfile?.healthScore ?: 86}%",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AgriForestGreen
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Health Progress Bar
                    LinearProgressIndicator(
                        progress = { (cropProfile?.healthScore ?: 86) / 100f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(7.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = AgriForestGreen,
                        trackColor = AgriSageLight,
                        strokeCap = StrokeCap.Round
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = st("Condition: Good vegetative canopy"),
                            style = MaterialTheme.typography.bodySmall,
                            color = AgriTextSecondary
                        )
                        Text(
                            text = "86% ${st("Healthy")}",
                            style = MaterialTheme.typography.bodySmall,
                            color = AgriForestGreen,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Weather Snapshot Bar (Navigates to Weather Screen)
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = AgriCardSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, AgriLineSubtle),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { onNavigateWeather() }
                    .testTag("weather_preview_card")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        AgriWeatherIllustration(
                            weatherType = "humidity",
                            size = 46.dp
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "${weather.temperatureC}°C",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = AgriTextPrimary
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "• Humidity ${weather.humidityPercent}%",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = AgriTextSecondary
                                )
                            }
                            Text(
                                text = "Rain Expected • Inspect lower leaves",
                                style = MaterialTheme.typography.bodySmall,
                                color = AgriWarningAmber,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "View Weather Details",
                        tint = AgriTextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // CROP JOURNEY SECTION
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "THE CROP JOURNEY",
                        style = MaterialTheme.typography.labelSmall,
                        color = AgriTextSecondary,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Connected Lifecycle Guidance",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = AgriTextPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Connected Visual Journey Timeline
            VisualJourneyTimeline(
                activeStageIndex = activeStageIndex,
                onStageSelected = { onNavigateStage(it) }
            )

            Spacer(modifier = Modifier.height(28.dp))

            // "WHAT FARMERS NEED" SECTION
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("what_farmers_need_section")
            ) {
                Text(
                    text = "SUPPORTING THE GROWER",
                    style = MaterialTheme.typography.labelSmall,
                    color = AgriTextSecondary,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "What Farmers Need",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = AgriTextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Practical, human-centered technology built around real agricultural needs on the ground.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = AgriTextSecondary
                )

                Spacer(modifier = Modifier.height(14.dp))

                val farmerNeeds = listOf(
                    Triple("guidance", "Simple & understandable guidance", "No complex jargon. Clear day-by-day steps from nursery to harvest."),
                    Triple("location", "Location-specific information", "Soil parameters, local climate and mandi intelligence tailored to Coimbatore."),
                    Triple("early_warning", "Early warnings", "Timely alert before humidity triggers early blight or fungal spreading."),
                    Triple("cost_profit", "Cost & profit visibility", "Transparent accounting of seeds, drip, labor vs potential mandi returns."),
                    Triple("selling", "Practical selling support", "Direct market connections comparing Uzhavar Sandhai vs Mandi rates.")
                )

                farmerNeeds.forEach { (iconKey, title, desc) ->
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = AgriCardSurface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, AgriLineSubtle),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 5.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            FarmerNeedAgriIcon(
                                needType = iconKey,
                                size = 42.dp
                            )

                            Spacer(modifier = Modifier.width(14.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = title,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = AgriTextPrimary
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = desc,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = AgriTextSecondary
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(36.dp))
        }
    }
}
