package com.example.services

import com.example.data.*
import kotlinx.coroutines.delay

class MockCropPlannerService : CropPlannerService {
    override suspend fun getCropRecommendations(input: CropPlannerService.CropPlanInput): CropPlannerService.CropPlanResult {
        delay(200)
        return CropPlannerService.CropPlanResult(
            recommendedCrop = "Tomato",
            variety = "Shivam / Pusa Ruby (Determinate Hybrid)",
            suitability = "High (Recommended)",
            reason = "Based on the available location (Coimbatore), current season, water and red sandy loam soil information.",
            growingPeriodDays = "75 – 85 Days",
            estimatedCost = "₹42,000 (for 2 Acres)",
            expectedYieldRange = "5.2 – 6.1 Tonnes",
            alternativeCrops = listOf(
                CropPlannerService.AlternativeCrop(
                    name = "Chilli (G4 / Hybrid)",
                    suitability = "Moderate",
                    reason = "Extended picking cycle (120 days); requires higher upfront labor.",
                    estimatedCost = "₹55,000",
                    yieldRange = "3.2 – 4.0 Tonnes"
                ),
                CropPlannerService.AlternativeCrop(
                    name = "Brinjal (Annamalai)",
                    suitability = "Moderate",
                    reason = "Resilient in red loam, but local mandi price volatility is higher.",
                    estimatedCost = "₹38,000",
                    yieldRange = "6.0 – 7.5 Tonnes"
                )
            )
        )
    }
}

class MockCropJourneyService : CropJourneyService {
    override suspend fun getCropJourney(cropCycleId: String): List<CropJourneyService.JourneyStage> {
        delay(150)
        return listOf(
            CropJourneyService.JourneyStage(
                stageNumber = 1,
                stageCode = "01 PLAN",
                title = "Choose the right crop",
                subtitle = "Soil suitability, seed selection & field budgeting",
                description = "Deep summer ploughing, bed laying, drip installation, and seedling procurement.",
                status = "Completed",
                milestones = listOf(
                    CropJourneyService.StageMilestone("Day 0", "Transplanting in Main Field", "Transplanted 4,800 healthy nursery seedlings on raised beds with mulch.", true),
                    CropJourneyService.StageMilestone("Day 7", "Root Establishment & Drip Check", "Uniform emitter discharge verified at 2 LPH across 2 acres.", true)
                )
            ),
            CropJourneyService.JourneyStage(
                stageNumber = 2,
                stageCode = "02 GROW",
                title = "Follow stage-wise guidance",
                subtitle = "Vegetative care, fertigation & canopy trellising",
                description = "Nutrient fertigation, weed management, and gentle staking support.",
                status = "Completed",
                milestones = listOf(
                    CropJourneyService.StageMilestone("Day 15", "First Side Dressing & Weeding", "Applied neem cake + vermicompost; removed competing inter-bed weeds.", true),
                    CropJourneyService.StageMilestone("Day 30", "Bamboo Staking & Pruning", "Tied active lateral stems with jute twine to encourage vertical airflow.", true)
                )
            ),
            CropJourneyService.JourneyStage(
                stageNumber = 3,
                stageCode = "03 MONITOR",
                title = "Track crop changes",
                subtitle = "Upload crop photos & detect early leaf symptoms",
                description = "Periodic foliar scanning to catch microclimate fungal stress early.",
                status = "In Progress",
                milestones = listOf(
                    CropJourneyService.StageMilestone("Day 28", "Observation: Clean Foliage", "86% Health Score. Active vegetative growth with crisp margins.", true),
                    CropJourneyService.StageMilestone("Day 35", "Observation: Lower Foliage Stress", "71% Health Score. Detected moisture spotting on bottom 2 tiers.", false)
                )
            ),
            CropJourneyService.JourneyStage(
                stageNumber = 4,
                stageCode = "04 PROTECT",
                title = "Identify possible risks",
                subtitle = "High humidity advisory & preventative biological spray",
                description = "Preventive measures against early blight spores and humid canopy wetness.",
                status = "Action Needed",
                milestones = listOf(
                    CropJourneyService.StageMilestone("Day 36", "Organic Foliar Spray", "Apply 5% NSKE (Neem Seed Kernel) or Panchagavya (30ml/L) in early morning.", false),
                    CropJourneyService.StageMilestone("Day 42", "Pre-Flowering Health Recheck", "Verify that new upper shoot tiers remain spotless.", false)
                )
            ),
            CropJourneyService.JourneyStage(
                stageNumber = 5,
                stageCode = "05 HARVEST",
                title = "Estimate your yield",
                subtitle = "5.2 – 6.1 tonnes forecast & picking schedule",
                description = "Fruit maturity monitoring, crate preparation, and breaker-stage picking.",
                status = "Upcoming",
                milestones = listOf(
                    CropJourneyService.StageMilestone("Day 60", "First Flush Flowering & Fruit Set", "Monitor blossom end rot and calcium uptake during fruit enlargement.", false),
                    CropJourneyService.StageMilestone("Day 75", "First Breaker Harvest Picking", "Target 1.5 tonnes initial picking for fresh consumption markets.", false),
                    CropJourneyService.StageMilestone("Day 90", "Peak Harvest & Yield Finalization", "Final bulk pickings across 2 acres.", false)
                )
            ),
            CropJourneyService.JourneyStage(
                stageNumber = 6,
                stageCode = "06 SELL",
                title = "Find suitable markets",
                subtitle = "Uzhavar Sandhai vs Wholesale Mandi rate comparison",
                description = "Comparing spot prices, distance, demand, and transport logistics.",
                status = "Upcoming",
                milestones = listOf(
                    CropJourneyService.StageMilestone("Day 74", "Mandi Spot Rate Check", "Compare Coimbatore Uzhavar Sandhai direct quotes vs Mettupalayam Mandi.", false),
                    CropJourneyService.StageMilestone("Day 76", "Market Dispatch & Sale", "Direct dispatch at 4:30 AM for peak morning retail demand.", false)
                )
            )
        )
    }
}

