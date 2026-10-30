package com.farmcompanion.ui.screens

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.farmcompanion.ml.PlantDiseaseClassifier
import com.farmcompanion.ml.ScreeningResult
import com.farmcompanion.ml.ScreeningStatus
import com.farmcompanion.recommendation.AgronomicRecommendation
import com.farmcompanion.recommendation.RecommendationService
import kotlinx.coroutines.launch

/**
 * Jetpack Compose Crop Health Screening Screen for Farm Companion.
 *
 * Demonstrates:
 * 1. Pure Computer Vision inference execution (`PlantDiseaseClassifier`)
 * 2. Decoupled agricultural guidance consumption (`RecommendationService`)
 * 3. Enforced provisional confidence threshold (0.70f)
 * 4. Distinct presentation of preliminary screening vs educational recommendations
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CropHealthScreen() {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // Manage classifier lifecycle
    val classifier = remember {
        PlantDiseaseClassifier(
            context = context,
            modelFilename = "leaf_disease_model.tflite",
            labelsFilename = "labels.txt",
            confidenceThreshold = 0.70f
        )
    }

    DisposableEffect(Unit) {
        onDispose {
            classifier.close()
        }
    }

    var selectedBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var isAnalyzing by remember { mutableStateOf(false) }
    var screeningResult by remember { mutableStateOf<ScreeningResult?>(null) }
    var agronomicRecommendation by remember { mutableStateOf<AgronomicRecommendation?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            try {
                val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    ImageDecoder.decodeBitmap(ImageDecoder.createSource(context.contentResolver, it)) { decoder, _, _ ->
                        decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
                        decoder.isMutableRequired = true
                    }
                } else {
                    @Suppress("DEPRECATION")
                    MediaStore.Images.Media.getBitmap(context.contentResolver, it)
                }

                selectedBitmap = bitmap
                screeningResult = null
                agronomicRecommendation = null
                errorMessage = null

                // Execute CV screening asynchronously off the UI thread
                coroutineScope.launch {
                    isAnalyzing = true
                    val result = classifier.classify(bitmap)
                    isAnalyzing = false

                    result.onSuccess { screenRes ->
                        screeningResult = screenRes
                        // Decoupled recommendation generation based on verified screening output
                        agronomicRecommendation = RecommendationService.getRecommendation(screenRes)
                    }.onFailure { err ->
                        errorMessage = "Screening error: ${err.localizedMessage}"
                    }
                }
            } catch (e: Exception) {
                errorMessage = "Failed to load leaf image: ${e.localizedMessage}"
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Leaf Disease Screening", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Image Preview Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                if (selectedBitmap != null) {
                    Image(
                        bitmap = selectedBitmap!!.asImageBitmap(),
                        contentDescription = "Selected Leaf Photograph",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Text(
                        text = "No leaf photograph selected",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 14.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { imagePickerLauncher.launch("image/*") },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isAnalyzing
            ) {
                Text(if (selectedBitmap == null) "Select Leaf Photograph" else "Choose Different Leaf")
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (isAnalyzing) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                Text("Analyzing leaf symptoms with local ML model...", style = MaterialTheme.typography.bodySmall)
            }

            errorMessage?.let { error ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                ) {
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            // 1. PURE COMPUTER VISION SCREENING CARD
            screeningResult?.let { screenRes ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "COMPUTER VISION SCREENING RESULT",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        if (screenRes.status == ScreeningStatus.UNCERTAIN_SCREENING) {
                            // Uncertain Screening Banner
                            Row(verticalAlignment = Alignment.Top) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "Uncertain Screening",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp,
                                        color = MaterialTheme.colorScheme.error
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = screenRes.screeningMessage,
                                        fontSize = 13.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Top score: ${String.format("%.1f", screenRes.topScore * 100)}% (Required threshold: ${String.format("%.0f", screenRes.thresholdApplied * 100)}%)",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        } else {
                            // Confirmed Preliminary Result
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (screenRes.topClass == "Healthy") Icons.Default.CheckCircle else Icons.Default.Info,
                                    contentDescription = null,
                                    tint = if (screenRes.topClass == "Healthy") Color(0xFF2E7D32) else Color(0xFFE65100),
                                    modifier = Modifier.size(28.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = screenRes.topClass,
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Preliminary match (${String.format("%.1f", screenRes.topScore * 100)}% confidence)",
                                        fontSize = 14.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        Divider(modifier = Modifier.padding(vertical = 12.dp))

                        // Full Model Score Breakdown
                        Text(
                            text = "Model Probability Breakdown:",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        screenRes.classScores.forEach { (clazz, score) ->
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(clazz, fontSize = 12.sp)
                                Text("${String.format("%.1f", score * 100)}%", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                            LinearProgressIndicator(
                                progress = score,
                                modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp))
                            )
                        }
                    }
                }
            }

            // 2. DECOUPLED AGRONOMIC RECOMMENDATION CARD
            agronomicRecommendation?.let { rec ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "AGRONOMIC GUIDANCE (DECOUPLED SERVICE)",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.secondary
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = rec.categoryTitle,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = rec.summary,
                            fontSize = 13.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Recommended Cultural Practices:",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        rec.culturalPractices.forEach { practice ->
                            Text(
                                text = "• $practice",
                                fontSize = 12.sp,
                                modifier = Modifier.padding(vertical = 2.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = rec.generalEducation,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Divider(modifier = Modifier.padding(vertical = 10.dp))

                        Text(
                            text = rec.disclaimer,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            }
        }
    }
}
