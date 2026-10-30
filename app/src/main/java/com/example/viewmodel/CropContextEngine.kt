package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import com.example.data.localization.AppLanguage
import com.example.data.localization.LocalizationManager
import com.example.services.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

/**
 * The Central Crop Context Engine
 * Coordinates state across all micro-features:
 * Farmer → Farm → Crop Cycle → Crop Stage → Previous Observations → Current Observations → Weather → Health Changes → Risk → Yield → Market
 */
class CropContextEngine(application: Application) : AndroidViewModel(application) {

    // Database
    private val db = AppDatabase.getDatabase(application, viewModelScope)
    private val dao = db.farmDao()

    // Services
    val cropPlannerService: CropPlannerService = MockCropPlannerService()
    val cropJourneyService: CropJourneyService = MockCropJourneyService()
    val healthService: HealthService = MockHealthService()
    val riskService: RiskService = MockRiskService()
    val weatherService: WeatherService = OpenMeteoWeatherService()
    val yieldService: YieldService = MockYieldService()
    val marketService: MarketService = MockMarketService()
    val assistantService: AssistantService = MockAssistantService()
    val reportService: ReportService = MockReportService()
    val farmerService: FarmerService = MockFarmerService()
    val farmService: FarmService = MockFarmService()
    val notificationService: NotificationService = MockNotificationService()

    // Central Crop Context State
    private val _cropContext = MutableStateFlow<CropContext>(createInitialContext())
    val cropContext: StateFlow<CropContext> = _cropContext.asStateFlow()

    // Session State (Logged out by default to trigger mandatory Onboarding & Login Setup)
    private val _session = MutableStateFlow<SessionEntity?>(
        SessionEntity(
            sessionKey = "KSN-NEW-IN",
            activeFarmerId = "",
            activeFarmId = "",
            isLoggedIn = false
        )
    )
    val session: StateFlow<SessionEntity?> = _session.asStateFlow()

    // Registered Farms List
    private val _farmsList = MutableStateFlow<List<Farm>>(emptyList())
    val farmsList: StateFlow<List<Farm>> = _farmsList.asStateFlow()

    // Assistant Chat Messages
    private val _assistantMessages = MutableStateFlow<List<AssistantMessage>>(
        listOf(
            AssistantMessage(
                sender = AssistantSender.ASSISTANT,
                text = "Vanakkam, Farmer! I am your Farm Companion. Your crop profile is active and synced. How can I assist your field decisions today?"
            )
        )
    )
    val assistantMessages: StateFlow<List<AssistantMessage>> = _assistantMessages.asStateFlow()

    private val _isAssistantLoading = MutableStateFlow(false)
    val isAssistantLoading: StateFlow<Boolean> = _isAssistantLoading.asStateFlow()

    // Journey stages list for timeline
    private val _journeyStages = MutableStateFlow<List<CropJourneyService.JourneyStage>>(emptyList())
    val journeyStages: StateFlow<List<CropJourneyService.JourneyStage>> = _journeyStages.asStateFlow()

    // Latest Generated Report
    private val _latestReport = MutableStateFlow<CropReport?>(null)
    val latestReport: StateFlow<CropReport?> = _latestReport.asStateFlow()

    // Global Language State persisted
    private val _currentLanguage = MutableStateFlow<AppLanguage>(
        LocalizationManager.getSavedLanguage(application)
    )
    val currentLanguage: StateFlow<AppLanguage> = _currentLanguage.asStateFlow()

