package com.example.features.crophealth.ui

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.data.CropContext
import com.example.data.localization.st
import com.example.data.localization.t
import com.example.features.crophealth.model.*
import com.example.features.crophealth.service.CropHealthService
import com.example.ui.components.AppDestination
import com.example.ui.components.LeafAiSparkIcon
import com.example.ui.components.SubtleFarmBackground
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class ScreenState {
    MAIN_MENU,
    LIVE_CAMERA,
    PHOTO_REVIEW,
    ANALYZING,
    RESULT_PAGE,
    HISTORY_VIEW
}

@Composable
fun CropHealthMonitorScreen(
    cropContext: CropContext,
    service: CropHealthService,
    onNavigate: (AppDestination) -> Unit,
    onOpenCompanionWithAssessment: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var screenState by remember { mutableStateOf(ScreenState.MAIN_MENU) }
    var capturedBitmaps by remember { mutableStateOf<List<Bitmap>>(emptyList()) }
    var selectedImageUris by remember { mutableStateOf<List<Uri>>(emptyList()) }

    var selectedCrop by remember { mutableStateOf(CropSelection.TOMATO) }
    var selectedSymptoms by remember {
        mutableStateOf(
            listOf(
                SymptomCheckbox("yellowing", "Yellow leaves", false),
                SymptomCheckbox("spots", "Brown spots", false),
                SymptomCheckbox("wilting", "Wilting", false),
                SymptomCheckbox("holes", "Holes in leaves", false),
                SymptomCheckbox("powder", "White powder", false),
                SymptomCheckbox("curling", "Curling leaves", false),
                SymptomCheckbox("growth", "Slow growth", false),
                SymptomCheckbox("unknown", "Unknown / Not sure", false)
            )
        )
    }
    var farmerNotes by remember { mutableStateOf("") }
    var qualityCheckResult by remember { mutableStateOf(ImageQualityCheckResult(isAcceptable = true)) }

    // Analysis State
    var currentAssessment by remember { mutableStateOf<CropHealthAssessment?>(null) }
    var isSavingObservation by remember { mutableStateOf(false) }
    var observationSavedSuccess by remember { mutableStateOf(false) }
    var analysisProgressStep by remember { mutableStateOf(1) }

    // Photo picker launcher (up to 3 images)
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems = 3)
    ) { uris ->
        if (uris.isNotEmpty()) {
            selectedImageUris = uris
            // Load first bitmap for quality check
            coroutineScope.launch {
                try {
                    val input = context.contentResolver.openInputStream(uris.first())
                    val bmp = BitmapFactory.decodeStream(input)
                    input?.close()
                    if (bmp != null) {
                        capturedBitmaps = listOf(bmp)
                        qualityCheckResult = service.validateImage(bmp)
                    }
                    screenState = ScreenState.PHOTO_REVIEW
                } catch (e: Exception) {
                    screenState = ScreenState.PHOTO_REVIEW
                }
            }
        }
    }

    // Function to run analysis
    fun triggerAnalysis(demoScenario: CropHealthAssessment? = null) {
        screenState = ScreenState.ANALYZING
        analysisProgressStep = 1
        coroutineScope.launch {
            delay(500)
            analysisProgressStep = 2
            delay(700)
            analysisProgressStep = 3
            delay(700)
            analysisProgressStep = 4

            val result = service.analyzeCrop(
                crop = selectedCrop.displayName,
                variety = cropContext.cropCycle.variety,
                selectedSymptoms = selectedSymptoms.filter { it.isChecked }.map { it.label },
                farmerNotes = farmerNotes,
                imageUris = selectedImageUris.map { it.toString() },
                bitmap = capturedBitmaps.firstOrNull(),
                demoScenario = demoScenario
            )

            delay(400)
            currentAssessment = result
            observationSavedSuccess = false
            screenState = ScreenState.RESULT_PAGE
        }
    }

    when (screenState) {
        ScreenState.LIVE_CAMERA -> {
            LiveCameraView(
                onPhotoCaptured = { bitmap ->
                    capturedBitmaps = listOf(bitmap)
                    qualityCheckResult = service.validateImage(bitmap)
                    screenState = ScreenState.PHOTO_REVIEW
                },
                onUploadFallback = {
                    photoPickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                },
                onCancel = {
                    screenState = ScreenState.MAIN_MENU
                },
                modifier = modifier
            )
        }

        ScreenState.PHOTO_REVIEW -> {
            PhotoReviewScreen(
                capturedBitmaps = capturedBitmaps,
                selectedUris = selectedImageUris,
                qualityCheck = qualityCheckResult,
                selectedCrop = selectedCrop,
                onSelectCrop = { selectedCrop = it },
                symptoms = selectedSymptoms,
                onToggleSymptom = { id ->
                    selectedSymptoms = selectedSymptoms.map {
                        if (it.id == id) it.copy(isChecked = !it.isChecked) else it
                    }
                },
                farmerNotes = farmerNotes,
                onNotesChange = { farmerNotes = it },
                onRetake = {
                    capturedBitmaps = emptyList()
                    selectedImageUris = emptyList()
                    screenState = ScreenState.MAIN_MENU
                },
                onAnalyze = { triggerAnalysis() },
                modifier = modifier
            )
        }

        ScreenState.ANALYZING -> {
            AnalysisLoadingScreen(
                progressStep = analysisProgressStep,
                selectedCrop = selectedCrop.displayName,
                dayNumber = cropContext.cropCycle.cropAgeDays,
                modifier = modifier
            )
        }

        ScreenState.RESULT_PAGE -> {
            currentAssessment?.let { assessment ->
                AnalysisResultScreen(
                    assessment = assessment,
                    cropContext = cropContext,
                    isSaved = observationSavedSuccess,
                    isSaving = isSavingObservation,
                    onSaveToJourney = {
                        isSavingObservation = true
                        coroutineScope.launch {
                            val success = service.saveObservationToJourney(assessment)
                            isSavingObservation = false
                            observationSavedSuccess = success
                        }
                    },
                    onAskAssistant = {
                        val summaryText = "Regarding my ${assessment.crop} (Day ${assessment.dayNumber}) health assessment: ${assessment.assessmentTitle} with symptoms ${assessment.visibleSymptoms.joinToString(", ")}. What should I do next?"
                        onOpenCompanionWithAssessment(summaryText)
                    },
                    onAnalyzeAnother = {
                        capturedBitmaps = emptyList()
                        selectedImageUris = emptyList()
                        screenState = ScreenState.MAIN_MENU
                    },
                    onViewHistory = {
                        screenState = ScreenState.HISTORY_VIEW
                    },
                    onBack = {
                        screenState = ScreenState.MAIN_MENU
                    },
                    modifier = modifier
                )
            }
        }

        ScreenState.HISTORY_VIEW -> {
            ObservationHistoryScreen(
                cropContext = cropContext,
                onSelectObservation = { obs ->
                    // View historical observation
                },
                onBack = {
                    screenState = ScreenState.MAIN_MENU
                },
                modifier = modifier
            )
        }

        ScreenState.MAIN_MENU -> {
            MainMenuScreen(
                cropContext = cropContext,
                onOpenCamera = { screenState = ScreenState.LIVE_CAMERA },
                onUploadPhoto = {
                    photoPickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                },
                onViewHistory = { screenState = ScreenState.HISTORY_VIEW },
                onSelectDemoScenario = { scenario ->
                    selectedCrop = CropSelection.TOMATO
                    triggerAnalysis(scenario)
                },
                onBack = { onNavigate(AppDestination.DASHBOARD) },
                modifier = modifier
            )
        }
    }
}

