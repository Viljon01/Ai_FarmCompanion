package com.example

import android.app.Application
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.features.crophealth.model.CropHealthDemoScenarios
import com.example.features.crophealth.model.VisualHealthStatus
import com.example.features.crophealth.service.CropHealthService
import com.example.viewmodel.CropContextEngine
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("AI Farm Companion", appName)
    }

    @Test
    fun `crop context engine initializes with tomato cycle and telemetry`() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val engine = CropContextEngine(app)
        val context = engine.cropContext.value

        assertNotNull(context)
        assertEquals("Tomato", context.cropCycle.cropName)
        assertEquals(2.0, context.farm.landSizeAcres, 0.01)
        assertEquals("Coimbatore, Tamil Nadu", context.farm.location)
        assertEquals(78, context.currentWeather.humidityPercent)
    }

    @Test
    fun `crop context engine stage selection updates context`() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val engine = CropContextEngine(app)

        engine.selectCropStage(5)
        val context = engine.cropContext.value

        assertEquals(5, context.cropCycle.currentStageIndex)
        assertEquals("05 HARVEST", context.cropCycle.currentStageName)
    }

    @Test
    fun `agricultural planning weather contains condition telemetry and advisory`() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val engine = CropContextEngine(app)
        val weather = engine.cropContext.value.currentWeather

        assertEquals(28, weather.temperatureC)
        assertEquals(78, weather.humidityPercent)
        assertEquals(65, weather.rainfallChance)
        assertTrue(weather.recommendation.isNotEmpty())
        assertTrue(weather.cropImpactNotice.isNotEmpty())
    }

    @Test
    fun `crop health demo scenarios contain complete structured guidance`() {
        val earlyBlight = CropHealthDemoScenarios.POSSIBLE_EARLY_BLIGHT
        assertEquals(VisualHealthStatus.MODERATE_RISK, earlyBlight.visualStatus)
        assertEquals(78, earlyBlight.confidencePercent)
        assertTrue(earlyBlight.visibleSymptoms.isNotEmpty())
        assertTrue(earlyBlight.possibleConditions.isNotEmpty())
        assertTrue(earlyBlight.precautions.isNotEmpty())
        assertTrue(earlyBlight.nextSteps.doNow.isNotEmpty())
        assertTrue(earlyBlight.nextSteps.monitor.isNotEmpty())
        assertTrue(earlyBlight.nextSteps.check.isNotEmpty())
        assertTrue(earlyBlight.nextSteps.getHelp.isNotEmpty())
        assertTrue(earlyBlight.chemicalSafetyNotice.isNotEmpty())
    }

    @Test
    fun `crop health service validates null bitmap gracefully`() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val engine = CropContextEngine(app)
        val service = CropHealthService(app, engine)

        val check = service.validateImage(null)
        assertFalse(check.isAcceptable)
        assertNotNull(check.warningTitle)
        assertTrue(check.suggestions.isNotEmpty())
    }

    @Test
    fun `crop health service analyzes symptoms and generates categorized next steps`() = runBlocking {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val engine = CropContextEngine(app)
        val service = CropHealthService(app, engine)

        val assessment = service.analyzeCrop(
            crop = "Tomato",
            variety = "Shivam",
            selectedSymptoms = listOf("Brown spots", "Yellow leaves"),
            farmerNotes = "Lower canopy moisture wetness",
            imageUris = emptyList()
        )

        assertEquals("Tomato", assessment.crop)
        assertEquals(VisualHealthStatus.MODERATE_RISK, assessment.visualStatus)
        assertTrue(assessment.visibleSymptoms.isNotEmpty())
        assertTrue(assessment.precautions.isNotEmpty())
        assertTrue(assessment.nextSteps.doNow.isNotEmpty())
        assertEquals(71, assessment.currentHealthScore)
    }

    @Test
    fun `multilingual localization contains non-empty translations for all 6 required languages`() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val engine = CropContextEngine(app)

        val requiredLanguages = listOf(
            com.example.data.localization.AppLanguage.EN,
            com.example.data.localization.AppLanguage.TA,
            com.example.data.localization.AppLanguage.TE,
            com.example.data.localization.AppLanguage.HI,
            com.example.data.localization.AppLanguage.ML,
            com.example.data.localization.AppLanguage.KN
        )

        val sampleKeys = listOf(
            "common.appName",
            "common.tagline",
            "common.home",
            "common.journey",
            "common.monitor",
            "common.alerts",
            "common.assistant",
            "dashboard.greeting",
            "journey.title",
            "health.title",
            "weather.title",
            "risk.title",
            "yield.title",
            "market.title",
            "assistant.title",
            "settings.title",
            "profile.title",
            "crop.tomato"
        )

        for (lang in requiredLanguages) {
            engine.setLanguage(lang)
            assertEquals(lang, engine.currentLanguage.value)

            for (key in sampleKeys) {
                val translated = com.example.data.localization.Translations.get(lang, key)
                assertNotNull("Key '$key' should be translated for language ${lang.code}", translated)
                assertTrue("Translation for key '$key' in ${lang.code} should not be blank", translated.isNotBlank())
            }
        }
    }
}
