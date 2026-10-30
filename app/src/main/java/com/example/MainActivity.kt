package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.NotificationCategory
import com.example.data.localization.*
import com.example.features.assistant.AiAssistantScreen
import com.example.features.auth.WelcomeAuthScreen
import com.example.features.crophealth.service.CropHealthService
import com.example.features.crophealth.ui.CropHealthMonitorScreen
import com.example.features.dashboard.DashboardScreen
import com.example.features.journey.CropJourneyScreen
import com.example.features.notifications.NotificationCenterScreen
import com.example.features.planning.CropPlannerScreen
import com.example.features.profiles.FarmProfileScreen
import com.example.features.profiles.FarmerProfileScreen
import com.example.features.reports.CropReportScreen
import com.example.features.risk.RiskAlertsScreen
import com.example.features.selling.SmartSellingFlowScreen
import com.example.features.settings.SettingsScreen
import com.example.features.weather.AgriWeatherScreen
import com.example.features.yield.YieldCostScreen
import com.example.ui.components.*
import com.example.ui.theme.AgriWarmBg
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.CropContextEngine
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    private val engine: CropContextEngine by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val currentLanguage by engine.currentLanguage.collectAsStateWithLifecycle()

            CompositionLocalProvider(LocalAppLanguage provides currentLanguage) {
                MyApplicationTheme {
                    val cropContext by engine.cropContext.collectAsStateWithLifecycle()
                    val session by engine.session.collectAsStateWithLifecycle()
                    val farmsList by engine.farmsList.collectAsStateWithLifecycle()
                    val journeyStages by engine.journeyStages.collectAsStateWithLifecycle()
                    val assistantMessages by engine.assistantMessages.collectAsStateWithLifecycle()
                    val isAssistantLoading by engine.isAssistantLoading.collectAsStateWithLifecycle()

                    var currentDestination by remember { mutableStateOf(AppDestination.DASHBOARD) }
                    var showSplash by remember { mutableStateOf(true) }
                    var showAuthScreen by remember { mutableStateOf(false) }
                    var showMoreMenu by remember { mutableStateOf(false) }
                    var showAiCompanionSheet by remember { mutableStateOf(false) }
                    var showLanguageDialog by remember { mutableStateOf(false) }

                    // System Back button handler
                    BackHandler(enabled = currentDestination != AppDestination.DASHBOARD || showAuthScreen) {
                        if (showAuthScreen) {
                            showAuthScreen = false
                        } else {
                            currentDestination = AppDestination.DASHBOARD
                        }
                    }

                    val isLoggedIn = session?.isLoggedIn == true

                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = AgriWarmBg
                    ) {
                        if (showSplash) {
                            MyFarmSplashScreen(onSplashFinished = { showSplash = false })
                        } else if (showAuthScreen || !isLoggedIn) {
                            WelcomeAuthScreen(
                                onOnboardingComplete = { farmer, farm, cropCycle, sessionKey ->
                                    engine.completeOnboarding(farmer, farm, cropCycle, sessionKey)
                                    showAuthScreen = false
                                },
                                onSessionRestore = { sessionKey ->
                                    engine.restoreSessionKey(sessionKey)
                                    showAuthScreen = false
                                },
                                onLanguageChange = { newLang ->
                                    engine.setLanguage(newLang)
                                }
                            )
                        } else {
                            Scaffold(
                                topBar = {
                                    AppHeader(
                                        cropContext = cropContext,
                                        onNavigate = { destination ->
                                            currentDestination = destination
                                        },
                                        onOpenCompanion = {
                                            showAiCompanionSheet = true
                                        },
                                        onOpenLanguageDialog = {
                                            showLanguageDialog = true
                                        }
                                    )
                                },
                                bottomBar = {
                                    val unreadAlerts = cropContext.notifications.count {
                                        !it.isRead && it.category == NotificationCategory.ALERT
                                    }
                                    AppBottomNavigation(
                                        currentDestination = currentDestination,
                                        onNavigate = { destination ->
                                            currentDestination = destination
                                        },
                                        onOpenMoreMenu = {
                                            showMoreMenu = true
                                        },
                                        unreadAlertsCount = unreadAlerts
                                    )
                                },
                                containerColor = AgriWarmBg
                            ) { innerPadding ->
                                val contentModifier = Modifier
                                    .fillMaxSize()
                                    .padding(innerPadding)

                                when (currentDestination) {
                                    AppDestination.DASHBOARD -> {
                                        DashboardScreen(
                                            cropContext = cropContext,
                                            onNavigate = { destination ->
                                                currentDestination = destination
                                            },
                                            onStageClick = { stageNum ->
                                                engine.selectCropStage(stageNum)
                                                currentDestination = when (stageNum) {
                                                    1 -> AppDestination.PLANNER
                                                    2 -> AppDestination.JOURNEY
                                                    3 -> AppDestination.HEALTH
                                                    4 -> AppDestination.RISK
                                                    5 -> AppDestination.YIELD
                                                    6 -> AppDestination.SELLING
                                                    else -> AppDestination.DASHBOARD
                                                }
                                            },
                                            modifier = contentModifier
                                        )
                                    }

                                    AppDestination.JOURNEY -> {
                                        CropJourneyScreen(
                                            cropContext = cropContext,
                                            stages = journeyStages,
                                            onSelectStage = { stageIndex ->
                                                engine.selectCropStage(stageIndex)
                                                currentDestination = when (stageIndex) {
                                                    1 -> AppDestination.PLANNER
                                                    2 -> AppDestination.JOURNEY
                                                    3 -> AppDestination.HEALTH
                                                    4 -> AppDestination.RISK
                                                    5 -> AppDestination.YIELD
                                                    6 -> AppDestination.SELLING
                                                    else -> AppDestination.JOURNEY
                                                }
                                            },
                                            onBack = { currentDestination = AppDestination.DASHBOARD },
                                            modifier = contentModifier
                                        )
                                    }

                                    AppDestination.HEALTH -> {
                                        val cropHealthService = remember { CropHealthService(applicationContext, engine) }
                                        DisposableEffect(cropHealthService) {
                                            onDispose { cropHealthService.close() }
                                        }
                                        CropHealthMonitorScreen(
                                            cropContext = cropContext,
                                            service = cropHealthService,
                                            onNavigate = { destination ->
                                                currentDestination = destination
                                            },
                                            onOpenCompanionWithAssessment = { prompt ->
                                                engine.sendAssistantPrompt(prompt)
                                                showAiCompanionSheet = true
                                            },
                                            modifier = contentModifier
                                        )
                                    }

                                    AppDestination.RISK -> {
                                        RiskAlertsScreen(
                                            cropContext = cropContext,
                                            onResolveRisk = { riskId ->
                                                engine.resolveRisk(riskId)
                                            },
                                            onNavigateYield = {
                                                engine.selectCropStage(5)
                                                currentDestination = AppDestination.YIELD
                                            },
                                            onBack = { currentDestination = AppDestination.DASHBOARD },
                                            modifier = contentModifier
                                        )
                                    }

                                    AppDestination.PLANNER -> {
                                        CropPlannerScreen(
                                            cropContext = cropContext,
                                            onBack = { currentDestination = AppDestination.DASHBOARD },
                                            onProceedToJourney = {
                                                engine.selectCropStage(2)
                                                currentDestination = AppDestination.JOURNEY
                                            },
                                            onConfirmPlantCrop = { cropName, variety, acres ->
                                                engine.updatePlantedCrop(cropName, variety, acres)
                                                engine.selectCropStage(2)
                                                currentDestination = AppDestination.JOURNEY
                                            },
                                            onAskAiSuitability = { query ->
                                                engine.sendAssistantPrompt("Is $query suitable for my farm in ${cropContext.farm.location} with ${cropContext.farm.soilType} soil?")
                                                showAiCompanionSheet = true
                                            },
                                            modifier = contentModifier
                                        )
                                    }

                                    AppDestination.YIELD -> {
                                        YieldCostScreen(
                                            cropContext = cropContext,
                                            onNavigateSelling = {
                                                engine.selectCropStage(6)
                                                currentDestination = AppDestination.SELLING
                                            },
                                            onBack = { currentDestination = AppDestination.DASHBOARD },
                                            modifier = contentModifier
                                        )
                                    }

                                    AppDestination.SELLING -> {
                                        SmartSellingFlowScreen(
                                            cropContext = cropContext,
                                            onBack = { currentDestination = AppDestination.DASHBOARD },
                                            modifier = contentModifier
                                        )
                                    }

                                    AppDestination.WEATHER -> {
                                        AgriWeatherScreen(
                                            cropContext = cropContext,
                                            onBack = { currentDestination = AppDestination.DASHBOARD },
                                            modifier = contentModifier
                                        )
                                    }

                                    AppDestination.REPORTS -> {
                                        CropReportScreen(
                                            cropContext = cropContext,
                                            onBack = { currentDestination = AppDestination.DASHBOARD },
                                            modifier = contentModifier
                                        )
                                    }

                                    AppDestination.FARM_PROFILE -> {
                                        FarmProfileScreen(
                                            cropContext = cropContext,
                                            registeredFarms = farmsList,
                                            onSwitchFarm = { farm ->
                                                engine.switchFarm(farm)
                                            },
                                            onAddFarmParcel = { farmName, location, acres, soilType, waterType, cropName ->
                                                engine.addNewFarmParcel(farmName, location, acres, soilType, waterType, cropName)
                                            },
                                            onBack = { currentDestination = AppDestination.DASHBOARD },
                                            modifier = contentModifier
                                        )
                                    }

                                    AppDestination.FARMER_PROFILE -> {
                                        FarmerProfileScreen(
                                            cropContext = cropContext,
                                            onSaveProfile = { name, phone, language, location ->
                                                engine.updateFarmerProfile(name, phone, language, location)
                                            },
                                            onSignOut = {
                                                engine.signOut()
                                            },
                                            onBack = { currentDestination = AppDestination.DASHBOARD },
                                            modifier = contentModifier
                                        )
                                    }

                                    AppDestination.NOTIFICATIONS -> {
                                        NotificationCenterScreen(
                                            cropContext = cropContext,
                                            onMarkRead = { notifId ->
                                                engine.markNotificationRead(notifId)
                                            },
                                            onMarkAllRead = {
                                                engine.markAllNotificationsRead()
                                            },
                                            onNavigate = { dest ->
                                                currentDestination = dest
                                            },
                                            onBack = { currentDestination = AppDestination.DASHBOARD },
                                            modifier = contentModifier
                                        )
                                    }

                                    AppDestination.SETTINGS -> {
                                        SettingsScreen(
                                            cropContext = cropContext,
                                            onToggleOffline = {
                                                engine.toggleOfflineMode()
                                            },
                                            onSignOut = {
                                                engine.signOut()
                                            },
                                            onBack = { currentDestination = AppDestination.DASHBOARD },
                                            modifier = contentModifier
                                        )
                                    }

                                    AppDestination.ASSISTANT -> {
                                        AiAssistantScreen(
                                            cropContext = cropContext,
                                            messages = assistantMessages,
                                            isLoading = isAssistantLoading,
                                            onSendMessage = { prompt ->
                                                engine.sendAssistantPrompt(prompt)
                                            },
                                            onBack = { currentDestination = AppDestination.DASHBOARD },
                                            modifier = contentModifier
                                        )
                                    }
                                }
                            }
                        }

                        // More Features Bottom Sheet
                        if (showMoreMenu) {
                            MoreFeaturesBottomSheet(
                                onDismiss = { showMoreMenu = false },
                                onNavigate = { dest ->
                                    currentDestination = dest
                                },
                                onOpenLanguageDialog = {
                                    showLanguageDialog = true
                                }
                            )
                        }

                        // Floating Companion Bottom Sheet
                        if (showAiCompanionSheet) {
                            FarmCompanionBottomSheet(
                                onDismiss = { showAiCompanionSheet = false },
                                cropContext = cropContext,
                                messages = assistantMessages,
                                isLoading = isAssistantLoading,
                                onSendMessage = { prompt ->
                                    engine.sendAssistantPrompt(prompt)
                                }
                            )
                        }

                        // Global Language Selection Dialog
                        if (showLanguageDialog) {
                            LanguageSelectionDialog(
                                currentLanguage = currentLanguage,
                                onSelectLanguage = { newLang ->
                                    engine.setLanguage(newLang)
                                },
                                onDismiss = { showLanguageDialog = false }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MyFarmSplashScreen(onSplashFinished: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(1600)
        onSplashFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFF3F8F2),
                        Color(0xFFE5F2E3),
                        Color(0xFF2E6F40)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(24.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.myfarm_logo),
                contentDescription = "MyFarm Logo",
                modifier = Modifier
                    .width(240.dp)
                    .height(120.dp)
            )

            Spacer(modifier = Modifier.height(28.dp))

            CircularProgressIndicator(
                color = Color(0xFF2E6F40),
                strokeWidth = 3.dp,
                modifier = Modifier.size(42.dp)
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Loading...",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF2E6F40)
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Smarter Farming, Better Decisions",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF2E6F40).copy(alpha = 0.8f)
            )
        }
    }
}