/**
 * 1. MAIN MENU SCREEN
 */
@Composable
fun MainMenuScreen(
    cropContext: CropContext,
    onOpenCamera: () -> Unit,
    onUploadPhoto: () -> Unit,
    onViewHistory: () -> Unit,
    onSelectDemoScenario: (CropHealthAssessment) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 18.dp, vertical = 12.dp)
            .testTag("crop_health_main_screen")
    ) {
        // Page Title & Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Crop Health Monitor",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = AgriTextPrimary
                )
                Text(
                    text = "Check your crop using a live camera or photo.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = AgriForestGreen,
                    fontWeight = FontWeight.Medium
                )
            }

            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = AgriTextSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Crop Context Pill Strip
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = AgriCardSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, AgriLineSubtle),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(AgriSageLight),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🍅", fontSize = 22.sp)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "${cropContext.cropCycle.cropName} • ${cropContext.cropCycle.variety.substringBefore(" ")}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = AgriTextPrimary
                        )
                        Text(
                            text = "${cropContext.farm.landSizeAcres} Acres • ${cropContext.farm.location.substringBefore(",")}",
                            style = MaterialTheme.typography.bodySmall,
                            color = AgriTextSecondary
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = AgriSageLight,
                    border = androidx.compose.foundation.BorderStroke(1.dp, AgriForestGreen.copy(alpha = 0.4f))
                ) {
                    Text(
                        text = "Day ${cropContext.cropCycle.cropAgeDays}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = AgriForestGreen,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Hero Diagnostic Card with Hand-drawn Illustration
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = AgriCardSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, AgriLineSubtle),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Image(
                    painter = painterResource(id = R.drawable.img_during_cultivation),
                    contentDescription = "Farmer inspecting crop health",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .clip(RoundedCornerShape(14.dp)),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "AI-Assisted Foliar Assessment",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = AgriTextPrimary
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Scan leaves, stems, or whole plants to identify visible discoloration, humidity-stress precursors, or pest signatures. Contextualized with regional weather and your soil profile.",
                    style = MaterialTheme.typography.bodySmall,
                    color = AgriTextSecondary,
                    lineHeight = 19.sp
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Primary Action 1: Open Camera
                Button(
                    onClick = onOpenCamera,
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AgriForestGreen),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("open_camera_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Open Camera",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Primary Action 2: Upload Photo
                OutlinedButton(
                    onClick = onUploadPhoto,
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.2.dp, AgriForestGreen),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("upload_photo_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.PhotoLibrary,
                        contentDescription = null,
                        tint = AgriForestGreen,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = st("Upload Photo (Up to 3)"),
                        fontSize = 15.sp,
                        color = AgriForestGreen,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Secondary Action: View Previous Observations
        OutlinedCard(
            onClick = onViewHistory,
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, AgriLineSubtle),
            modifier = Modifier.fillMaxWidth().testTag("view_previous_observations_card")
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(AgriSageLight),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = null,
                            tint = AgriForestGreen,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = st("View Previous Observations"),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = AgriTextPrimary
                        )
                        Text(
                            text = st("Compare Day 28 (86%) vs Day 35 shift"),
                            style = MaterialTheme.typography.bodySmall,
                            color = AgriTextSecondary
                        )
                    }
                }

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = AgriTextSecondary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // ML Model Classification Capabilities
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "🌱 ML Model Classification Scope",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = AgriForestGreen
            )
            Text(
                text = "543 KB • 5ms CPU",
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                color = AgriEarthBrown
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            val supportedClasses = listOf(
                Triple("Healthy Leaf", "🌿", "90%+ Accuracy"),
                Triple("Powdery Mildew", "🌫️", "Fungal Spore Screening"),
                Triple("Plant Rust", "🔴", "Rust Pustule Detection")
            )
            supportedClasses.forEach { (title, emoji, badge) ->
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = AgriCardSurface,
                    border = BorderStroke(1.dp, AgriLineSubtle),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(emoji, fontSize = 14.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = title,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = AgriTextPrimary
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = badge,
                            fontSize = 9.sp,
                            color = AgriTextSecondary
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Photography Guidance Card
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = AgriSurfaceSubtle,
            border = androidx.compose.foundation.BorderStroke(1.dp, AgriLineSubtle),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "📸 Photo Capture Guidance",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = AgriForestGreen
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "1. Use natural daylight\n2. Keep camera steady and focused on affected leaves\n3. Capture both healthy and affected sections\n4. Avoid strong shadows or backlit glare",
                    fontSize = 11.sp,
                    color = AgriTextSecondary,
                    lineHeight = 17.sp
                )
            }
        }
    }
}