class MockHealthService : HealthService {
    override suspend fun analyzeCropImage(cropCycleId: String, imageUri: String?): HealthService.AnalysisResult {
        delay(400)
        val prev = CropObservation(
            id = "obs_prev_28",
            dayNumber = 28,
            dateLabel = "Day 28 • Previous Observation",
            stageName = "Vegetative Stage",
            healthScore = 86,
            summary = "Optimal vegetative growth with clean dark green leaves.",
            detail = "No pest spotting. Branching active. Stem thickness optimal. Moisture distribution steady.",
            riskLevel = "Low",
            confidencePercent = 94,
            observedChanges = "Baseline healthy foliage. Strong leaf lamina, clear veins.",
            suggestedNextSteps = "Continue scheduled drip cycles and weekly foliar Jeevamrit application.",
            leafCondition = "Crisp green leaf lamina, no chlorosis, no fungal spotting.",
            weatherCondition = "Warm & Sunny (29°C, 58% Humidity)",
            isCurrent = false,
            drawableResName = "img_during_cultivation"
        )

        val curr = CropObservation(
            id = "obs_curr_35",
            dayNumber = 35,
            dateLabel = "Day 35 • Current Observation",
            stageName = "Vegetative Stage (Day 35)",
            healthScore = 71,
            summary = "Possible health change detected on lower leaves.",
            detail = "Early moisture stress signs and slight yellow margins observed on lower tier foliage. Attributed to high humidity (78%) and morning canopy wetness.",
            riskLevel = "Moderate",
            confidencePercent = 88,
            observedChanges = "Lower tier leaf margin chlorosis with small brown specks (early fungal moisture symptom).",
            suggestedNextSteps = "Prune lowest leaves touching damp soil; apply 5% NSKE spray early morning.",
            leafCondition = "Lower leaves show mild margin yellowing; upper canopy shoots remain vigorous.",
            weatherCondition = "High Humidity (28°C, 78% Humidity, Rain Expected)",
            isCurrent = true,
            drawableResName = "img_during_cultivation"
        )

        return HealthService.AnalysisResult(
            previousObservation = prev,
            currentObservation = curr,
            healthScoreChange = "86% → 71%",
            statusMessage = "Possible health change detected",
            observedChanges = "Slight lower leaf chlorosis and moisture retention spotting detected on bottom foliage tiers.",
            possibleRisk = "Moderate Risk (Foliar moisture stress / Early Blight precursor)",
            confidence = 88,
            suggestedNextStep = "Check affected leaves, prune soil-touching shoots, and apply organic biological foliar spray."
        )
    }

