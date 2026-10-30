package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

/**
 * Crop Profile Entity for Room & Legacy Screens
 */
@Entity(tableName = "crop_profile")
data class CropProfile(
    @PrimaryKey val id: Long = 1,
    val cropName: String = "Tomato",
    val variety: String = "Shivam / Pusa Ruby (Hybrid)",
    val acres: Double = 2.0,
    val acreage: String = "2.0 Acres",
    val location: String = "Coimbatore, Tamil Nadu",
    val plantingDate: String = "Aug 25, 2026",
    val daysAfterTransplant: Int = 34,
    val cropAgeDays: Int = 35,
    val currentStage: String = "02 GROW (Vegetative)",
    val healthScore: Int = 86,
    val previousHealthScore: Int = 86,
    val soilType: String = "Red Sandy Loam",
    val irrigationType: String = "Drip Irrigation",
    val currentStatus: String = "Optimal vegetative growth with clean dark green leaves."
)

/**
 * Farm Task Entity for Stage Guidance Checklist
 */
@Entity(tableName = "farm_tasks")
data class FarmTask(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val stageIndex: Int = 1,
    val title: String = "",
    val timing: String = "",
    val guidance: String = "",
    val isCompleted: Boolean = false,
    val category: String = "General"
)

/**
 * Farmer Profile Entity
 */
@Entity(tableName = "farmers")
data class Farmer(
    @PrimaryKey val id: String = "farmer_demo_1",
    val name: String = "Murugan (Farmer)",
    val phone: String = "+91 98765 43210",
    val preferredLanguage: String = "English",
    val location: String = "Coimbatore, Tamil Nadu",
    val profileCompletionPercent: Int = 90,
    val avatarUrl: String? = null
)

/**
 * Session State Entity for persistent session key and user login state
 */
@Entity(tableName = "session_state")
data class SessionEntity(
    @PrimaryKey val id: Int = 1,
    val sessionKey: String = "KSN-8842-TN",
    val activeFarmerId: String = "farmer_demo_1",
    val activeFarmId: String = "farm_demo_1",
    val isLoggedIn: Boolean = true
)


/**
 * Farm Profile Entity (Multi-farm ready)
 */
@Entity(tableName = "farms")
data class Farm(
    @PrimaryKey val id: String = "farm_demo_1",
    val farmerId: String = "farmer_demo_1",
    val farmName: String = "Coimbatore Green Valley Farm",
    val location: String = "Coimbatore, Tamil Nadu",
    val landSizeAcres: Double = 2.0,
    val soilType: String = "Red Sandy Loam (pH 6.8)",
    val waterAvailability: String = "Borewell + Drip Irrigation",
    val coordinates: String = "11.0168° N, 76.9558° E",
    val isDefault: Boolean = true
)

/**
 * Crop Cycle Entity representing the active crop journey
 */
@Entity(tableName = "crop_cycles")
data class CropCycle(
    @PrimaryKey val id: String = "cycle_demo_tomato",
    val farmId: String = "farm_demo_1",
    val cropName: String = "Tomato",
    val variety: String = "Shivam / Pusa Ruby (Hybrid)",
    val acres: Double = 2.0,
    val plantingDate: String = "Aug 25, 2026",
    val cropAgeDays: Int = 35,
    val currentStageName: String = "03 MONITOR",
    val currentStageIndex: Int = 3,
    val currentStageDescription: String = "Vegetative Growth & Canopy Monitoring",
    val expectedHarvestDate: String = "Nov 15, 2026",
    val status: String = "Active"
)

/**
 * Crop Observation Entity (Comparison between previous and current observations)
 */
