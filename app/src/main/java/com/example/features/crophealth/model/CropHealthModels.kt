package com.example.features.crophealth.model

import java.util.UUID

/**
 * Visual Health Status Categories with Icon and Plain-Language Description
 */
enum class VisualHealthStatus(
    val label: String,
    val iconEmoji: String,
    val description: String
) {
    HEALTHY(
        label = "Healthy",
        iconEmoji = "🌿",
        description = "Foliage and plant structure show normal, vigorous growth with no significant visible stress symptoms."
    ),
    WATCH(
        label = "Watch & Monitor",
        iconEmoji = "👀",
        description = "Minor leaf irregularities observed. No immediate threat, but close observation is recommended over the next 48 hours."
    ),
    MODERATE_RISK(
        label = "Possible Issue Detected",
        iconEmoji = "⚠️",
        description = "Visible symptoms may indicate an emerging crop-health issue. Timely preventive action is advised."
    ),
    HIGH_RISK(
        label = "High Alert Condition",
        iconEmoji = "🚨",
        description = "Pronounced visible distress or spreading lesions detected. Prompt management steps and expert consultation recommended."
    ),
    INCONCLUSIVE(
        label = "Inconclusive Scan",
        iconEmoji = "🔍",
        description = "Image quality or lighting does not permit clear diagnosis. Retake with natural light and closer focus on the affected leaf."
    )
}

/**
 * Possible condition identified with confidence and visual evidence reason
 */
data class PossibleCondition(
    val name: String,
    val confidence: String, // "High", "Moderate", "Low"
    val reason: String
)

/**
 * Next steps categorized into action phases
 */
data class ActionNextSteps(
    val doNow: List<String>,
    val monitor: List<String>,
    val check: List<String>,
    val getHelp: List<String>
)

/**
 * Full Crop Health Assessment Result
 */
data class CropHealthAssessment(
    val id: String = UUID.randomUUID().toString(),
    val crop: String = "Tomato",
    val variety: String = "Shivam / Pusa Ruby",
    val acreage: String = "2.0 Acres",
    val location: String = "Coimbatore, Tamil Nadu",
    val dayNumber: Int = 35,
    val dateLabel: String = "Day 35 • Live Crop Scan",
    val assessmentTitle: String = "Possible leaf health issue detected",
    val confidencePercent: Int = 78,
    val visualStatus: VisualHealthStatus = VisualHealthStatus.MODERATE_RISK,
    val visibleSymptoms: List<String> = emptyList(),
    val possibleConditions: List<PossibleCondition> = emptyList(),
    val contributingFactors: List<String> = emptyList(),
    val precautions: List<String> = emptyList(),
    val nextSteps: ActionNextSteps = ActionNextSteps(emptyList(), emptyList(), emptyList(), emptyList()),
    val chemicalSafetyNotice: String = "Use only products approved for this crop in your state. Follow the product label and local Krishi Vigyan Kendra / agricultural university guidance rather than unverified chemical dosages.",
    val expertVerificationRecommended: Boolean = true,
    val isDemoResult: Boolean = false,
    val capturedImageUris: List<String> = emptyList(),
    val previousHealthScore: Int = 86,
    val currentHealthScore: Int = 71,
    val farmerObservedNotes: String = ""
)

/**
 * Supported crop types for selection
 */
enum class CropSelection(val displayName: String, val emoji: String) {
    TOMATO("Tomato", "🍅"),
    RICE("Rice (Paddy)", "🌾"),
    MAIZE("Maize / Corn", "🌽"),
    COTTON("Cotton", "🌱"),
    CHILLI("Chilli / Pepper", "🌶️"),
    OTHER("Other Crop", "🌿")
}

/**
 * Farmer observed symptom checklist
 */
data class SymptomCheckbox(
    val id: String,
    val label: String,
    val isChecked: Boolean = false
)

/**
 * Image quality validation check result
 */
data class ImageQualityCheckResult(
    val isAcceptable: Boolean = true,
    val warningTitle: String? = null,
    val warningMessage: String? = null,
    val suggestions: List<String> = emptyList()
)

/**
 * Pre-configured Demo Scenarios for Offline & Testing
 */