/**
 * 2. PHOTO REVIEW & DETAILS INPUT SCREEN
 */
@Composable
fun PhotoReviewScreen(
    capturedBitmaps: List<Bitmap>,
    selectedUris: List<Uri>,
    qualityCheck: ImageQualityCheckResult,
    selectedCrop: CropSelection,
    onSelectCrop: (CropSelection) -> Unit,
    symptoms: List<SymptomCheckbox>,
    onToggleSymptom: (String) -> Unit,
    farmerNotes: String,
    onNotesChange: (String) -> Unit,
    onRetake: () -> Unit,
    onAnalyze: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 18.dp, vertical = 12.dp)
            .testTag("photo_review_screen")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = st("Review Photo & Details"),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = AgriTextPrimary
            )
            TextButton(onClick = onRetake) {
                Text(st("Retake"), color = AgriTomatoRed, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Image Previews
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = AgriCardSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, AgriLineSubtle),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(10.dp)) {
                if (capturedBitmaps.isNotEmpty()) {
                    Image(
                        bitmap = capturedBitmaps.first().asImageBitmap(),
                        contentDescription = "Captured crop photo preview",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(210.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )
                } else if (selectedUris.isNotEmpty()) {
                    AsyncImage(
                        model = selectedUris.first(),
                        contentDescription = "Uploaded crop photo preview",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(210.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.img_during_cultivation),
                        contentDescription = "Standard preview",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(210.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )
                }

                if (selectedUris.size > 1) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "${selectedUris.size} ${st("images selected for comprehensive canopy analysis")}",
                        fontSize = 11.sp,
                        color = AgriForestGreen,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        // Quality check warning if applicable
        if (!qualityCheck.isAcceptable) {
            Spacer(modifier = Modifier.height(10.dp))
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = AgriWarningLight,
                border = androidx.compose.foundation.BorderStroke(1.dp, AgriWarningAmber.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = AgriWarningAmber,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = st(qualityCheck.warningTitle ?: "Image Quality Warning"),
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = AgriEarthBrown
                        )
                        Text(
                            text = st(qualityCheck.warningMessage ?: "Image quality may affect the assessment."),
                            fontSize = 11.sp,
                            color = AgriTextSecondary
                        )
                        qualityCheck.suggestions.forEach { sug ->
                            Text("• ${st(sug)}", fontSize = 10.sp, color = AgriTextSecondary)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Crop Selection
        Text(
            text = st("What crop are you checking?"),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = AgriTextPrimary
        )

        Spacer(modifier = Modifier.height(6.dp))

        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(CropSelection.values()) { crop ->
                val isSelected = crop == selectedCrop
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (isSelected) AgriForestGreen else AgriCardSurface,
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isSelected) AgriForestGreen else AgriLineSubtle
                    ),
                    modifier = Modifier.clickable { onSelectCrop(crop) }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(crop.emoji, fontSize = 14.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = st(crop.displayName),
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color.White else AgriTextPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Symptom checklist
        Text(
            text = st("What have you noticed? (Optional)"),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = AgriTextPrimary
        )

        Spacer(modifier = Modifier.height(6.dp))

        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            symptoms.chunked(2).forEach { rowPair ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    rowPair.forEach { item ->
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (item.isChecked) AgriSageLight else AgriCardSurface,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (item.isChecked) AgriForestGreen else AgriLineSubtle
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .heightIn(min = 44.dp)
                                .clickable { onToggleSymptom(item.id) }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = item.isChecked,
                                    onCheckedChange = { onToggleSymptom(item.id) },
                                    colors = CheckboxDefaults.colors(checkedColor = AgriForestGreen),
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = st(item.label),
                                    fontSize = 11.sp,
                                    fontWeight = if (item.isChecked) FontWeight.Bold else FontWeight.Normal,
                                    color = if (item.isChecked) AgriForestGreen else AgriTextPrimary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                    if (rowPair.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Additional notes
        OutlinedTextField(
            value = farmerNotes,
            onValueChange = onNotesChange,
            label = {
                Text(
                    text = st("Describe what you noticed (e.g. soil wetness, lower leaves)"),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = AgriForestGreen,
                unfocusedBorderColor = AgriLineSubtle
            ),
            maxLines = 3
        )

        Spacer(modifier = Modifier.height(18.dp))

        // Analyze Button
        Button(
            onClick = onAnalyze,
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AgriForestGreen),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("analyze_crop_button")
        ) {
            LeafAiSparkIcon(size = 20.dp, tintColor = Color.White)
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = st("Analyze Crop"),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/**
 * 3. ANALYSIS LOADING ANIMATION SCREEN
 */
@Composable
fun AnalysisLoadingScreen(
    progressStep: Int,
    selectedCrop: String,
    dayNumber: Int,
    modifier: Modifier = Modifier
) {
    val transition = rememberInfiniteTransition(label = "pulse_leaf")
    val pulseScale by transition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "leaf_pulse"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(AgriWarmBg)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        SubtleFarmBackground()

        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = AgriCardSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, AgriLineSubtle),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Animated Pulsing Leaf Icon
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(AgriSageLight)
                        .border(1.5.dp, AgriForestGreen.copy(alpha = 0.4f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    LeafAiSparkIcon(size = 46.dp)
                }

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = "Analyzing your crop…",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = AgriTextPrimary
                )

                Text(
                    text = "$selectedCrop • Day $dayNumber Vegetative Canopy",
                    style = MaterialTheme.typography.bodyMedium,
                    color = AgriForestGreen,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Progressive real UI step indicators
                val steps = listOf(
                    "Image received & validated",
                    "Crop identified ($selectedCrop)",
                    "Checking visible symptoms",
                    "Correlating weather & crop context",
                    "Preparing recommendations"
                )

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    steps.forEachIndexed { idx, stepText ->
                        val stepNum = idx + 1
                        val isComplete = progressStep > stepNum
                        val isCurrent = progressStep == stepNum

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            if (isComplete) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = AgriForestGreen,
                                    modifier = Modifier.size(18.dp)
                                )
                            } else if (isCurrent) {
                                CircularProgressIndicator(
                                    strokeWidth = 2.dp,
                                    color = AgriForestGreen,
                                    modifier = Modifier.size(16.dp)
                                )
                            } else {
                                Box(
                                    modifier = Modifier
                                        .size(16.dp)
                                        .border(1.5.dp, AgriTextTertiary, CircleShape)
                                )
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            Text(
                                text = stepText,
                                fontSize = 12.5.sp,
                                fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                                color = when {
                                    isComplete -> AgriForestGreen
                                    isCurrent -> AgriTextPrimary
                                    else -> AgriTextTertiary
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * 4. FULL ANALYSIS RESULT SCREEN
 */
@Composable
fun AnalysisResultScreen(
    assessment: CropHealthAssessment,
    cropContext: CropContext,
    isSaved: Boolean,
    isSaving: Boolean,
    onSaveToJourney: () -> Unit,
    onAskAssistant: () -> Unit,
    onAnalyzeAnother: () -> Unit,
    onViewHistory: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 18.dp, vertical = 12.dp)
            .testTag("crop_health_result_screen")
    ) {
        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = AgriTextPrimary
                    )
                }
                Text(
                    text = "Crop Health Assessment",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = AgriTextPrimary
                )
            }

            if (assessment.isDemoResult) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = AgriPaleYellow,
                    border = androidx.compose.foundation.BorderStroke(0.8.dp, AgriGoldenOchre)
                ) {
                    Text(
                        text = "Demo Analysis",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = AgriEarthBrown,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Visual Status Card with confidence
        val statusColor = when (assessment.visualStatus) {
            VisualHealthStatus.HEALTHY -> AgriForestGreen
            VisualHealthStatus.WATCH -> AgriGoldenOchre
            VisualHealthStatus.MODERATE_RISK -> AgriWarningAmber
            VisualHealthStatus.HIGH_RISK -> AgriTomatoRed
            VisualHealthStatus.INCONCLUSIVE -> AgriSkyBlue
        }

        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = AgriCardSurface),
            border = androidx.compose.foundation.BorderStroke(1.2.dp, statusColor.copy(alpha = 0.5f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(assessment.visualStatus.iconEmoji, fontSize = 24.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = assessment.visualStatus.label,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = statusColor
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = statusColor.copy(alpha = 0.12f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, statusColor.copy(alpha = 0.3f))
                    ) {
                        Text(
                            text = "Confidence: ${assessment.confidencePercent}%",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = statusColor,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = assessment.assessmentTitle,
                    style = MaterialTheme.typography.headlineSmall,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = AgriTextPrimary
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = assessment.visualStatus.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = AgriTextSecondary,
                    lineHeight = 18.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Previous Observation Comparison Box (Requirement 29)
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = AgriSurfaceSubtle),
            border = androidx.compose.foundation.BorderStroke(1.dp, AgriLineSubtle),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "Observation Trajectory",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = AgriForestGreen
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("PREVIOUS (Day 28)", fontSize = 10.sp, color = AgriTextSecondary)
                        Text(
                            text = "Health: ${assessment.previousHealthScore}%",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = AgriForestGreen
                        )
                    }

                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = AgriTextSecondary,
                        modifier = Modifier.size(16.dp)
                    )

                    Column(horizontalAlignment = Alignment.End) {
                        Text("${st("CURRENT")} (${st("Day")} ${assessment.dayNumber})", fontSize = 10.sp, color = AgriTextSecondary)
                        Text(
                            text = "${st("Health")}: ${assessment.currentHealthScore}%",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (assessment.currentHealthScore < assessment.previousHealthScore) AgriTomatoRed else AgriForestGreen
                        )
                    }
                }

                val scoreDiff = assessment.currentHealthScore - assessment.previousHealthScore
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = if (scoreDiff < 0) {
                        "${st("Health score decreased by")} ${-scoreDiff} ${st("points (AI-assisted health score).")}"
                    } else if (scoreDiff > 0) {
                        "${st("Health score improved by")} $scoreDiff ${st("points (AI-assisted health score).")}"
                    } else {
                        "${st("Health score maintained steady at")} ${assessment.currentHealthScore}%."
                    },
                    fontSize = 11.sp,
                    color = if (scoreDiff < 0) AgriTomatoRed else AgriForestGreen,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Visible Symptoms ("What the AI observed")
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = AgriCardSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, AgriLineSubtle),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "What the AI Observed",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = AgriTextPrimary
                )

                Spacer(modifier = Modifier.height(8.dp))

                assessment.visibleSymptoms.forEach { symptom ->
                    Row(
                        modifier = Modifier.padding(vertical = 3.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Text("•", color = AgriForestGreen, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = symptom,
                            style = MaterialTheme.typography.bodyMedium,
                            color = AgriTextPrimary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Relevant visual observations are summarized above from high-contrast foliage analysis.",
                    fontSize = 10.5.sp,
                    color = AgriTextTertiary
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Possible Conditions
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = AgriCardSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, AgriLineSubtle),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Possible Conditions",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = AgriTextPrimary
                )

                Spacer(modifier = Modifier.height(8.dp))

                assessment.possibleConditions.forEach { condition ->
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = AgriSurfaceSubtle,
                        border = androidx.compose.foundation.BorderStroke(1.dp, AgriLineSubtle),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = condition.name,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = AgriTextPrimary,
                                    modifier = Modifier.weight(1f)
                                )
                                Text(
                                    text = condition.confidence,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AgriForestGreen
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Why: ${condition.reason}",
                                fontSize = 11.5.sp,
                                color = AgriTextSecondary,
                                lineHeight = 17.sp
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // What You Can Do Now (Precautions)
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = AgriCardSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, AgriLineSubtle),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "What You Can Do Now",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = AgriForestGreen
                )

                Spacer(modifier = Modifier.height(8.dp))

                assessment.precautions.forEach { precaution ->
                    Row(
                        modifier = Modifier.padding(vertical = 3.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = AgriForestGreen,
                            modifier = Modifier.size(16.dp).padding(top = 2.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = precaution,
                            style = MaterialTheme.typography.bodyMedium,
                            color = AgriTextPrimary
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Recommended Next Steps (DO NOW / MONITOR / CHECK / GET HELP)
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = AgriCardSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, AgriLineSubtle),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Recommended Next Steps",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = AgriTextPrimary
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Phase 1: DO NOW
                if (assessment.nextSteps.doNow.isNotEmpty()) {
                    NextStepPhaseItem(
                        tag = "DO NOW",
                        tagColor = AgriTomatoRed,
                        steps = assessment.nextSteps.doNow
                    )
                }

                // Phase 2: MONITOR
                if (assessment.nextSteps.monitor.isNotEmpty()) {
                    NextStepPhaseItem(
                        tag = "MONITOR",
                        tagColor = AgriGoldenOchre,
                        steps = assessment.nextSteps.monitor
                    )
                }

                // Phase 3: CHECK
                if (assessment.nextSteps.check.isNotEmpty()) {
                    NextStepPhaseItem(
                        tag = "CHECK",
                        tagColor = AgriForestGreen,
                        steps = assessment.nextSteps.check
                    )
                }

                // Phase 4: GET HELP
                if (assessment.nextSteps.getHelp.isNotEmpty()) {
                    NextStepPhaseItem(
                        tag = "GET HELP",
                        tagColor = AgriSkyBlue,
                        steps = assessment.nextSteps.getHelp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Chemical Safety & Professional Verification Notice
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = AgriSurfaceSubtle,
            border = androidx.compose.foundation.BorderStroke(1.dp, AgriLineSubtle),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = AgriEarthBrown,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Agricultural Safety & Expert Verification",
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.5.sp,
                        color = AgriEarthBrown
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = assessment.chemicalSafetyNotice,
                    fontSize = 11.sp,
                    color = AgriTextSecondary,
                    lineHeight = 16.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Visible symptoms may be consistent with emerging issues. Consider verification by an agricultural extension expert before any major intervention.",
                    fontSize = 10.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = AgriTextSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Primary Action: Ask Farm Assistant
        Button(
            onClick = onAskAssistant,
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AgriForestGreen),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("ask_companion_result_button")
        ) {
            LeafAiSparkIcon(size = 20.dp, tintColor = Color.White)
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "Ask Farm Companion About This",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Action: Save to Crop Journey
        OutlinedButton(
            onClick = onSaveToJourney,
            enabled = !isSaved && !isSaving,
            shape = RoundedCornerShape(14.dp),
            border = androidx.compose.foundation.BorderStroke(
                1.2.dp,
                if (isSaved) AgriForestGreen else AgriForestGreen
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("save_to_journey_button")
        ) {
            if (isSaving) {
                CircularProgressIndicator(strokeWidth = 2.dp, modifier = Modifier.size(18.dp), color = AgriForestGreen)
                Spacer(modifier = Modifier.width(8.dp))
                Text(st("Saving to Journey..."), color = AgriForestGreen)
            } else if (isSaved) {
                Icon(Icons.Default.Check, contentDescription = null, tint = AgriForestGreen, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(st("Saved to Crop Journey!"), color = AgriForestGreen, fontWeight = FontWeight.Bold)
            } else {
                Icon(Icons.Default.BookmarkAdd, contentDescription = null, tint = AgriForestGreen, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(st("Save to Crop Journey"), color = AgriForestGreen, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextButton(onClick = onAnalyzeAnother) {
                Text(st("Scan Another Image"), color = AgriForestGreen, fontWeight = FontWeight.SemiBold)
            }
            TextButton(onClick = onViewHistory) {
                Text(st("View History Timeline"), color = AgriTextSecondary, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
fun NextStepPhaseItem(
    tag: String,
    tagColor: Color,
    steps: List<String>
) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Surface(
            shape = RoundedCornerShape(6.dp),
            color = tagColor.copy(alpha = 0.15f),
            border = androidx.compose.foundation.BorderStroke(0.8.dp, tagColor.copy(alpha = 0.4f))
        ) {
            Text(
                text = st(tag),
                fontSize = 10.sp,
                fontWeight = FontWeight.ExtraBold,
                color = tagColor,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        steps.forEach { step ->
            Text(
                text = "• ${st(step)}",
                fontSize = 12.sp,
                color = AgriTextPrimary,
                lineHeight = 17.sp,
                modifier = Modifier.padding(start = 4.dp, bottom = 2.dp)
            )
        }
    }
}

/**
 * 5. OBSERVATION HISTORY TIMELINE SCREEN
 */
@Composable
fun ObservationHistoryScreen(
    cropContext: CropContext,
    onSelectObservation: (CropHealthAssessment) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 18.dp, vertical = 12.dp)
            .testTag("observation_history_screen")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = AgriTextPrimary
                )
            }
            Text(
                text = st("Crop Health History"),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = AgriTextPrimary
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "${st("Historical visual observations for")} ${st(cropContext.cropCycle.cropName)} (${cropContext.farm.landSizeAcres} ${t("common.acres")}).",
            style = MaterialTheme.typography.bodySmall,
            color = AgriTextSecondary
        )

        Spacer(modifier = Modifier.height(14.dp))

        // History items
        val historyList = listOf(
            Triple("Day 35 • Latest Scan", "71% Health", "Possible Foliar Humidity Stress / Mild chlorosis on lower leaves"),
            Triple("Day 28 • Previous Observation", "86% Health", "Optimal vegetative foliage, clean lamina, balanced irrigation"),
            Triple("Day 20 • Early Vegetative", "90% Health", "Seedlings established, strong taproot anchoring, zero disease signs"),
            Triple("Day 01 • Transplanting", "95% Health", "Certified hybrid Shivam seedlings transplanted into red sandy loam beds")
        )

        historyList.forEachIndexed { index, (dayLabel, healthLabel, summary) ->
            Row(modifier = Modifier.fillMaxWidth()) {
                // Vertical Timeline Bar
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .background(if (index == 0) AgriTomatoRed else AgriForestGreen, CircleShape)
                    )
                    if (index < historyList.size - 1) {
                        Box(
                            modifier = Modifier
                                .width(2.dp)
                                .height(70.dp)
                                .background(AgriLineSubtle)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = AgriCardSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, AgriLineSubtle),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = st(dayLabel),
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = AgriTextPrimary
                            )
                            Text(
                                text = st(healthLabel),
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = if (index == 0) AgriTomatoRed else AgriForestGreen
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = st(summary),
                            fontSize = 11.5.sp,
                            color = AgriTextSecondary,
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }
    }
}