@Entity(tableName = "crop_observations")
data class CropObservation(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val cropCycleId: String = "cycle_demo_tomato",
    val dayNumber: Int = 35,
    val dateLabel: String = "Day 35 • Observation",
    val stage: String = "Vegetative Stage",
    val stageName: String = "Vegetative Stage",
    val healthScore: Int = 86,
    val summary: String = "",
    val detail: String = "",
    val riskLevel: String = "Low",
    val confidencePercent: Int = 88,
    val observedChanges: String = "Normal leaf development.",
    val suggestedNextSteps: String = "Continue scheduled irrigation.",
    val leafCondition: String = "Healthy green leaf lamina, crisp margins.",
    val weatherCondition: String = "Sunny & Warm",
    val isToday: Boolean = false,
    val isCurrent: Boolean = false,
    val drawableResName: String = "img_during_cultivation"
)

/**
 * Risk Alert Entity (Connected to Crop Context Engine)
 */
@Entity(tableName = "risk_alerts")
data class RiskAlert(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val cropCycleId: String = "cycle_demo_tomato",
    val riskType: String = "Disease",
    val title: String = "",
    val severity: String = "Moderate",
    val whatWasObserved: String = "",
    val whyIsThisARisk: String = "",
    val whatInfoContributed: String = "",
    val whatToInspectNext: String = "",
    val recommendedOrganicAction: String = "",
    val isResolved: Boolean = false,
    val dateReported: String = "Day 35 • High Humidity Alert"
)

/**
 * Weather Record
 */
data class WeatherRecord(
    val dayLabel: String = "Today",
    val dateFormatted: String = "Monday, Sep 28",
    val temperatureC: Int = 28,
    val humidityPercent: Int = 78,
    val conditionTitle: String = "High Humidity & Cloud Cover",
    val forecastText: String = "Rain Expected within 36 hours",
    val rainfallChance: Int = 65,
    val windSpeedKmh: Int = 14,
    val cropImpactNotice: String = "Recent humidity (78%) and warm soil conditions may accelerate foliar moisture retention, increasing early fungal/blight risks in dense vegetative tomato rows.",
    val recommendation: String = "Inspect bottom leaves for moisture spotting, withhold evening foliar wetting, and ensure drainage furrows are clear.",
    val sceneType: String = "humidity"
)

/**
 * Weather Data for legacy viewmodel & screen
 */
data class WeatherConditionData(
    val temperatureC: Int = 28,
    val humidityPercent: Int = 78,
    val conditionTitle: String = "High Humidity & Cloud Cover",
    val forecastText: String = "Rain Expected in 36h",
    val rainfallChance: Int = 65,
    val windSpeedKmh: Int = 14,
    val cropImpactNotice: String = "Recent humidity (78%) increases fungal blight vulnerability.",
    val whyItMatters: String = "Recent humidity (78%) and warm soil conditions may accelerate foliar moisture retention, increasing early fungal/blight risks in dense vegetative tomato rows.",
    val recommendation: String = "Inspect lower leaves, hold off foliar spraying.",
    val sceneType: String = "humidity"
)

/**
 * Yield & Cultivation Cost Estimate
 */
data class YieldEstimate(
    val cropCycleId: String = "cycle_demo_tomato",
    val landAcres: Double = 2.0,
    val cropName: String = "Tomato",
    val estimatedCostTotal: Int = 63000,
    val costSeeds: Int = 12000,
    val costDripIrrigation: Int = 8500,
    val costOrganicNutrients: Int = 18500,
    val costLaborStaking: Int = 24000,
    val expectedYieldMinTonnes: Double = 5.2,
    val expectedYieldMaxTonnes: Double = 6.1,
    val expectedYieldTonnesMin: Double = 5.2,
    val expectedYieldTonnesMax: Double = 6.1,
    val expectedYieldBagsMin: Int = 210,
    val expectedYieldBagsMax: Int = 245,
    val confidencePercent: Int = 89,
    val harvestWindowDays: Int = 28,
    val projectedRevenue: Int = 158000,
    val potentialRevenueMin: Int = 140000,
    val potentialRevenueMax: Int = 185000,
    val estimatedNetReturnMin: Int = 98000,
    val estimatedNetReturnMax: Int = 143000,
    val mandiRateMinPerKg: Int = 28,
    val mandiRateMaxPerKg: Int = 36,
    val isEstimateNotice: String = "All values are estimates based on regional planting density and seasonal yield benchmarks."
)