    fun setLanguage(language: AppLanguage) {
        _currentLanguage.value = language
        LocalizationManager.saveLanguage(getApplication(), language)

        val initialGreeting = com.example.data.localization.Translations.smartTranslate(
            "Vanakkam, Farmer! I am your Farm Companion. Your crop profile is active and synced. How can I assist your field decisions today?",
            language
        )
        if (_assistantMessages.value.size == 1 && _assistantMessages.value.first().sender == AssistantSender.ASSISTANT) {
            _assistantMessages.value = listOf(AssistantMessage(sender = AssistantSender.ASSISTANT, text = initialGreeting))
        }

        val current = _cropContext.value
        val localizedLatestObs = current.latestObservation.copy(
            summary = com.example.data.localization.Translations.smartTranslate(current.latestObservation.summary, language),
            detail = com.example.data.localization.Translations.smartTranslate(current.latestObservation.detail, language),
            observedChanges = com.example.data.localization.Translations.smartTranslate(current.latestObservation.observedChanges, language),
            suggestedNextSteps = com.example.data.localization.Translations.smartTranslate(current.latestObservation.suggestedNextSteps, language),
            leafCondition = com.example.data.localization.Translations.smartTranslate(current.latestObservation.leafCondition, language)
        )
        val localizedPrevObs = current.previousObservation?.copy(
            summary = com.example.data.localization.Translations.smartTranslate(current.previousObservation.summary, language),
            detail = com.example.data.localization.Translations.smartTranslate(current.previousObservation.detail, language),
            observedChanges = com.example.data.localization.Translations.smartTranslate(current.previousObservation.observedChanges, language),
            suggestedNextSteps = com.example.data.localization.Translations.smartTranslate(current.previousObservation.suggestedNextSteps, language),
            leafCondition = com.example.data.localization.Translations.smartTranslate(current.previousObservation.leafCondition, language)
        )
        val localizedWeather = current.currentWeather.copy(
            cropImpactNotice = com.example.data.localization.Translations.smartTranslate(current.currentWeather.cropImpactNotice, language),
            recommendation = com.example.data.localization.Translations.smartTranslate(current.currentWeather.recommendation, language)
        )
        val localizedRisks = current.activeRisks.map { risk ->
            risk.copy(
                title = com.example.data.localization.Translations.smartTranslate(risk.title, language),
                whatWasObserved = com.example.data.localization.Translations.smartTranslate(risk.whatWasObserved, language),
                whyIsThisARisk = com.example.data.localization.Translations.smartTranslate(risk.whyIsThisARisk, language),
                recommendedOrganicAction = com.example.data.localization.Translations.smartTranslate(risk.recommendedOrganicAction, language)
            )
        }

        _cropContext.value = current.copy(
            farmer = current.farmer.copy(preferredLanguage = language.code),
            latestObservation = localizedLatestObs,
            previousObservation = localizedPrevObs,
            currentWeather = localizedWeather,
            activeRisks = localizedRisks
        )

        generateReport()
    }

    init {
        loadSessionAndFarms()
    }

    private fun createInitialContext(): CropContext {
        val farmer = Farmer()
        val farm = Farm()
        val cycle = CropCycle()
        val prevObs = CropObservation(
            id = "obs_prev_28",
            dayNumber = 28,
            dateLabel = "Day 28 • Previous Observation",
            stageName = "Vegetative Stage",
            healthScore = 86,
            summary = "Optimal vegetative growth with clean dark green leaves.",
            detail = "No pest spotting. Branching active. Stem thickness optimal. Moisture distribution steady.",
            riskLevel = "Low",
            confidencePercent = 94,
            observedChanges = "Clean green lamina with crisp margins.",
            suggestedNextSteps = "Continue scheduled drip irrigation cycles.",
            leafCondition = "Healthy green leaf lamina, crisp margins, no spotting.",
            weatherCondition = "Warm & Sunny",
            isCurrent = false,
            drawableResName = "img_during_cultivation"
        )

        val currObs = CropObservation(
            id = "obs_curr_35",
            dayNumber = 35,
            dateLabel = "Day 35 • Current Observation",
            stageName = "Vegetative Stage (Day 35)",
            healthScore = 71,
            summary = "Possible health change detected on lower leaves.",
            detail = "Subtle yellow margins and pinhead specks on bottom foliage. High humidity causes morning condensation wetness.",
            riskLevel = "Moderate",
            confidencePercent = 88,
            observedChanges = "Lower tier leaf margin chlorosis with small brown specks (early fungal moisture symptom).",
            suggestedNextSteps = "Prune bottom 2 leaves touching soil; apply 5% NSKE foliar spray early morning.",
            leafCondition = "Lower tier chlorosis, upper shoots vigorous.",
            weatherCondition = "High Humidity",
            isCurrent = true,
            drawableResName = "img_during_cultivation"
        )

        val weather = WeatherRecord(
            dayLabel = "Today",
            dateFormatted = "Live Weather • Coimbatore",
            temperatureC = 28,
            humidityPercent = 78,
            conditionTitle = "High Humidity & Cloud Cover",
            forecastText = "Rain Expected within 36 hours",
            rainfallChance = 65,
            windSpeedKmh = 14,
            cropImpactNotice = "Recent humidity (78%) and warm soil conditions may accelerate foliar moisture retention, increasing early fungal/blight risks in dense vegetative rows.",
            recommendation = "Inspect bottom leaves for moisture spotting, withhold evening foliar wetting, and ensure drainage furrows are clear.",
            sceneType = "humidity"
        )

        return CropContext(
            farmer = farmer,
            farm = farm,
            cropCycle = cycle,
            latestObservation = currObs,
            previousObservation = prevObs,
            recentObservations = listOf(currObs, prevObs),
            activeRisks = emptyList(),
            currentWeather = weather,
            weatherForecast = emptyList(),
            yieldEstimate = YieldEstimate(),
            marketOptions = emptyList(),
            notifications = emptyList()
        )
    }

