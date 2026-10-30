package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.CropContext
import com.example.data.localization.*
import com.example.ui.theme.*

enum class AppDestination(val route: String, val label: String) {
    DASHBOARD("dashboard", "Home"),
    JOURNEY("journey", "Journey"),
    HEALTH("health", "Monitor"),
    RISK("risk", "Alerts"),
    ASSISTANT("assistant", "Assistant"),
    PLANNER("planner", "Crop Planner"),
    YIELD("yield", "Yield & Cost"),
    SELLING("selling", "Smart Selling"),
    WEATHER("weather", "Weather"),
    REPORTS("reports", "Crop Report"),
    FARM_PROFILE("farm_profile", "My Farm"),
    FARMER_PROFILE("farmer_profile", "Profile"),
    NOTIFICATIONS("notifications", "Notifications"),
    SETTINGS("settings", "Settings")
}

/**
 * Reusable Global Header (Enhanced)
 * Featuring edge-to-edge statusBarsPadding, dynamic micro-weather pill,
 * real-time crop telemetry ribbon, live connection indicator, quick AI assistant action,
 * and high-clarity touch targets.
 */
@Composable
fun AppHeader(
    cropContext: CropContext,
    onNavigate: (AppDestination) -> Unit,
    modifier: Modifier = Modifier,
    onOpenCompanion: (() -> Unit)? = null,
    onOpenLanguageDialog: (() -> Unit)? = null
) {
    val unreadNotifs = cropContext.notifications.count { !it.isRead }
    val currentLang = LocalAppLanguage.current

    Surface(
        color = AgriWarmBg,
        border = androidx.compose.foundation.BorderStroke(1.dp, AgriLineSubtle),
        tonalElevation = 2.dp,
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 8.dp)
        ) {
            // Row 1: Brand Logo & Title (Left) + Actions (Right)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Brand Logo & Title (MyFarm)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { onNavigate(AppDestination.DASHBOARD) }
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.myfarm_logo),
                        contentDescription = "MyFarm Logo",
                        modifier = Modifier
                            .height(34.dp)
                            .width(84.dp)
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    Column {
                        Text(
                            text = "MyFarm",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = AgriForestGreen,
                            fontSize = 15.sp,
                            letterSpacing = 0.2.sp
                        )
                        Text(
                            text = "Smarter Farming, Better Decisions",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Medium,
                            color = AgriTextSecondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // Actions: Language, Notifications, AI Companion, Settings
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Language Chip
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = AgriSageLight,
                        border = androidx.compose.foundation.BorderStroke(1.dp, AgriForestGreen.copy(alpha = 0.3f)),
                        modifier = Modifier.clickable { onOpenLanguageDialog?.invoke() }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Language, contentDescription = null, tint = AgriForestGreen, modifier = Modifier.size(13.dp))
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(currentLang.nativeName, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = AgriForestGreen)
                        }
                    }

                    // AI Companion Button
                    IconButton(
                        onClick = { onOpenCompanion?.invoke() ?: onNavigate(AppDestination.ASSISTANT) },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(AgriSageLight)
                                .border(1.dp, AgriForestGreen.copy(alpha = 0.3f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            LeafAiSparkIcon(size = 15.dp)
                        }
                    }

                    // Notification Icon
                    IconButton(
                        onClick = { onNavigate(AppDestination.NOTIFICATIONS) },
                        modifier = Modifier.size(32.dp)
                    ) {
                        BadgedBox(
                            badge = {
                                if (unreadNotifs > 0) {
                                    Badge(containerColor = AgriTomatoRed, contentColor = Color.White) {
                                        Text("$unreadNotifs", fontSize = 8.sp)
                                    }
                                }
                            }
                        ) {
                            Icon(Icons.Default.Notifications, contentDescription = null, tint = AgriTextPrimary, modifier = Modifier.size(18.dp))
                        }
                    }

                    // Profile / Settings Avatar Button
                    IconButton(
                        onClick = { onNavigate(AppDestination.FARMER_PROFILE) },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(AgriSageLight)
                                .border(1.2.dp, AgriForestGreen, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = cropContext.farmer.name.take(1).ifEmpty { "F" }.uppercase(),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = AgriForestGreen
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Row 2: Clean Metric Strip Card
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = AgriCardSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, AgriLineSubtle),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Crop & Age
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { onNavigate(AppDestination.JOURNEY) }
                    ) {
                        Text(
                            text = "🌱 ${st(cropContext.cropCycle.cropName)} • Day ${cropContext.cropCycle.cropAgeDays}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = AgriForestGreen
                        )
                    }

                    // Live Weather
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { onNavigate(AppDestination.WEATHER) }
                    ) {
                        Text(
                            text = "⛅ ${cropContext.currentWeather.temperatureC}°C",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = AgriTextPrimary
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "• ${cropContext.farm.location.substringBefore(",")}",
                            fontSize = 10.sp,
                            color = AgriTextSecondary
                        )
                    }

                    // Health Score Pill
                    val hScore = cropContext.latestObservation.healthScore
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = when {
                            hScore >= 80 -> AgriSageLight
                            hScore >= 60 -> AgriPaleYellow
                            else -> AgriTomatoRed.copy(alpha = 0.15f)
                        },
                        modifier = Modifier.clickable { onNavigate(AppDestination.HEALTH) }
                    ) {
                        Text(
                            text = "Health $hScore%",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = when {
                                hScore >= 80 -> AgriForestGreen
                                hScore >= 60 -> AgriEarthBrown
                                else -> AgriTomatoRed
                            },
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Reusable Mobile Bottom Navigation
 * 5 primary destinations: Home, Journey, Monitor, Alerts, Assistant + "More"
 */
@Composable
fun AppBottomNavigation(
    currentDestination: AppDestination,
    onNavigate: (AppDestination) -> Unit,
    onOpenMoreMenu: () -> Unit,
    unreadAlertsCount: Int = 0,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        containerColor = AgriCardSurface,
        tonalElevation = 2.dp,
        modifier = modifier
            .fillMaxWidth()
            .testTag("app_bottom_navigation")
    ) {
        // 1. Home
        NavigationBarItem(
            selected = currentDestination == AppDestination.DASHBOARD,
            onClick = { onNavigate(AppDestination.DASHBOARD) },
            icon = {
                Icon(Icons.Default.Home, contentDescription = t("common.home"), modifier = Modifier.size(22.dp))
            },
            label = { Text(t("common.home"), fontSize = 10.sp, maxLines = 1, overflow = TextOverflow.Ellipsis) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = AgriForestGreen,
                selectedTextColor = AgriForestGreen,
                indicatorColor = AgriSageLight,
                unselectedIconColor = AgriTextSecondary,
                unselectedTextColor = AgriTextSecondary
            )
        )

        // 2. Journey
        NavigationBarItem(
            selected = currentDestination == AppDestination.JOURNEY,
            onClick = { onNavigate(AppDestination.JOURNEY) },
            icon = {
                Icon(Icons.Default.Timeline, contentDescription = t("common.journey"), modifier = Modifier.size(22.dp))
            },
            label = { Text(t("common.journey"), fontSize = 10.sp, maxLines = 1, overflow = TextOverflow.Ellipsis) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = AgriForestGreen,
                selectedTextColor = AgriForestGreen,
                indicatorColor = AgriSageLight,
                unselectedIconColor = AgriTextSecondary,
                unselectedTextColor = AgriTextSecondary
            )
        )

        // 3. Monitor
        NavigationBarItem(
            selected = currentDestination == AppDestination.HEALTH,
            onClick = { onNavigate(AppDestination.HEALTH) },
            icon = {
                Icon(Icons.Default.AddAPhoto, contentDescription = t("common.monitor"), modifier = Modifier.size(22.dp))
            },
            label = { Text(t("common.monitor"), fontSize = 10.sp, maxLines = 1, overflow = TextOverflow.Ellipsis) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = AgriForestGreen,
                selectedTextColor = AgriForestGreen,
                indicatorColor = AgriSageLight,
                unselectedIconColor = AgriTextSecondary,
                unselectedTextColor = AgriTextSecondary
            )
        )

        // 4. Alerts
        NavigationBarItem(
            selected = currentDestination == AppDestination.RISK,
            onClick = { onNavigate(AppDestination.RISK) },
            icon = {
                BadgedBox(
                    badge = {
                        if (unreadAlertsCount > 0) {
                            Badge(containerColor = AgriWarningAmber) {
                                Text("$unreadAlertsCount", fontSize = 9.sp)
                            }
                        }
                    }
                ) {
                    Icon(Icons.Default.Warning, contentDescription = t("common.alerts"), modifier = Modifier.size(22.dp))
                }
            },
            label = { Text(t("common.alerts"), fontSize = 10.sp, maxLines = 1, overflow = TextOverflow.Ellipsis) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = AgriForestGreen,
                selectedTextColor = AgriForestGreen,
                indicatorColor = AgriSageLight,
                unselectedIconColor = AgriTextSecondary,
                unselectedTextColor = AgriTextSecondary
            )
        )

        // 5. Assistant
        NavigationBarItem(
            selected = currentDestination == AppDestination.ASSISTANT,
            onClick = { onNavigate(AppDestination.ASSISTANT) },
            icon = {
                LeafAiSparkIcon(size = 20.dp)
            },
            label = { Text(t("common.assistant"), fontSize = 10.sp, maxLines = 1, overflow = TextOverflow.Ellipsis) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = AgriForestGreen,
                selectedTextColor = AgriForestGreen,
                indicatorColor = AgriSageLight,
                unselectedIconColor = AgriTextSecondary,
                unselectedTextColor = AgriTextSecondary
            )
        )

        // 6. More
        NavigationBarItem(
            selected = currentDestination in listOf(
                AppDestination.PLANNER,
                AppDestination.YIELD,
                AppDestination.SELLING,
                AppDestination.WEATHER,
                AppDestination.REPORTS,
                AppDestination.FARM_PROFILE,
                AppDestination.SETTINGS
            ),
            onClick = onOpenMoreMenu,
            icon = {
                Icon(Icons.Default.Menu, contentDescription = t("common.more"), modifier = Modifier.size(22.dp))
            },
            label = { Text(t("common.more"), fontSize = 10.sp, maxLines = 1, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = AgriForestGreen,
                selectedTextColor = AgriForestGreen,
                indicatorColor = AgriSageLight,
                unselectedIconColor = AgriTextSecondary,
                unselectedTextColor = AgriTextSecondary
            )
        )
    }
}

/**
 * Reusable "More" Bottom Sheet / Menu
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoreFeaturesBottomSheet(
    onDismiss: () -> Unit,
    onNavigate: (AppDestination) -> Unit,
    onOpenLanguageDialog: () -> Unit = {}
) {
    val currentLang = LocalAppLanguage.current
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = AgriWarmBg
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 10.dp)
                .navigationBarsPadding()
        ) {
            Text(
                text = t("common.more"),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = AgriTextPrimary
            )
            Spacer(modifier = Modifier.height(14.dp))

            // Dedicated Language Switcher Row in More Sheet
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = AgriSageLight,
                border = androidx.compose.foundation.BorderStroke(1.2.dp, AgriForestGreen.copy(alpha = 0.5f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clickable {
                        onDismiss()
                        onOpenLanguageDialog()
                    }
                    .testTag("more_language_button")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(AgriForestGreen),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Language, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(t("settings.language"), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = AgriForestGreen)
                        Text(currentLang.nativeName + " (" + currentLang.englishName + ")", style = MaterialTheme.typography.bodySmall, fontSize = 11.sp, color = AgriTextSecondary)
                    }
                }
            }

            val items = listOf(
                Triple(AppDestination.PLANNER, "journey.plan", st("Crop Planner")),
                Triple(AppDestination.WEATHER, "weather.title", st("Microclimate humidity & 7-day forecast")),
                Triple(AppDestination.YIELD, "yield.title", st("5.2-6.1 tonnes forecast & cost breakdown")),
                Triple(AppDestination.SELLING, "market.title", st("Uzhavar Sandhai vs Mandi quotes")),
                Triple(AppDestination.SETTINGS, "settings.title", st("Regional languages, units & offline settings"))
            )

            items.forEach { (dest, titleKey, subtitle) ->
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = AgriCardSurface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, AgriLineSubtle),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable {
                            onDismiss()
                            onNavigate(dest)
                        }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(AgriSageLight),
                            contentAlignment = Alignment.Center
                        ) {
                            when (dest) {
                                AppDestination.PLANNER -> StageAgriIcon(1, size = 22.dp)
                                AppDestination.WEATHER -> AgriWeatherIllustration("humidity", size = 26.dp)
                                AppDestination.YIELD -> StageAgriIcon(5, size = 22.dp)
                                AppDestination.SELLING -> StageAgriIcon(6, size = 22.dp)
                                else -> Icon(Icons.Default.Settings, contentDescription = null, tint = AgriForestGreen, modifier = Modifier.size(18.dp))
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(t(titleKey), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, color = AgriTextPrimary)
                            Text(subtitle, style = MaterialTheme.typography.bodySmall, fontSize = 11.sp, color = AgriTextSecondary)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

/**
 * Reusable Page Header
 */
@Composable
fun PageHeader(
    stageTag: String? = null,
    title: String,
    subtitle: String? = null,
    onBack: (() -> Unit)? = null,
    action: (@Composable () -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            if (onBack != null) {
                IconButton(onClick = onBack, modifier = Modifier.testTag("page_back_button")) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = AgriTextPrimary
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
            }
            Column {
                if (stageTag != null) {
                    Text(
                        text = stageTag,
                        style = MaterialTheme.typography.labelSmall,
                        color = AgriForestGreen,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = AgriTextPrimary
                )
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = AgriTextSecondary
                    )
                }
            }
        }

        if (action != null) {
            action()
        }
    }
}

