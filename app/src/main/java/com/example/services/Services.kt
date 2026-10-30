package com.example.services

import com.example.data.*
import kotlinx.coroutines.delay

/**
 * Service contracts for Micro-Feature Architecture
 */

interface CropPlannerService {
    data class CropPlanInput(
        val location: String = "Coimbatore, Tamil Nadu",
        val landSizeAcres: Double = 2.0,
        val season: String = "Current Season (Kharif/Rabi)",
        val waterAvailability: String = "Moderate Water (Borewell + Drip)",
        val soilType: String = "Red Sandy Loam",
        val targetBudget: Int = 50000
    )

    data class CropPlanResult(
        val recommendedCrop: String,
        val variety: String,
        val suitability: String, // "High", "Moderate"
        val reason: String,
        val growingPeriodDays: String,
        val estimatedCost: String,
        val expectedYieldRange: String,
        val alternativeCrops: List<AlternativeCrop>
    )

    data class AlternativeCrop(
        val name: String,
        val suitability: String,
        val reason: String,
        val estimatedCost: String,
        val yieldRange: String
    )

    suspend fun getCropRecommendations(input: CropPlanInput): CropPlanResult
}

interface CropJourneyService {
    data class JourneyStage(
        val stageNumber: Int,
        val stageCode: String,
        val title: String,
        val subtitle: String,
        val description: String,
        val status: String, // "Completed", "In Progress", "Action Needed", "Upcoming"
        val milestones: List<StageMilestone>
    )

    data class StageMilestone(
        val dayLabel: String,
        val title: String,
        val detail: String,
        val isDone: Boolean,
        val photoEvidence: String? = null
    )

    suspend fun getCropJourney(cropCycleId: String): List<JourneyStage>
}

interface HealthService {
    data class AnalysisResult(
        val previousObservation: CropObservation,
        val currentObservation: CropObservation,
        val healthScoreChange: String, // "86% → 71%"
        val statusMessage: String,
        val observedChanges: String,
        val possibleRisk: String,
        val confidence: Int,
        val suggestedNextStep: String
    )

    suspend fun analyzeCropImage(cropCycleId: String, imageUri: String?): AnalysisResult
    suspend fun getObservationHistory(cropCycleId: String): List<CropObservation>
}

interface RiskService {
    suspend fun getActiveRisks(cropCycleId: String): List<RiskAlert>
    suspend fun getRiskDetails(riskId: String): RiskAlert?
    suspend fun resolveRisk(riskId: String): Boolean
}

interface WeatherService {
    suspend fun getCurrentWeather(location: String): WeatherRecord
    suspend fun get7DayForecast(location: String): List<WeatherRecord>
}

interface YieldService {
    suspend fun getYieldEstimate(cropCycleId: String): YieldEstimate
}

interface MarketService {
    suspend fun getMarketOptions(cropCycleId: String): List<MarketOption>
    suspend fun getMarketDetails(marketId: String): MarketOption?
}

interface AssistantService {
    suspend fun askAssistant(prompt: String, cropContext: CropContext): String
}

interface ReportService {
    suspend fun generateCropReport(cropContext: CropContext): CropReport
}

interface FarmerService {
    suspend fun getFarmerProfile(): Farmer
    suspend fun updateFarmerProfile(updated: Farmer): Farmer
}

interface FarmService {
    suspend fun getFarms(farmerId: String): List<Farm>
    suspend fun addFarm(farm: Farm): Farm
    suspend fun updateFarm(farm: Farm): Farm
}

interface NotificationService {
    suspend fun getNotifications(): List<NotificationItem>
    suspend fun markAsRead(notificationId: String): List<NotificationItem>
    suspend fun markAllAsRead(): List<NotificationItem>
}