    private fun loadSessionAndFarms() {
        viewModelScope.launch {
            _cropContext.value = _cropContext.value.copy(isLoading = true)

            // Try loading stored session
            launch {
                dao.getSession().collect { sess ->
                    if (sess != null) {
                        _session.value = sess
                    }
                }
            }

            // Load initial farmer and farms
            val farmer = farmerService.getFarmerProfile()
            val farms = farmService.getFarms(farmer.id)
            _farmsList.value = farms
            val defaultFarm = farms.find { it.isDefault } ?: farms.firstOrNull() ?: Farm()

            // Fetch live Open-Meteo weather
            val liveWeather = weatherService.getCurrentWeather(defaultFarm.location)
            val liveForecast = weatherService.get7DayForecast(defaultFarm.location)

            val observations = healthService.getObservationHistory("cycle_demo_tomato")
            val risks = riskService.getActiveRisks("cycle_demo_tomato")
            val yield = yieldService.getYieldEstimate("cycle_demo_tomato")
            val markets = marketService.getMarketOptions("cycle_demo_tomato")
            val notifs = notificationService.getNotifications()
            val stages = cropJourneyService.getCropJourney("cycle_demo_tomato")

            _journeyStages.value = stages
            val latest = observations.firstOrNull { it.isCurrent } ?: observations.first()
            val prev = observations.getOrNull(1)

            _cropContext.value = CropContext(
                farmer = farmer,
                farm = defaultFarm,
                cropCycle = CropCycle(
                    farmId = defaultFarm.id,
                    cropName = "Tomato",
                    variety = "Shivam / Pusa Ruby (Hybrid)",
                    acres = defaultFarm.landSizeAcres,
                    cropAgeDays = 35,
                    currentStageName = "03 MONITOR",
                    currentStageIndex = 3,
                    currentStageDescription = "Vegetative Growth & Canopy Monitoring"
                ),
                latestObservation = latest,
                previousObservation = prev,
                recentObservations = observations,
                activeRisks = risks,
                currentWeather = liveWeather,
                weatherForecast = liveForecast,
                yieldEstimate = yield,
                marketOptions = markets,
                notifications = notifs,
                isLoading = false,
                error = null
            )

            _latestReport.value = reportService.generateCropReport(_cropContext.value)
        }
    }