    override suspend fun getObservationHistory(cropCycleId: String): List<CropObservation> {
        delay(150)
        return listOf(
            CropObservation(
                id = "obs_curr_35",
                dayNumber = 35,
                dateLabel = "Day 35 • Today's Observation",
                stageName = "Vegetative Stage (Day 35)",
                healthScore = 71,
                summary = "Possible health change detected on lower leaves.",
                detail = "Early moisture stress signs on bottom leaves under 78% humidity.",
                riskLevel = "Moderate",
                confidencePercent = 88,
                observedChanges = "Lower leaf margin chlorosis with small brown specks.",
                suggestedNextSteps = "Prune bottom 2 leaves; spray 5% NSKE early morning.",
                leafCondition = "Mild lower leaf chlorosis, upper shoots vigorous.",
                weatherCondition = "High Humidity (78%)",
                isCurrent = true,
                drawableResName = "img_during_cultivation"
            ),
            CropObservation(
                id = "obs_prev_28",
                dayNumber = 28,
                dateLabel = "Day 28 • Previous Observation",
                stageName = "Vegetative Stage",
                healthScore = 86,
                summary = "Optimal vegetative growth with clean dark green leaves.",
                detail = "No pest spotting. Branching active. Stem thickness optimal.",
                riskLevel = "Low",
                confidencePercent = 94,
                observedChanges = "Clean green lamina with crisp margins.",
                suggestedNextSteps = "Continue drip irrigation and bamboo support.",
                leafCondition = "Healthy green leaf lamina.",
                weatherCondition = "Sunny & Warm (58% Humidity)",
                isCurrent = false,
                drawableResName = "img_during_cultivation"
            ),
            CropObservation(
                id = "obs_past_15",
                dayNumber = 15,
                dateLabel = "Day 15 • Early Vegetative",
                stageName = "Early Vegetative",
                healthScore = 90,
                summary = "Seedlings well established after transplanting.",
                detail = "Strong root anchoring and first side branching observed.",
                riskLevel = "Low",
                confidencePercent = 96,
                observedChanges = "Healthy dark green seedling growth.",
                suggestedNextSteps = "Apply first side dressing of compost.",
                leafCondition = "Uniform young foliage.",
                weatherCondition = "Sunny (28°C)",
                isCurrent = false,
                drawableResName = "img_during_cultivation"
            )
        )
    }
}

class MockRiskService : RiskService {
    private val risks = mutableListOf(
        RiskAlert(
            id = "risk_1",
            riskType = "Disease Risk",
            title = "Foliar Humidity Stress & Early Blight Precursor",
            severity = "Moderate",
            whatWasObserved = "Lower leaf margins showed subtle yellow chlorosis and pinhead-sized brown specks on Day 35 scan.",
            whyIsThisARisk = "Combined with 78% relative humidity and night condensation, moisture on leaf surfaces permits Alternaria fungal spore germination within 8-12 hours.",
            whatInfoContributed = "Image Scan (Day 35) + Weather Sensor (78% humidity, rain forecast) + Crop Stage (Vegetative dense canopy).",
            whatToInspectNext = "Turn over bottom leaves to check for water-soaked concentric ring spots or dark brown discoloration near soil level.",
            recommendedOrganicAction = "1. Prune lowest 2 soil-touching leaves to improve furrow airflow.\n2. Spray 5% Neem Seed Kernel Extract (NSKE) or Panchagavya (30ml/L) tomorrow morning.\n3. Avoid evening sprinkler/foliar irrigation."
        ),
        RiskAlert(
            id = "risk_2",
            riskType = "Weather Stress",
            title = "Soil Saturation & Drainage Precaution",
            severity = "Low",
            whatWasObserved = "Rain predicted in Coimbatore region over next 36 hours.",
            whyIsThisARisk = "Tomato root systems are sensitive to standing water; root hypoxia can impair calcium uptake causing blossom end issues later.",
            whatInfoContributed = "Weather Forecast (65% rain probability, 14mm) + Soil Profile (Red Sandy Loam).",
            whatToInspectNext = "Check bed ends and drainage furrows to ensure excess surface runoff exits field freely.",
            recommendedOrganicAction = "Clear furrow outlets and suspend drip irrigation until 24 hours post-rain."
        )
    )

    override suspend fun getActiveRisks(cropCycleId: String): List<RiskAlert> {
        delay(150)
        return risks
    }

    override suspend fun getRiskDetails(riskId: String): RiskAlert? {
        return risks.find { it.id == riskId }
    }