object CropHealthDemoScenarios {
    val HEALTHY_TOMATO = CropHealthAssessment(
        id = "demo_healthy",
        crop = "Tomato",
        variety = "Shivam / Pusa Ruby",
        dayNumber = 35,
        dateLabel = "Day 35 • Healthy Benchmark",
        assessmentTitle = "Vigorous Vegetative Growth",
        confidencePercent = 94,
        visualStatus = VisualHealthStatus.HEALTHY,
        visibleSymptoms = listOf(
            "Deep green, uniform leaf lamina without chlorosis",
            "Strong turgid petiole structure and healthy branch axils",
            "No visible lesions, wilting, or pest webbing observed"
        ),
        possibleConditions = listOf(
            PossibleCondition(
                name = "Optimal Vegetative Foliage",
                confidence = "High",
                reason = "Uniform chlorophyll distribution, crisp serrated margins, clean vein networks."
            )
        ),
        contributingFactors = listOf(
            "Balanced drip irrigation schedule",
            "Appropriate soil moisture and ambient temperature (28°C)"
        ),
        precautions = listOf(
            "Maintain current drip fertigation cycle",
            "Continue periodic leaf-underside inspections"
        ),
        nextSteps = ActionNextSteps(
            doNow = listOf("Continue scheduled morning irrigation.", "Ensure bamboo staking supports new branches."),
            monitor = listOf("Record next visual observation at Day 42 (flowering onset)."),
            check = listOf("Check soil moisture 2 inches below surface."),
            getHelp = listOf("Reach out to local extension if sudden leaf curling occurs.")
        ),
        isDemoResult = true,
        previousHealthScore = 86,
        currentHealthScore = 92
    )

    val POSSIBLE_EARLY_BLIGHT = CropHealthAssessment(
        id = "demo_early_blight",
        crop = "Tomato",
        variety = "Shivam / Pusa Ruby",
        dayNumber = 35,
        dateLabel = "Day 35 • Live Scan Assessment",
        assessmentTitle = "Possible Foliar Humidity Stress & Early Blight Precursor",
        confidencePercent = 78,
        visualStatus = VisualHealthStatus.MODERATE_RISK,
        visibleSymptoms = listOf(
            "Yellowing (chlorosis) along lowest leaf margins",
            "Small pinpoint concentric dark brown spots on older lower leaves",
            "Mild leaf curling towards leaf tips under high foliar moisture"
        ),
        possibleConditions = listOf(
            PossibleCondition(
                name = "Possible Early Blight Precursor (Alternaria-like symptoms)",
                confidence = "Moderate (74%)",
                reason = "Pinhead brown spots with subtle concentric rings on lower leaves touching damp soil."
            ),
            PossibleCondition(
                name = "Moisture Stress / Soil Splash Chlorosis",
                confidence = "Low (42%)",
                reason = "Rain splash and high ambient humidity (78%) restricting oxygen diffusion at root level."
            )
        ),
        contributingFactors = listOf(
            "78% relative humidity with overcast sky",
            "Dense canopy shading lower leaves near soil",
            "Upcoming rain forecasted in 36 hours"
        ),
        precautions = listOf(
            "Prune bottom 2 leaves touching the soil surface to improve airflow",
            "Suspend evening overhead watering; irrigate only through drip lines",
            "Inspect neighboring tomato beds for similar spotting",
            "Avoid spraying synthetic chemicals in high wind or prior to rain"
        ),
        nextSteps = ActionNextSteps(
            doNow = listOf(
                "Prune yellowing lowest foliage with clean shears and discard away from field.",
                "Ensure drainage furrows are clear before forecasted rain."
            ),
            monitor = listOf(
                "Re-scan affected plants in 48 hours to check if brown spots expand.",
                "Observe morning dew persistence on canopy."
            ),
            check = listOf(
                "Inspect leaf undersides for fungal spores or dark mycelial felt.",
                "Check neighboring rows (5 meters in each direction)."
            ),
            getHelp = listOf(
                "If concentric spots enlarge past 5mm with yellow halos, consult local KVK agronomist."
            )
        ),
        isDemoResult = true,
        previousHealthScore = 86,
        currentHealthScore = 71
    )

    val PEST_DAMAGE = CropHealthAssessment(
        id = "demo_pest_damage",
        crop = "Tomato",
        variety = "Shivam / Pusa Ruby",
        dayNumber = 35,
        dateLabel = "Day 35 • Pest Screening",
        assessmentTitle = "Possible Leaf Miner or Sap Feeder Damage",
        confidencePercent = 82,
        visualStatus = VisualHealthStatus.MODERATE_RISK,
        visibleSymptoms = listOf(
            "White serpentine winding trails on upper leaf surface",
            "Punctured leaf cells along vein margins",
            "Slight leaf puckering near new shoot tips"
        ),
        possibleConditions = listOf(
            PossibleCondition(
                name = "Leaf Miner (Liriomyza spp.)",
                confidence = "High (82%)",
                reason = "Distinctive white serpentine tunneling in leaf mesophyll tissue."
            ),
            PossibleCondition(
                name = "Early Whitefly Activity",
                confidence = "Low (38%)",
                reason = "Mild honeydew sheen on secondary branch leaflets."
            )
        ),
        contributingFactors = listOf(
            "Warm afternoon temperatures (28°C–31°C)",
            "Tender succulent vegetative foliage attractive to egg-laying adults"
        ),
        precautions = listOf(
            "Install yellow sticky traps (10 traps per acre) at canopy height",
            "Spray 5% Neem Seed Kernel Extract (NSKE) early in the morning",
            "Avoid broad-spectrum synthetic pyrethroids which eliminate natural parasitoid wasps"
        ),
        nextSteps = ActionNextSteps(
            doNow = listOf("Crush active larvae inside mined leaves between fingers.", "Place yellow sticky traps."),
            monitor = listOf("Count trapped adults on cards every 2 days."),
            check = listOf("Check underside of top leaves for whitefly eggs."),
            getHelp = listOf("Consult your regional block agricultural office if >30% foliage is mined.")
        ),
        isDemoResult = true,
        previousHealthScore = 86,
        currentHealthScore = 68
    )