    fun completeOnboarding(
        farmer: Farmer,
        farm: Farm,
        cropCycle: CropCycle,
        sessionKey: String
    ) {
        viewModelScope.launch {
            _cropContext.value = _cropContext.value.copy(isLoading = true)

            // Persist to Room DB
            dao.insertFarmer(farmer)
            dao.insertFarm(farm)
            farmService.addFarm(farm)
            _farmsList.value = listOf(farm)

            val sessionEntity = SessionEntity(
                id = 1,
                sessionKey = sessionKey,
                activeFarmerId = farmer.id,
                activeFarmId = farm.id,
                isLoggedIn = true
            )
            dao.insertSession(sessionEntity)
            _session.value = sessionEntity

            // Fetch live Open-Meteo weather for farm location
            val liveWeather = weatherService.getCurrentWeather(farm.location)
            val liveForecast = weatherService.get7DayForecast(farm.location)

            // Initial observation matching crop and age
            val initialObs = CropObservation(
                id = "obs_curr_${cropCycle.cropAgeDays}",
                dayNumber = cropCycle.cropAgeDays,
                dateLabel = "Day ${cropCycle.cropAgeDays} • Field Setup Completed",
                stageName = cropCycle.currentStageName,
                healthScore = 92,
                summary = "Active crop cycle initialized for ${cropCycle.cropName} (${cropCycle.variety}).",
                detail = "Soil type ${farm.soilType} with ${farm.waterAvailability}. Soil moisture and initial crop canopy optimal.",
                riskLevel = "Low",
                confidencePercent = 95,
                observedChanges = "Clean seedling growth and sturdy root development.",
                suggestedNextSteps = "Follow ${cropCycle.currentStageName} schedule for ${cropCycle.cropName}.",
                leafCondition = "Vigorous green foliage with standard leaf pattern.",
                weatherCondition = liveWeather.conditionTitle,
                isCurrent = true,
                drawableResName = "img_during_cultivation"
            )
            dao.insertObservation(initialObs)

            val risks = riskService.getActiveRisks(cropCycle.id)
            val yield = yieldService.getYieldEstimate(cropCycle.id)
            val markets = marketService.getMarketOptions(cropCycle.id)
            val notifs = notificationService.getNotifications()
            val stages = cropJourneyService.getCropJourney(cropCycle.id)
            _journeyStages.value = stages

            _cropContext.value = CropContext(
                farmer = farmer,
                farm = farm,
                cropCycle = cropCycle,
                latestObservation = initialObs,
                previousObservation = null,
                recentObservations = listOf(initialObs),
                activeRisks = risks,
                currentWeather = liveWeather,
                weatherForecast = liveForecast,
                yieldEstimate = yield,
                marketOptions = markets,
                notifications = notifs,
                isLoading = false,
                error = null
            )

            generateReport()
        }
    }

    fun restoreSessionKey(sessionKey: String) {
        viewModelScope.launch {
            _cropContext.value = _cropContext.value.copy(isLoading = true)

            val sessionEntity = SessionEntity(
                id = 1,
                sessionKey = sessionKey,
                activeFarmerId = _cropContext.value.farmer.id,
                activeFarmId = _cropContext.value.farm.id,
                isLoggedIn = true
            )
            dao.insertSession(sessionEntity)
            _session.value = sessionEntity

            _cropContext.value = _cropContext.value.copy(isLoading = false)
        }
    }

    fun signOut() {
        viewModelScope.launch {
            val loggedOutSession = SessionEntity(
                id = 1,
                sessionKey = _session.value?.sessionKey ?: "KSN-8842-TN",
                activeFarmerId = _cropContext.value.farmer.id,
                activeFarmId = _cropContext.value.farm.id,
                isLoggedIn = false
            )
            dao.insertSession(loggedOutSession)
            _session.value = loggedOutSession
        }
    }

    fun loginWithMobileOrSessionKey(identifier: String, farmerName: String? = null) {
        viewModelScope.launch {
            _cropContext.value = _cropContext.value.copy(isLoading = true)

            val isSessionKey = identifier.startsWith("KSN-") || identifier.length > 8
            val newSessionKey = if (isSessionKey) identifier else "KSN-${(1000..9999).random()}-IN"
            val farmerId = "farmer_${identifier.filter { it.isLetterOrDigit() }}"

            val loggedInFarmer = Farmer(
                id = farmerId,
                name = farmerName ?: if (identifier.length > 5) "Farmer ($identifier)" else "Murugan (Farmer)",
                phone = if (!isSessionKey) identifier else "+91 98765 43210",
                preferredLanguage = _currentLanguage.value.name,
                location = _cropContext.value.farm.location,
                profileCompletionPercent = 95
            )

            dao.insertFarmer(loggedInFarmer)
            val newSession = SessionEntity(
                id = 1,
                sessionKey = newSessionKey,
                activeFarmerId = farmerId,
                activeFarmId = _cropContext.value.farm.id,
                isLoggedIn = true
            )
            dao.insertSession(newSession)
            _session.value = newSession

            // Refresh live weather for farmer's location
            val liveWeather = weatherService.getCurrentWeather(loggedInFarmer.location)
            val liveForecast = weatherService.get7DayForecast(loggedInFarmer.location)

            _cropContext.value = _cropContext.value.copy(
                farmer = loggedInFarmer,
                currentWeather = liveWeather,
                weatherForecast = liveForecast,
                isLoading = false
            )
        }
    }