    override suspend fun resolveRisk(riskId: String): Boolean {
        val index = risks.indexOfFirst { it.id == riskId }
        if (index != -1) {
            risks[index] = risks[index].copy(isResolved = true)
            return true
        }
        return false
    }
}

class MockWeatherService : WeatherService {
    override suspend fun getCurrentWeather(location: String): WeatherRecord {
        delay(100)
        return WeatherRecord(
            dayLabel = "Today",
            dateFormatted = "Monday, Sep 28",
            temperatureC = 28,
            humidityPercent = 78,
            conditionTitle = "High Humidity & Cloud Cover",
            forecastText = "Rain Expected within 36 hours",
            rainfallChance = 65,
            windSpeedKmh = 14,
            cropImpactNotice = "Recent humidity (78%) and warm soil conditions may accelerate foliar moisture retention, increasing early fungal/blight risks in dense vegetative tomato rows.",
            recommendation = "Inspect bottom leaves for moisture spotting, withhold evening foliar wetting, and ensure drainage furrows are clear.",
            sceneType = "humidity"
        )
    }

    override suspend fun get7DayForecast(location: String): List<WeatherRecord> {
        delay(150)
        return listOf(
            WeatherRecord("Today", "Sep 28", 28, 78, "High Humidity", "Rain Expected overnight", 65, 14, "Moisture stress alert for lower leaves", "Inspect leaf undersides & clear drainage", "humidity"),
            WeatherRecord("Tomorrow", "Sep 29", 26, 85, "Moderate Rain", "Scattered rain showers (14mm)", 80, 16, "Hold off foliar chemical/organic spraying", "Ensure field runoff channels are open", "rain"),
            WeatherRecord("Wednesday", "Sep 30", 27, 72, "Passing Clouds", "Partly cloudy with brief sun", 35, 12, "Favorable condition for morning aerating", "Perform bio-spray (NSKE 5%) after leaf dries", "humidity"),
            WeatherRecord("Thursday", "Oct 01", 29, 64, "Sunny & Warm", "Clear skies with light breeze", 15, 10, "Ideal photosynthetic assimilation", "Resume regular 2-hour drip cycle", "sunny"),
            WeatherRecord("Friday", "Oct 02", 30, 60, "Sunny", "Optimal daytime sunshine", 10, 11, "Good vegetative growth", "Bamboo trellising check", "sunny"),
            WeatherRecord("Saturday", "Oct 03", 29, 62, "Mild Breeze", "Clear morning, afternoon clouds", 20, 13, "Stable conditions", "Soil moisture check at 15cm depth", "sunny"),
            WeatherRecord("Sunday", "Oct 04", 28, 68, "Partly Cloudy", "Comfortable agricultural weather", 25, 12, "Normal development", "Prepare weekly Jeevamrit fertigation", "sunny")
        )
    }
}

class MockYieldService : YieldService {
    override suspend fun getYieldEstimate(cropCycleId: String): YieldEstimate {
        delay(150)
        return YieldEstimate(
            cropCycleId = cropCycleId,
            landAcres = 2.0,
            cropName = "Tomato",
            estimatedCostTotal = 42000,
            costSeeds = 8500,
            costDripIrrigation = 11000,
            costOrganicNutrients = 9500,
            costLaborStaking = 13000,
            expectedYieldMinTonnes = 5.2,
            expectedYieldMaxTonnes = 6.1,
            potentialRevenueMin = 140000,
            potentialRevenueMax = 185000,
            estimatedNetReturnMin = 98000,
            estimatedNetReturnMax = 143000,
            mandiRateMinPerKg = 28,
            mandiRateMaxPerKg = 36
        )
    }
}