    val NUTRIENT_STRESS = CropHealthAssessment(
        id = "demo_nutrient",
        crop = "Tomato",
        variety = "Shivam / Pusa Ruby",
        dayNumber = 35,
        dateLabel = "Day 35 • Nutrition Check",
        assessmentTitle = "Possible Nitrogen or Magnesium Deficiency Chlorosis",
        confidencePercent = 76,
        visualStatus = VisualHealthStatus.WATCH,
        visibleSymptoms = listOf(
            "General pale light-green to yellow cast across older leaves",
            "Veins remain slightly greener than interveinal leaf tissue",
            "No necrotic dark spots or rotting tissue observed"
        ),
        possibleConditions = listOf(
            PossibleCondition(
                name = "Nitrogen (N) Deficiency",
                confidence = "Moderate (72%)",
                reason = "Uniform lightening of older bottom leaves moving upward as mobile N relocates to new shoot tips."
            ),
            PossibleCondition(
                name = "Magnesium (Mg) Deficiency",
                confidence = "Moderate (64%)",
                reason = "Interveinal chlorosis pattern in sandy loam soil."
            )
        ),
        contributingFactors = listOf(
            "Red Sandy Loam soil has high leaching rate after irrigation",
            "Rapid canopy expansion consuming available nitrogen"
        ),
        precautions = listOf(
            "Apply well-decomposed vermicompost or Panchagavya (3% foliar spray)",
            "Check soil pH (target 6.5–6.8 for optimal nutrient uptake)",
            "Do not over-irrigate to avoid leaching nitrogen past root zone"
        ),
        nextSteps = ActionNextSteps(
            doNow = listOf("Top dress with compost or apply light organic nitrogen fertigation."),
            monitor = listOf("Watch if new leaves regain rich dark green hue within 5 days."),
            check = listOf("Check root depth and soil drainage rate."),
            getHelp = listOf("Send leaf tissue sample for laboratory testing if yellowing persists.")
        ),
        isDemoResult = true,
        previousHealthScore = 86,
        currentHealthScore = 74
    )

    val INCONCLUSIVE_SCAN = CropHealthAssessment(
        id = "demo_inconclusive",
        crop = "Tomato",
        variety = "Shivam / Pusa Ruby",
        dayNumber = 35,
        dateLabel = "Day 35 • Scan Quality Review",
        assessmentTitle = "Inconclusive Scan — Insufficient Visual Clarity",
        confidencePercent = 35,
        visualStatus = VisualHealthStatus.INCONCLUSIVE,
        visibleSymptoms = listOf(
            "Image is backlit or blurred; leaf texture is out of sharp focus",
            "Cannot differentiate fungal lesions from natural leaf soil dust"
        ),
        possibleConditions = listOf(
            PossibleCondition(
                name = "Unverifiable Symptom Pattern",
                confidence = "Low (35%)",
                reason = "Optical blur and harsh shadow obstruct micro-symptom analysis."
            )
        ),
        contributingFactors = listOf(
            "Shaky camera capture or low ambient lighting"
        ),
        precautions = listOf(
            "Hold phone steady with both hands or rest elbow against a staking pole",
            "Face the sun with the leaf in front of you so sunlight falls on the leaf, not into the lens",
            "Capture both the affected leaf and a healthy neighbor leaf for contrast"
        ),
        nextSteps = ActionNextSteps(
            doNow = listOf("Retake photo in bright natural morning or late afternoon light."),
            monitor = listOf("Re-run live scan once clear image is captured."),
            check = listOf("Wipe camera lens clean of field dust."),
            getHelp = listOf("Take a physical leaf sample in a clean paper bag to nearest agri clinic if urgent.")
        ),
        isDemoResult = true,
        previousHealthScore = 86,
        currentHealthScore = 86
    )

    val ALL_DEMOS = listOf(
        POSSIBLE_EARLY_BLIGHT,
        HEALTHY_TOMATO,
        PEST_DAMAGE,
        NUTRIENT_STRESS,
        INCONCLUSIVE_SCAN
    )
}