    fun addNewFarmParcel(
        farmName: String,
        location: String,
        landSizeAcres: Double,
        soilType: String,
        waterAvailability: String,
        cropName: String
    ) {
        viewModelScope.launch {
            _cropContext.value = _cropContext.value.copy(isLoading = true)

            val newFarmId = "farm_${UUID.randomUUID().toString().take(8)}"
            val newFarm = Farm(
                id = newFarmId,
                farmerId = _cropContext.value.farmer.id,
                farmName = farmName.ifBlank { "New Parcel ($location)" },
                location = location.ifBlank { "Coimbatore, Tamil Nadu" },
                landSizeAcres = if (landSizeAcres > 0) landSizeAcres else 2.0,
                soilType = soilType.ifBlank { "Red Sandy Loam" },
                waterAvailability = waterAvailability.ifBlank { "Drip Irrigation" },
                coordinates = "11.0168° N, 76.9558° E",
                isDefault = true
            )

            // Save to Room DB and local service
            dao.insertFarm(newFarm)
            farmService.addFarm(newFarm)
            _farmsList.value = _farmsList.value + newFarm

            // Fetch live weather for the new farm's real location
            val liveWeather = weatherService.getCurrentWeather(newFarm.location)
            val liveForecast = weatherService.get7DayForecast(newFarm.location)

            val updatedCycle = CropCycle(
                id = "cycle_${UUID.randomUUID().toString().take(6)}",
                farmId = newFarmId,
                cropName = cropName.ifBlank { "Tomato" },
                variety = "Selected Hybrid Variety",
                acres = newFarm.landSizeAcres,
                cropAgeDays = 25,
                currentStageName = "02 GROW",
                currentStageIndex = 2,
                currentStageDescription = "Vegetative Care & Fertigation"
            )

            _cropContext.value = _cropContext.value.copy(
                farm = newFarm,
                cropCycle = updatedCycle,
                currentWeather = liveWeather,
                weatherForecast = liveForecast,
                isLoading = false
            )

            generateReport()
        }
    }

    fun selectCropStage(stageIndex: Int) {
        val stageNames = mapOf(
            1 to ("01 PLAN" to "Crop Planning & Soil Setup"),
            2 to ("02 GROW" to "Stage-wise Growth Guidance"),
            3 to ("03 MONITOR" to "Crop Health & Leaf Monitoring"),
            4 to ("04 PROTECT" to "Risk Advisory & Plant Hygiene"),
            5 to ("05 HARVEST" to "Yield & Harvest Forecast"),
            6 to ("06 SELL" to "Smart Selling & Markets")
        )
        val info = stageNames[stageIndex] ?: ("03 MONITOR" to "Crop Monitoring")
        val current = _cropContext.value
        _cropContext.value = current.copy(
            cropCycle = current.cropCycle.copy(
                currentStageIndex = stageIndex,
                currentStageName = info.first,
                currentStageDescription = info.second
            )
        )
    }

    fun simulateNewObservation(imageUri: String? = null) {
        viewModelScope.launch {
            _cropContext.value = _cropContext.value.copy(isLoading = true)
            val analysis = healthService.analyzeCropImage(_cropContext.value.cropCycle.id, imageUri)
            val updatedList = listOf(analysis.currentObservation, analysis.previousObservation)

            _cropContext.value = _cropContext.value.copy(
                latestObservation = analysis.currentObservation,
                previousObservation = analysis.previousObservation,
                recentObservations = updatedList,
                isLoading = false
            )
        }
    }