class MockMarketService : MarketService {
    override suspend fun getMarketOptions(cropCycleId: String): List<MarketOption> {
        delay(150)
        return listOf(
            MarketOption(
                id = "mkt_1",
                marketName = "Coimbatore Uzhavar Sandhai",
                distanceKm = 12,
                pricePerKg = 36,
                demandLevel = "High Demand",
                transportCostText = "Low (₹1.20/kg)",
                transportCostNum = 1.20,
                netRealizationNum = 34.80,
                marketType = "Direct Farmer Market",
                suitabilityBadge = "Most Profitable",
                recommendationNote = "Direct sale to consumers with zero middlemen commissions. Yields highest net return."
            ),
            MarketOption(
                id = "mkt_2",
                marketName = "Mettupalayam Wholesale Mandi",
                distanceKm = 28,
                pricePerKg = 32,
                demandLevel = "Very High",
                transportCostText = "Medium (₹2.40/kg)",
                transportCostNum = 2.40,
                netRealizationNum = 29.60,
                marketType = "Wholesale Mandi",
                suitabilityBadge = "Bulk Offtake",
                recommendationNote = "Ideal for rapid clearance of entire multi-tonne harvest within 2 hours."
            ),
            MarketOption(
                id = "mkt_3",
                marketName = "Pollachi APMC Market Yard",
                distanceKm = 42,
                pricePerKg = 38,
                demandLevel = "High Demand",
                transportCostText = "Higher (₹3.60/kg)",
                transportCostNum = 3.60,
                netRealizationNum = 34.40,
                marketType = "APMC Yard",
                suitabilityBadge = "Highest Spot Price",
                recommendationNote = "Highest gross rate, but factor in 42km transport fuel and 1.5% APMC market cess."
            ),
            MarketOption(
                id = "mkt_4",
                marketName = "Local Farmgate Aggregator",
                distanceKm = 0,
                pricePerKg = 29,
                demandLevel = "Moderate",
                transportCostText = "Zero (₹0.00)",
                transportCostNum = 0.0,
                netRealizationNum = 29.00,
                marketType = "Farmgate Pickup",
                suitabilityBadge = "Zero Effort",
                recommendationNote = "Trader collects directly at your field. Zero transport effort, but lowest per-kg margin."
            )
        )
    }

    override suspend fun getMarketDetails(marketId: String): MarketOption? {
        return getMarketOptions("cycle_demo_tomato").find { it.id == marketId }
    }
}

class MockAssistantService : AssistantService {
    override suspend fun askAssistant(prompt: String, cropContext: CropContext): String {
        val cropInfo = """
            Crop: ${cropContext.cropCycle.cropName} (${cropContext.cropCycle.acres} Acres in ${cropContext.farm.location})
            Age: Day ${cropContext.cropCycle.cropAgeDays} (${cropContext.cropCycle.currentStageName})
            Health Score: ${cropContext.latestObservation.healthScore}% (Previous: ${cropContext.previousObservation?.healthScore ?: 86}%)
            Weather: ${cropContext.currentWeather.temperatureC}°C, Humidity ${cropContext.currentWeather.humidityPercent}%, ${cropContext.currentWeather.conditionTitle}
            Estimated Yield: ${cropContext.yieldEstimate.expectedYieldMinTonnes} - ${cropContext.yieldEstimate.expectedYieldMaxTonnes} Tonnes
        """.trimIndent()

        return com.example.data.GeminiService.askCompanion(prompt, cropInfo)
    }
}

class MockReportService : ReportService {
    override suspend fun generateCropReport(cropContext: CropContext): CropReport {
        delay(250)
        return CropReport(
            id = "report_tomato_${System.currentTimeMillis()}",
            generatedDate = "Sep 28, 2026",
            farmerName = cropContext.farmer.name,
            farmName = cropContext.farm.farmName,
            cropName = cropContext.cropCycle.cropName,
            variety = cropContext.cropCycle.variety,
            location = cropContext.farm.location,
            landSizeAcres = cropContext.cropCycle.acres,
            cropAgeDays = cropContext.cropCycle.cropAgeDays,
            currentStage = "${cropContext.cropCycle.currentStageName} (${cropContext.cropCycle.currentStageDescription})",
            healthScore = cropContext.latestObservation.healthScore,
            previousHealthScore = cropContext.previousObservation?.healthScore ?: 86,
            currentRisk = cropContext.activeRisks.firstOrNull()?.title ?: "Moderate Risk",
            weatherSnapshot = "${cropContext.currentWeather.temperatureC}°C • ${cropContext.currentWeather.humidityPercent}% Humidity • ${cropContext.currentWeather.forecastText}",
            yieldProjection = "${cropContext.yieldEstimate.expectedYieldMinTonnes} – ${cropContext.yieldEstimate.expectedYieldMaxTonnes} tonnes (Estimated Net: ₹${cropContext.yieldEstimate.estimatedNetReturnMin} – ₹${cropContext.yieldEstimate.estimatedNetReturnMax})",
            topMarketRecommendation = cropContext.marketOptions.firstOrNull()?.let { "${it.marketName} (${it.distanceKm} km, ₹${it.pricePerKg}/kg)" } ?: "Coimbatore Uzhavar Sandhai",
            aiSummary = "Your tomato crop is currently in the vegetative stage (Day 35). Recent observations show a 15% health score change on lower foliage under 78% humidity. Timely morning pruning and 5% NSKE spray will protect yield before upcoming rain."
        )
    }
}

