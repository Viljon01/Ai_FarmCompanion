package com.example.features.dashboard

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
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
import com.example.data.CropContext
import com.example.data.localization.*
import com.example.ui.components.*
import com.example.ui.theme.*

/**
 * GLOBAL DASHBOARD SCREEN
 * Summarizes the entire crop journey connected to the Crop Context Engine.
 */
@Composable
fun DashboardScreen(
    cropContext: CropContext,
    onNavigate: (AppDestination) -> Unit,
    onStageClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 18.dp, vertical = 12.dp)
    ) {
        if (cropContext.isOffline) {
            OfflineWarningBanner()
        }

        // Greeting Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${t("dashboard.greeting")}, ${cropContext.farmer.name.ifEmpty { t("dashboard.farmerTitle") }} 👋",
                    style = MaterialTheme.typography.titleLarge,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = AgriTextPrimary
                )
                Text(
                    text = t("dashboard.statusOnTrack"),
                    style = MaterialTheme.typography.bodyLarge,
                    color = AgriForestGreen,
                    fontWeight = FontWeight.Medium
                )
            }

            // Quick AI Companion Button
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = AgriCardSurface,
                border = androidx.compose.foundation.BorderStroke(1.2.dp, AgriForestGreen.copy(alpha = 0.4f)),
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .clickable { onNavigate(AppDestination.ASSISTANT) }
                    .testTag("dashboard_ask_ai_pill")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    LeafAiSparkIcon(size = 18.dp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = t("common.assistant"),
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
                .clickable { onNavigate(AppDestination.WEATHER) }
                .testTag("agricultural_weather_card")
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
                                weatherType = cropContext.currentWeather.sceneType,
                                size = 26.dp
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "${cropContext.currentWeather.temperatureC}°C",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = AgriTextPrimary
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "• ${cropContext.currentWeather.conditionTitle}",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontSize = 11.5.sp,
                                    color = AgriTextSecondary,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            Text(
                                text = "Humidity: ${cropContext.currentWeather.humidityPercent}% • Wind: ${cropContext.currentWeather.windSpeedKmh} km/h • Rain: ${cropContext.currentWeather.rainfallChance}%",
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
                            text = t("dashboard.planningTip"),
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
                        text = "${t("dashboard.agriAdvice")}: ${cropContext.currentWeather.recommendation}",
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

        // Large Agricultural Illustration Hero Card
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = AgriCardSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, AgriLineSubtle),
            modifier = Modifier.fillMaxWidth().testTag("dashboard_hero_card")
        ) {
            Column {
                Image(
                    painter = painterResource(id = R.drawable.img_farmer_hero),
                    contentDescription = "Indian farmer observing tomato field with smartphone",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(190.dp)
                        .clip(RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp)),
                    contentScale = ContentScale.Crop
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(8.dp).background(AgriForestGreen, CircleShape))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = st("Field Context Synced"),
                            style = MaterialTheme.typography.bodySmall,
                            color = AgriForestGreen,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Text(
                        text = "${t("common.day")} ${cropContext.cropCycle.cropAgeDays} ${t("common.of")} 85",
                        style = MaterialTheme.typography.bodySmall,
                        color = AgriTextSecondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // CURRENT CROP Card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = AgriCardSurface),
            border = androidx.compose.foundation.BorderStroke(1.2.dp, AgriLineSubtle),
            modifier = Modifier.fillMaxWidth().testTag("dashboard_current_crop_card")
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
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
                        Text(text = "🍅", fontSize = 28.sp, modifier = Modifier.padding(end = 10.dp))
                        Column {
                            Text(
                                text = st(cropContext.cropCycle.cropName),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = AgriTextPrimary
                            )
                            Text(
                                text = "${cropContext.cropCycle.acres} ${t("common.acres")} • ${st(cropContext.farm.location.substringBefore(","))}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = AgriTextSecondary
                            )
                        }
                    }

                    // Health Score Badge
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = AgriSageLight,
                        border = androidx.compose.foundation.BorderStroke(1.dp, AgriForestGreen.copy(alpha = 0.3f))
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(text = t("dashboard.health"), fontSize = 10.sp, color = AgriForestGreen, fontWeight = FontWeight.Medium)
                            Text(
                                text = "${cropContext.latestObservation.healthScore}%",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = AgriForestGreen
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                LinearProgressIndicator(
                    progress = { cropContext.latestObservation.healthScore / 100f },
                    modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                    color = AgriForestGreen,
                    trackColor = AgriSageLight,
                    strokeCap = StrokeCap.Round
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // PRIMARY ACTION: Next Action Banner
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = AgriSageLight),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, AgriForestGreen.copy(alpha = 0.5f)),
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onNavigate(AppDestination.HEALTH) }
                .testTag("dashboard_primary_action_card")
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(AgriForestGreen),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AddAPhoto,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = t("dashboard.nextAction"),
                            style = MaterialTheme.typography.labelSmall,
                            color = AgriForestGreen,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "“${t("dashboard.nextActionTitle")}”",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = AgriTextPrimary
                        )
                        Text(
                            text = t("dashboard.nextActionSubtitle"),
                            style = MaterialTheme.typography.bodySmall,
                            color = AgriTextSecondary
                        )
                    }
                }

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Go to Health Monitor",
                    tint = AgriForestGreen,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // TELEMETRY SNAPSHOT GRID (Health, Risk, Weather, Expected Yield, Estimated Cost)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Risk Card
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = AgriCardSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, AgriWarningAmber.copy(alpha = 0.4f)),
                modifier = Modifier
                    .weight(1f)
                    .clickable { onNavigate(AppDestination.RISK) }
                    .testTag("dashboard_risk_card")
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Warning, contentDescription = null, tint = AgriWarningAmber, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(t("dashboard.risk"), style = MaterialTheme.typography.labelSmall, color = AgriTextSecondary)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = st("Moderate"),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = AgriWarningAmber
                    )
                    Text(st("Humidity stress"), style = MaterialTheme.typography.bodySmall, fontSize = 11.sp, color = AgriTextSecondary)
                }
            }

            // Weather Card
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = AgriCardSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, AgriLineSubtle),
                modifier = Modifier
                    .weight(1f)
                    .clickable { onNavigate(AppDestination.WEATHER) }
                    .testTag("dashboard_weather_card")
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        AgriWeatherIllustration("humidity", size = 20.dp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(t("dashboard.weather"), style = MaterialTheme.typography.labelSmall, color = AgriTextSecondary)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${cropContext.currentWeather.temperatureC}°C",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = AgriTextPrimary
                    )
                    Text("${cropContext.currentWeather.humidityPercent}% ${st("Humidity")}", style = MaterialTheme.typography.bodySmall, fontSize = 11.sp, color = AgriForestGreen)
                }
            }

            // Yield & Cost Card
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = AgriCardSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, AgriLineSubtle),
                modifier = Modifier
                    .weight(1.2f)
                    .clickable { onNavigate(AppDestination.YIELD) }
                    .testTag("dashboard_yield_card")
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(t("dashboard.expectedYield"), style = MaterialTheme.typography.labelSmall, color = AgriTextSecondary)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "5.2–6.1 T",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = AgriForestGreen
                    )
                    Text("${st("Cost")}: ₹42,000", style = MaterialTheme.typography.bodySmall, fontSize = 11.sp, color = AgriWarmClay)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // CROP JOURNEY LIFECYCLE STEPPER
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = st("CROP JOURNEY"),
                    style = MaterialTheme.typography.labelSmall,
                    color = AgriTextSecondary,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Text(
                    text = t("journey.subtitle"),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = AgriTextPrimary
                )
            }

            TextButton(onClick = { onNavigate(AppDestination.JOURNEY) }) {
                Text(t("dashboard.fullTimeline"), color = AgriForestGreen, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Connected Visual Journey Timeline
        VisualJourneyTimeline(
            activeStageIndex = cropContext.cropCycle.currentStageIndex,
            onStageSelected = { stage ->
                onStageClick(stage)
                val target = when (stage) {
                    1 -> AppDestination.PLANNER
                    2 -> AppDestination.JOURNEY
                    3 -> AppDestination.HEALTH
                    4 -> AppDestination.RISK
                    5 -> AppDestination.YIELD
                    6 -> AppDestination.SELLING
                    else -> AppDestination.DASHBOARD
                }
                onNavigate(target)
            }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // RECENT ACTIVITY FEED
        Text(
            text = st("RECENT FIELD ACTIVITY"),
            style = MaterialTheme.typography.labelSmall,
            color = AgriTextSecondary,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )
        Text(
            text = st("Latest Observations & Risk Sync"),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = AgriTextPrimary
        )

        Spacer(modifier = Modifier.height(10.dp))

        val activities = listOf(
            Triple(st("📷 Latest crop image scan"), st("Day 35 leaf photo uploaded. Lower foliage yellowing flagged."), AppDestination.HEALTH),
            Triple(st("⚠️ Possible health change detected"), st("86% → 71% Health score adjustment. Early Blight advisory."), AppDestination.RISK),
            Triple(st("🌧️ Weather alert: Rain expected"), st("78% humidity combined with warm night temperatures."), AppDestination.WEATHER),
            Triple(st("🤝 Uzhavar Sandhai daily quote"), st("₹36/kg spot price for direct consumer sale in Coimbatore."), AppDestination.SELLING)
        )

        activities.forEach { (title, desc, dest) ->
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = AgriCardSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, AgriLineSubtle),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clickable { onNavigate(dest) }
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, color = AgriTextPrimary)
                        Text(desc, style = MaterialTheme.typography.bodySmall, color = AgriTextSecondary)
                    }
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = AgriTextSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        // Global Footer
        AppFooter(onNavigate = onNavigate)
    }
}
