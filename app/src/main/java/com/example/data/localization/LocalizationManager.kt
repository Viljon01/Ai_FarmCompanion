package com.example.data.localization

import android.content.Context
import androidx.compose.runtime.*

val LocalAppLanguage = compositionLocalOf { AppLanguage.EN }

@Composable
fun t(key: String): String {
    val lang = LocalAppLanguage.current
    return Translations.get(lang, key)
}

@Composable
fun st(text: String): String {
    val lang = LocalAppLanguage.current
    return Translations.smartTranslate(text, lang)
}

object LocalizationManager {
    private const val PREFS_NAME = "agri_localization_prefs"
    private const val KEY_LANG = "selected_language_code"

    fun getSavedLanguage(context: Context): AppLanguage {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val code = prefs.getString(KEY_LANG, AppLanguage.EN.code) ?: AppLanguage.EN.code
        return AppLanguage.fromCode(code)
    }

    fun saveLanguage(context: Context, language: AppLanguage) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_LANG, language.code).apply()
    }

    fun translate(lang: AppLanguage, key: String): String {
        return Translations.get(lang, key)
    }

    fun translateCrop(cropName: String, lang: AppLanguage): String {
        val key = when (cropName.lowercase()) {
            "tomato" -> "crop.tomato"
            "rice", "paddy" -> "crop.rice"
            "maize", "corn" -> "crop.maize"
            "cotton" -> "crop.cotton"
            "chilli", "pepper" -> "crop.chilli"
            else -> "crop.other"
        }
        return Translations.get(lang, key)
    }

    fun translateStage(stageIndex: Int, lang: AppLanguage): String {
        val key = when (stageIndex) {
            1 -> "journey.plan"
            2 -> "journey.grow"
            3 -> "journey.monitor"
            4 -> "journey.protect"
            5 -> "journey.harvest"
            6 -> "journey.sell"
            else -> "journey.monitor"
        }
        return Translations.get(lang, key)
    }
}