/**
 * Market Selling Channel Option
 */
@Entity(tableName = "market_options")
data class MarketOption(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val marketName: String = "",
    val name: String = "",
    val distanceKm: Int = 0,
    val ratePerKg: Int = 32,
    val pricePerKg: Int = 32,
    val commissionPercent: Int = 0,
    val transportCostEst: Int = 1200,
    val transportCost: String = "Low (₹1.20/kg)",
    val transportCostText: String = "Low (₹1.20/kg)",
    val transportCostNum: Double = 1.20,
    val demandLevel: String = "High",
    val netRealizationNum: Double = 30.80,
    val netPayoutEst: Int = 166400,
    val isRecommended: Boolean = false,
    val notes: String = "",
    val marketType: String = "Farmer Direct Market",
    val suitabilityBadge: String = "Most Profitable",
    val recommendationNote: String = "Direct sale with zero middlemen commissions."
)

/**
 * Notification Category
 */
enum class NotificationCategory {
    ALERT, GUIDANCE, MARKET, WEATHER, SYSTEM
}

/**
 * Notification Item
 */
@Entity(tableName = "notifications")
data class NotificationItem(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val title: String = "",
    val message: String = "",
    val category: NotificationCategory = NotificationCategory.ALERT,
    val timeAgo: String = "Just now",
    val timestamp: String = "Just now",
    val isRead: Boolean = false,
    val targetScreen: String = "DASHBOARD"
)

/**
 * Crop Report Entity
 */
data class CropReport(
    val id: String = "report_tomato_35",
    val generatedDate: String = "Sep 28, 2026",
    val farmerName: String = "Murugan (Farmer)",
    val farmName: String = "Coimbatore Green Valley Farm",
    val cropName: String = "Tomato",
    val variety: String = "Shivam / Pusa Ruby",
    val location: String = "Coimbatore, Tamil Nadu",
    val landSizeAcres: Double = 2.0,
    val cropAgeDays: Int = 35,
    val currentStage: String = "03 MONITOR (Vegetative)",
    val healthScore: Int = 71,
    val previousHealthScore: Int = 86,
    val currentRisk: String = "Moderate (High Humidity Stress)",
    val weatherSnapshot: String = "28°C • 78% Humidity • Rain Expected",
    val yieldProjection: String = "5.2 – 6.1 tonnes (Estimated Net Return: ₹95,000)",
    val topMarketRecommendation: String = "Coimbatore Uzhavar Sandhai (12 km, ₹32/kg)",
    val aiSummary: String = "Your tomato crop is currently in the vegetative stage (Day 35). Recent observations show a 15% health score change on lower foliage under 78% humidity. Timely morning pruning and 5% NSKE spray will protect yield before upcoming rain."
)

/**
 * Message Sender
 */
enum class MessageSender {
    USER, AI, COMPANION
}

/**
 * AiChatMessage for BottomSheet
 */
data class AiChatMessage(
    val id: Long = 0,
    val sender: MessageSender,
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * Assistant Sender for CropContextEngine
 */
enum class AssistantSender {
    USER, ASSISTANT
}

/**
 * Chat Message for AI Farm Assistant
 */
data class AssistantMessage(
    val id: String = UUID.randomUUID().toString(),
    val sender: AssistantSender,
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * Central Crop Context Engine Data Structure
 */
data class CropContext(
    val farmer: Farmer,
    val farm: Farm,
    val cropCycle: CropCycle,
    val latestObservation: CropObservation,
    val previousObservation: CropObservation?,
    val recentObservations: List<CropObservation>,
    val activeRisks: List<RiskAlert>,
    val currentWeather: WeatherRecord,
    val weatherForecast: List<WeatherRecord>,
    val yieldEstimate: YieldEstimate,
    val marketOptions: List<MarketOption>,
    val notifications: List<NotificationItem>,
    val isOffline: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
)
