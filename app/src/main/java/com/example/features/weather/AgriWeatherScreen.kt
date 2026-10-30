package com.example.features.weather

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
import com.example.ui.components.AgriWeatherIllustration
import com.example.ui.components.PageHeader
import com.example.ui.components.SubtleFarmBackground
import com.example.ui.theme.*

import com.example.data.localization.*

/**
 * FEATURE 10 — AGRICULTURAL WEATHER
 * Focuses on crop impact, microclimate humidity, spray advisories, and 7-day field planning.
 */
@Composable
fun AgriWeatherScreen(
    cropContext: CropContext,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val weather = cropContext.currentWeather
    val forecast = cropContext.weatherForecast

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 18.dp, vertical = 12.dp)
    ) {
        PageHeader(
            stageTag = st("FIELD TELEMETRY"),
            title = t("weather.title"),
            subtitle = t("weather.subtitle"),
            onBack = onBack
        )

        // Hero Weather Illustration Card
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
                        painter = painterResource(id = R.drawable.img_farmer_hero),
                        contentDescription = "Farmer observing field with rain clouds and agricultural microclimate",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "${weather.temperatureC}°C • ${st(weather.conditionTitle)}",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = AgriTextPrimary
                        )
                        Text(
                            text = "${cropContext.farm.location} • ${weather.dateFormatted}",
                            style = MaterialTheme.typography.bodySmall,
                            color = AgriTextSecondary
                        )
                    }

                    Surface(
                        color = AgriPaleYellow,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "${st("Rain")}: ${weather.rainfallChance}% ${st("Chance")}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = AgriEarthBrown,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // 3 Mini Metric Badges
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = AgriSageLight,
                        modifier = Modifier.weight(1f).padding(end = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(t("weather.humidity"), fontSize = 10.sp, color = AgriTextSecondary)
                            Text("${weather.humidityPercent}%", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = AgriForestGreen)
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = AgriSageLight,
                        modifier = Modifier.weight(1f).padding(horizontal = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(t("weather.wind"), fontSize = 10.sp, color = AgriTextSecondary)
                            Text("${weather.windSpeedKmh} km/h", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = AgriForestGreen)
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = AgriSageLight,
                        modifier = Modifier.weight(1f).padding(start = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(st("Soil Moisture"), fontSize = 10.sp, color = AgriTextSecondary)
                            Text("68% ${st("Adequate")}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = AgriForestGreen)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // "Why This Matters" Section
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = AgriPaleYellow),
            border = androidx.compose.foundation.BorderStroke(1.dp, AgriOchre.copy(alpha = 0.5f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Eco, contentDescription = null, tint = AgriForestGreen, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = st("WHY THIS MATTERS TO YOUR TOMATO CROP"),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = AgriEarthBrown
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = st(weather.cropImpactNotice),
                    fontSize = 12.sp,
                    color = AgriTextPrimary,
                    lineHeight = 17.sp
                )

                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color.White.copy(alpha = 0.7f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "${st("Farmer Advisory")}: ${st(weather.recommendation)}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = AgriTextPrimary,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 7-Day Agricultural Forecast
        Text(
            text = st("7-DAY AGRICULTURAL FORECAST & SPRAY WINDOW"),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = AgriTextSecondary,
            letterSpacing = 0.5.sp
        )
        Spacer(modifier = Modifier.height(8.dp))

        val days = listOf(
            Triple("Mon (Today)", "28°C / 78%", "Rain expected in 36h • Hold off foliar spraying"),
            Triple("Tue", "26°C / 82%", "Heavy drizzle probable • Check furrow drainage"),
            Triple("Wed", "27°C / 70%", "Clearing skies • Ideal for biological NSKE application"),
            Triple("Thu", "29°C / 62%", "Optimal sunshine • Resume normal drip irrigation"),
            Triple("Fri", "30°C / 58%", "Warm & clear • Canopy ventilation optimal"),
            Triple("Sat", "31°C / 55%", "Sunny • Flowering fruit setting active"),
            Triple("Sun", "30°C / 58%", "Mild breeze • Excellent field inspection conditions")
        )

        days.forEach { (day, tempHum, advice) ->
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = AgriCardSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, AgriLineSubtle),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 3.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.width(90.dp)) {
                        Text(st(day), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AgriTextPrimary)
                        Text(tempHum, fontSize = 10.sp, color = AgriTextSecondary)
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Text(
                        text = st(advice),
                        fontSize = 11.sp,
                        color = AgriForestGreen,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