class MockFarmerService : FarmerService {
    private var farmer = Farmer()

    override suspend fun getFarmerProfile(): Farmer = farmer

    override suspend fun updateFarmerProfile(updated: Farmer): Farmer {
        farmer = updated
        return farmer
    }
}

class MockFarmService : FarmService {
    private val farms = mutableListOf(
        Farm(
            id = "farm_demo_1",
            farmerId = "farmer_demo_1",
            farmName = "Coimbatore Green Valley Farm",
            location = "Coimbatore, Tamil Nadu",
            landSizeAcres = 2.0,
            soilType = "Red Sandy Loam (pH 6.8)",
            waterAvailability = "Borewell + Drip Irrigation",
            coordinates = "11.0168° N, 76.9558° E",
            isDefault = true
        ),
        Farm(
            id = "farm_demo_2",
            farmerId = "farmer_demo_1",
            farmName = "Pollachi Coconut & Veggie Orchard",
            location = "Pollachi, Tamil Nadu",
            landSizeAcres = 3.5,
            soilType = "Black Clay Loam",
            waterAvailability = "Canal + Well Irrigation",
            coordinates = "10.6609° N, 77.0048° E",
            isDefault = false
        )
    )

    override suspend fun getFarms(farmerId: String): List<Farm> = farms

    override suspend fun addFarm(farm: Farm): Farm {
        farms.add(farm)
        return farm
    }

    override suspend fun updateFarm(farm: Farm): Farm {
        val idx = farms.indexOfFirst { it.id == farm.id }
        if (idx != -1) {
            farms[idx] = farm
        }
        return farm
    }
}

class MockNotificationService : NotificationService {
    private val notifications = mutableListOf(
        NotificationItem(
            id = "notif_1",
            title = "Possible health change detected",
            message = "Day 35 scan indicates mild chlorosis on lower foliage under 78% humidity.",
            category = NotificationCategory.ALERT,
            timeAgo = "10m ago",
            timestamp = "10m ago",
            isRead = false,
            targetScreen = "HEALTH"
        ),
        NotificationItem(
            id = "notif_2",
            title = "Rain expected within 36 hours",
            message = "Scattered showers predicted for Coimbatore region. Check field furrow drainage.",
            category = NotificationCategory.WEATHER,
            timeAgo = "1h ago",
            timestamp = "1h ago",
            isRead = false,
            targetScreen = "WEATHER"
        ),
        NotificationItem(
            id = "notif_3",
            title = "Day 35 Staking & Trellising",
            message = "Tie active tomato branches with soft jute to optimize light penetration.",
            category = NotificationCategory.GUIDANCE,
            timeAgo = "4h ago",
            timestamp = "4h ago",
            isRead = true,
            targetScreen = "JOURNEY"
        ),
        NotificationItem(
            id = "notif_4",
            title = "Uzhavar Sandhai tomato rate updated",
            message = "Daily spot price quotes ₹36/kg for fresh breaker grade produce.",
            category = NotificationCategory.MARKET,
            timeAgo = "6h ago",
            timestamp = "6h ago",
            isRead = true,
            targetScreen = "SELLING"
        ),
        NotificationItem(
            id = "notif_5",
            title = "Crop Context Synced",
            message = "Tomato (2 Acres) active dataset synchronized with AI Farm Companion.",
            category = NotificationCategory.SYSTEM,
            timeAgo = "1d ago",
            timestamp = "1d ago",
            isRead = true,
            targetScreen = "DASHBOARD"
        )
    )

    override suspend fun getNotifications(): List<NotificationItem> = notifications

    override suspend fun markAsRead(notificationId: String): List<NotificationItem> {
        val idx = notifications.indexOfFirst { it.id == notificationId }
        if (idx != -1) {
            notifications[idx] = notifications[idx].copy(isRead = true)
        }
        return notifications
    }

    override suspend fun markAllAsRead(): List<NotificationItem> {
        for (i in notifications.indices) {
            notifications[i] = notifications[i].copy(isRead = true)
        }
        return notifications
    }
}
