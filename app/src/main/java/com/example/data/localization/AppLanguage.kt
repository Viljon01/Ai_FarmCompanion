package com.example.data.localization

enum class AppLanguage(
    val code: String,
    val nativeName: String,
    val englishName: String,
    val speechLocale: String
) {
    EN("en", "English", "English", "en-IN"),
    TA("ta", "தமிழ்", "Tamil", "ta-IN"),
    HI("hi", "हिन्दी", "Hindi", "hi-IN"),
    TE("te", "తెలుగు", "Telugu", "te-IN"),
    KN("kn", "ಕನ್ನಡ", "Kannada", "kn-IN"),
    ML("ml", "മലയാളം", "Malayalam", "ml-IN"),
    BN("bn", "বাংলা", "Bengali", "bn-IN"),
    MR("mr", "मराठी", "Marathi", "mr-IN");

    companion object {
        fun fromCode(code: String): AppLanguage {
            return values().firstOrNull { it.code.equals(code, ignoreCase = true) } ?: EN
        }
    }
}