/**
 * Reusable Section Header
 */
@Composable
fun SectionHeader(
    tag: String? = null,
    title: String,
    subtitle: String? = null,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth().padding(vertical = 6.dp)) {
        if (tag != null) {
            Text(
                text = tag,
                style = MaterialTheme.typography.labelSmall,
                color = AgriTextSecondary,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = AgriTextPrimary
        )
        if (subtitle != null) {
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = AgriTextSecondary
            )
        }
    }
}

/**
 * Reusable Global Footer
 */
@Composable
fun AppFooter(
    onNavigate: (AppDestination) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = AgriSurfaceSubtle,
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, AgriLineSubtle),
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 24.dp)
            .testTag("app_global_footer")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                LeafAiSparkIcon(size = 20.dp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = t("common.appName"),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = AgriForestGreen
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "“${st("Smarter decisions from planting to selling.")}”",
                style = MaterialTheme.typography.bodySmall,
                color = AgriTextSecondary
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Footer navigation links
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(st("Modules"), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = AgriTextPrimary)
                    Text(t("common.home"), fontSize = 11.sp, color = AgriForestGreen, modifier = Modifier.clickable { onNavigate(AppDestination.DASHBOARD) })
                    Text(t("common.journey"), fontSize = 11.sp, color = AgriForestGreen, modifier = Modifier.clickable { onNavigate(AppDestination.JOURNEY) })
                    Text(t("common.monitor"), fontSize = 11.sp, color = AgriForestGreen, modifier = Modifier.clickable { onNavigate(AppDestination.HEALTH) })
                }
                Column {
                    Text(st("Insights"), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = AgriTextPrimary)
                    Text(t("weather.title"), fontSize = 11.sp, color = AgriForestGreen, modifier = Modifier.clickable { onNavigate(AppDestination.WEATHER) })
                    Text(t("market.title"), fontSize = 11.sp, color = AgriForestGreen, modifier = Modifier.clickable { onNavigate(AppDestination.SELLING) })
                    Text(st("Reports"), fontSize = 11.sp, color = AgriForestGreen, modifier = Modifier.clickable { onNavigate(AppDestination.REPORTS) })
                }
                Column {
                    Text(st("Support"), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = AgriTextPrimary)
                    Text("v1.0 • ${st("AI Farm Companion")}", fontSize = 10.sp, color = AgriTextSecondary)
                }
            }
        }
    }
}
