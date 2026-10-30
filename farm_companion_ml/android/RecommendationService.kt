package com.farmcompanion.recommendation

import com.farmcompanion.ml.ScreeningResult
import com.farmcompanion.ml.ScreeningStatus

/**
 * Data model for decoupled agronomic guidance.
 *
 * Distinct and isolated from the computer vision model's output to ensure
 * recommendations never alter, override, or fabricate diagnostic predictions.
 */
data class AgronomicRecommendation(
    val categoryTitle: String,
    val summary: String,
    val culturalPractices: List<String>,
    val generalEducation: String,
    val requiresExtensionConsultation: Boolean,
    val disclaimer: String
)

/**
 * Decoupled Agricultural Recommendation Service.
 *
 * Responsibilities:
 * - Provide educational guidance appropriate to the confirmed screening category.
 * - Clearly distinguish general educational guidance from crop-specific treatment.
 * - Avoid unsupported pesticide, fertilizer, or unverified chemical claims.
 * - Request additional information when crop species, stage, or severity is unknown.
 * - Never override the classifier's output or convert an uncertain result into a diagnosis.
 */
object RecommendationService {

    private const val STANDARD_DISCLAIMER =
        "Preliminary screening notice: Farm Companion provides educational screening to support farmer " +
        "awareness and decision-making. It does not replace laboratory diagnostic assays, plant pathology " +
        "inspections, or local agricultural extension advice."

    /**
     * Generates decoupled agricultural guidance based strictly on the raw screening output.
     */
    fun getRecommendation(
        screeningResult: ScreeningResult,
        cropName: String? = null
    ): AgronomicRecommendation {
        return when (screeningResult.status) {
            ScreeningStatus.UNCERTAIN_SCREENING -> {
                AgronomicRecommendation(
                    categoryTitle = "Uncertain Screening — Manual Inspection Advised",
                    summary = "The computer vision model could not confidently identify the health condition from this photograph.",
                    culturalPractices = listOf(
                        "Photograph a single affected leaf against a neutral background.",
                        "Ensure bright, indirect natural sunlight without heavy shadows or lens glare.",
                        "Inspect both upper and lower leaf surfaces, petioles, and stem junctions manually."
                    ),
                    generalEducation = "Low confidence scores often indicate unsupported crops, co-occurring physiological stresses, early-stage disease manifestation, or photographic blur. Do not apply chemical treatments based on uncertain results.",
                    requiresExtensionConsultation = true,
                    disclaimer = STANDARD_DISCLAIMER
                )
            }

            ScreeningStatus.CONFIRMED_PRELIMINARY -> {
                val cropContext = if (!cropName.isNullOrBlank()) " on $cropName" else ""
                when (screeningResult.topClass) {
                    "Healthy" -> AgronomicRecommendation(
                        categoryTitle = "Normal / Healthy Foliage$cropContext",
                        summary = "The leaf exhibits no visible signs of powdery mildew or rust pustules.",
                        culturalPractices = listOf(
                            "Maintain regular weekly field scouting and crop rotation records.",
                            "Ensure adequate plant spacing to facilitate airflow through the canopy.",
                            "Avoid prolonged overhead sprinkler irrigation during high-humidity evenings."
                        ),
                        generalEducation = "Healthy leaves remain the first line of photosynthetic productivity. Continue preventive cultural practices and monitor lower canopy foliage where humidity lingers.",
                        requiresExtensionConsultation = false,
                        disclaimer = STANDARD_DISCLAIMER
                    )

                    "Powdery" -> AgronomicRecommendation(
                        categoryTitle = "Powdery Mildew Screening$cropContext",
                        summary = "Visible symptoms are consistent with powdery mildew (white/gray talcum-like fungal growth).",
                        culturalPractices = listOf(
                            "Prune dense foliage to improve sunlight penetration and air movement.",
                            "Avoid excessive nitrogen fertilization, which produces dense, susceptible tender growth.",
                            "Clean and sanitize pruning shears between plants to prevent spore transmission."
                        ),
                        generalEducation = "Powdery mildew fungi (Erysiphales) thrive in warm, dry climates with high relative humidity at night. For verified infestations, consult your regional agricultural extension agent for registered bio-fungicides or crop-specific organic sulfur controls.",
                        requiresExtensionConsultation = true,
                        disclaimer = STANDARD_DISCLAIMER
                    )

                    "Rust" -> AgronomicRecommendation(
                        categoryTitle = "Plant Rust Screening$cropContext",
                        summary = "Visible symptoms are consistent with plant rust (characteristic reddish, orange, or brown spore pustules).",
                        culturalPractices = listOf(
                            "Remove and safely destroy heavily infected lower leaves before spores disperse in the wind.",
                            "Switch to drip or ground-level irrigation to keep leaf surfaces dry.",
                            "Select rust-resistant crop varieties in future planting cycles."
                        ),
                        generalEducation = "Rust fungi (Pucciniales) are obligate parasites that penetrate leaf tissue and deplete nutrients. If rust is widespread in your field, contact your local extension service immediately for registered targeted treatments permitted in your region.",
                        requiresExtensionConsultation = true,
                        disclaimer = STANDARD_DISCLAIMER
                    )

                    else -> AgronomicRecommendation(
                        categoryTitle = "Unsupported Condition",
                        summary = "Unrecognized category output.",
                        culturalPractices = listOf("Inspect the field with a qualified agronomist."),
                        generalEducation = "The classifier only supports Healthy, Powdery Mildew, and Rust conditions.",
                        requiresExtensionConsultation = true,
                        disclaimer = STANDARD_DISCLAIMER
                    )
                }
            }
        }
    }
}
