package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AiChatMessage
import com.example.data.AppDatabase
import com.example.data.CropObservation
import com.example.data.CropProfile
import com.example.data.FarmTask
import com.example.data.GeminiService
import com.example.data.MarketOption
import com.example.data.MessageSender
import com.example.data.WeatherConditionData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FarmViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getDatabase(application, viewModelScope)
    private val dao = db.farmDao()

    val cropProfile: StateFlow<CropProfile?> = dao.getCropProfile()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            CropProfile()
        )

    val observations: StateFlow<List<CropObservation>> = dao.getAllObservations()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    val tasks: StateFlow<List<FarmTask>> = dao.getAllTasks()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    val markets: StateFlow<List<MarketOption>> = dao.getAllMarkets()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    private val _weather = MutableStateFlow(WeatherConditionData())
    val weather: StateFlow<WeatherConditionData> = _weather.asStateFlow()

    private val _activeStageIndex = MutableStateFlow(2) // 1: Plan, 2: Grow, 3: Monitor, 4: Protect, 5: Harvest, 6: Sell
    val activeStageIndex: StateFlow<Int> = _activeStageIndex.asStateFlow()

    private val _chatMessages = MutableStateFlow<List<AiChatMessage>>(
        listOf(
            AiChatMessage(
                sender = MessageSender.COMPANION,
                text = "Vanakkam, Farmer! I am your Farm Companion. Your 2-acre tomato field is currently in the Vegetative stage (Day 34). How can I assist your crop decisions today?"
            )
        )
    )
    val chatMessages: StateFlow<List<AiChatMessage>> = _chatMessages.asStateFlow()

    private val _isAiLoading = MutableStateFlow(false)
    val isAiLoading: StateFlow<Boolean> = _isAiLoading.asStateFlow()

    private val _isObservationUploaded = MutableStateFlow(true)
    val isObservationUploaded: StateFlow<Boolean> = _isObservationUploaded.asStateFlow()

    init {
        // Ensure default today's observation exists
        viewModelScope.launch {
            val obsList = dao.getAllObservations()
            // Check if today's observation already added
            launch {
                observations.collect { list ->
                    if (list.isNotEmpty() && list.none { it.isToday }) {
                        dao.insertObservation(
                            CropObservation(
                                dateLabel = "Day 34 • Today's Observation",
                                stage = "Vegetative Stage (Day 34)",
                                healthScore = 71,
                                summary = "Possible health change detected on lower leaves.",
                                detail = "Subtle yellowing on leaf margins and small chlorotic specks. Caused by persistent morning leaf wetness and 78% humidity.",
                                riskLevel = "Moderate",
                                isToday = true,
                                leafCondition = "Mild chlorosis on bottom 2 tiers, healthy upper vegetative growth.",
                                weatherCondition = "High Humidity (78%)"
                            )
                        )
                        dao.updateHealthScore(71)
                    }
                }
            }
        }
    }

    fun setActiveStage(stage: Int) {
        _activeStageIndex.value = stage
    }

    fun toggleTask(task: FarmTask) {
        viewModelScope.launch {
            dao.updateTask(task.copy(isCompleted = !task.isCompleted))
        }
    }

    fun simulateNewPhotoAnalysis() {
        viewModelScope.launch {
            _isObservationUploaded.value = true
            val newObs = CropObservation(
                dateLabel = "Day 34 • Today's Scan (Updated)",
                stage = "Vegetative Stage (Day 34)",
                healthScore = 71,
                summary = "Possible health change detected (Foliar moisture stress)",
                detail = "AI observation compared with Day 28: Lower leaf margins show early moisture stress & chlorotic spotting. Prompt preventative organic spraying advised.",
                riskLevel = "Moderate",
                isToday = true,
                leafCondition = "Lower tier chlorosis, upper shoots vigorous.",
                weatherCondition = "High Humidity (78%)"
            )
            dao.insertObservation(newObs)
            dao.updateHealthScore(71)
        }
    }

    fun sendAiPrompt(prompt: String) {
        val userMsg = AiChatMessage(sender = MessageSender.USER, text = prompt)
        _chatMessages.value = _chatMessages.value + userMsg

        viewModelScope.launch {
            _isAiLoading.value = true
            val profile = cropProfile.value
            val weatherData = weather.value
            val contextInfo = """
                Crop: ${profile?.cropName ?: "Tomato"} (${profile?.acres ?: 2.0} Acres in ${profile?.location ?: "Coimbatore"})
                Current Stage: ${profile?.currentStage ?: "02 GROW"}
                Health Score: ${profile?.healthScore ?: 71}% (Previous: ${profile?.previousHealthScore ?: 86}%)
                Weather: ${weatherData.temperatureC}°C, Humidity ${weatherData.humidityPercent}%, ${weatherData.forecastText}
                Soil: ${profile?.soilType ?: "Red Sandy Loam"}, Irrigation: ${profile?.irrigationType ?: "Drip"}
            """.trimIndent()

            val replyText = GeminiService.askCompanion(prompt, contextInfo)
            _chatMessages.value = _chatMessages.value + AiChatMessage(
                sender = MessageSender.COMPANION,
                text = replyText
            )
            _isAiLoading.value = false
        }
    }
}