    fun recordObservation(observation: CropObservation) {
        viewModelScope.launch {
            dao.insertObservation(observation)
            val previous = _cropContext.value.latestObservation
            val updatedRecent = listOf(observation) + _cropContext.value.recentObservations
            _cropContext.value = _cropContext.value.copy(
                latestObservation = observation,
                previousObservation = previous,
                recentObservations = updatedRecent
            )
        }
    }

    fun sendAssistantPrompt(prompt: String) {
        val userMsg = AssistantMessage(sender = AssistantSender.USER, text = prompt)
        _assistantMessages.value = _assistantMessages.value + userMsg

        viewModelScope.launch {
            _isAssistantLoading.value = true
            val crop = _cropContext.value
            val contextSummary = """
                Farmer: ${crop.farmer.name} (${crop.farmer.phone})
                Farm: ${crop.farm.farmName} in ${crop.farm.location} (${crop.farm.landSizeAcres} Acres, ${crop.farm.soilType})
                Crop: ${crop.cropCycle.cropName} (${crop.cropCycle.variety}), Age: Day ${crop.cropCycle.cropAgeDays} (${crop.cropCycle.currentStageName})
                Live Weather: ${crop.currentWeather.temperatureC}°C, ${crop.currentWeather.humidityPercent}% Humidity, ${crop.currentWeather.conditionTitle}
                Recent Observation: ${crop.latestObservation.summary} (Health Score: ${crop.latestObservation.healthScore}%)
                Active Risks: ${crop.activeRisks.joinToString { it.title }}
            """.trimIndent()

            val reply = GeminiService.askCompanion(
                userPrompt = prompt,
                cropContext = contextSummary,
                languageCode = _currentLanguage.value.code
            )
            _assistantMessages.value = _assistantMessages.value + AssistantMessage(
                sender = AssistantSender.ASSISTANT,
                text = reply
            )
            _isAssistantLoading.value = false
        }
    }

    fun updateFarmerProfile(name: String, phone: String, language: String, location: String) {
        viewModelScope.launch {
            val updated = _cropContext.value.farmer.copy(
                name = name,
                phone = phone,
                preferredLanguage = language,
                location = location
            )
            dao.insertFarmer(updated)
            val saved = farmerService.updateFarmerProfile(updated)

            // Re-fetch live weather if location changed
            val liveWeather = weatherService.getCurrentWeather(location)
            val liveForecast = weatherService.get7DayForecast(location)

            _cropContext.value = _cropContext.value.copy(
                farmer = saved,
                currentWeather = liveWeather,
                weatherForecast = liveForecast
            )
        }
    }

    fun switchFarm(farm: Farm) {
        viewModelScope.launch {
            _cropContext.value = _cropContext.value.copy(isLoading = true)

            // Fetch real Open-Meteo weather for the selected farm parcel
            val liveWeather = weatherService.getCurrentWeather(farm.location)
            val liveForecast = weatherService.get7DayForecast(farm.location)

            _cropContext.value = _cropContext.value.copy(
                farm = farm,
                cropCycle = _cropContext.value.cropCycle.copy(
                    farmId = farm.id,
                    acres = farm.landSizeAcres
                ),
                currentWeather = liveWeather,
                weatherForecast = liveForecast,
                isLoading = false
            )
        }
    }

    fun markNotificationRead(id: String) {
        viewModelScope.launch {
            val updated = notificationService.markAsRead(id)
            _cropContext.value = _cropContext.value.copy(notifications = updated)
        }
    }

    fun markAllNotificationsRead() {
        viewModelScope.launch {
            val updated = notificationService.markAllAsRead()
            _cropContext.value = _cropContext.value.copy(notifications = updated)
        }
    }

    fun toggleOfflineMode() {
        _cropContext.value = _cropContext.value.copy(
            isOffline = !_cropContext.value.isOffline
        )
    }

    fun generateReport() {
        viewModelScope.launch {
            val rep = reportService.generateCropReport(_cropContext.value)
            _latestReport.value = rep
        }
    }

    fun resolveRisk(riskId: String) {
        viewModelScope.launch {
            riskService.resolveRisk(riskId)
            val updatedRisks = riskService.getActiveRisks(_cropContext.value.cropCycle.id)
            _cropContext.value = _cropContext.value.copy(activeRisks = updatedRisks)
        }
    }
}
