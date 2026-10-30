package com.example.features.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.CropCycle
import com.example.data.Farm
import com.example.data.Farmer
import com.example.data.localization.AppLanguage
import com.example.data.localization.LanguageSelectionDialog
import com.example.data.localization.LocalAppLanguage
import com.example.data.localization.st
import com.example.ui.components.LeafAiSparkIcon
import com.example.ui.components.SubtleFarmBackground
import com.example.ui.theme.*
import java.util.UUID

/**
 * FEATURE 01 — AUTHENTICATION & MANDATORY SYSTEM ONBOARDING
 * Phone / Session Key Login & 3-Step Farmer Profile, Farm Parcel, Crop Cycle Setup
 * Supports real-time language switching & skippable crop choice for pre-planting stage.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun WelcomeAuthScreen(
    onOnboardingComplete: (farmer: Farmer, farm: Farm, cropCycle: CropCycle, sessionKey: String) -> Unit,
    onSessionRestore: (sessionKey: String) -> Unit,
    onLanguageChange: ((AppLanguage) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    // Current step: 1 = Auth Mode Choice, 2 = Farmer Profile Details, 3 = Farm & Active Crop Setup
    var currentStep by remember { mutableIntStateOf(1) }
    var authMode by remember { mutableStateOf("welcome") } // "welcome", "login", "session_key"
    var showLanguageModal by remember { mutableStateOf(false) }

    val currentLang = LocalAppLanguage.current

    // Step 1: Session / Login fields
    var phoneNumber by remember { mutableStateOf("") }
    var passwordOtp by remember { mutableStateOf("") }
    var sessionKeyInput by remember { mutableStateOf("") }

    // Step 2: Farmer Profile fields
    var fullName by remember { mutableStateOf("") }
    var selectedLanguage by remember { mutableStateOf(currentLang.englishName) }
    var districtLocation by remember { mutableStateOf("Coimbatore, Tamil Nadu") }

    // Step 3: Farm Parcel & Crop fields
    var farmName by remember { mutableStateOf("Green Valley Field A") }
    var landAcresText by remember { mutableStateOf("2.0") }
    var selectedSoilType by remember { mutableStateOf("Red Sandy Loam") }
    var selectedWaterSource by remember { mutableStateOf("Drip Irrigation") }

    // Skippable Crop Choice State
    var isCropAlreadyPlanted by remember { mutableStateOf(true) }
    var cropName by remember { mutableStateOf("Tomato") }
    var cropVariety by remember { mutableStateOf("Shivam Hybrid") }
    var cropAgeDaysText by remember { mutableStateOf("35") }

    var errorMessage by remember { mutableStateOf<String?>(null) }

    val scrollState = rememberScrollState()

    val soilOptions = listOf("Red Sandy Loam", "Black Cotton", "Alluvial Soil", "Clay Loam", "Laterite", "Coastal Sand")
    val waterOptions = listOf("Drip Irrigation", "Borewell Pump", "Canal Water", "Rainfed / Seasonal", "Sprinkler")
    val commonCrops = listOf("Tomato", "Paddy / Rice", "Maize", "Cotton", "Wheat", "Chilli", "Sugarcane", "Banana")

    Scaffold(
        contentWindowInsets = WindowInsets.statusBars
    ) { innerPadding ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(AgriWarmBg)
        ) {
        SubtleFarmBackground()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp, vertical = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            // Header Brand Identity & Language Switcher Pill
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(AgriSageLight)
                            .border(1.5.dp, AgriForestGreen.copy(alpha = 0.4f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        LeafAiSparkIcon(size = 28.dp)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = st("common.brandName"),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = AgriForestGreen,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = st("common.systemDescription"),
                            style = MaterialTheme.typography.bodySmall,
                            color = AgriTextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }

                // Global Language Selection Pill for Login/Onboarding Page
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = AgriSageLight,
                    border = androidx.compose.foundation.BorderStroke(1.2.dp, AgriForestGreen),
                    modifier = Modifier
                        .clickable { showLanguageModal = true }
                        .testTag("auth_language_selector")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Language,
                            contentDescription = st("Select Language"),
                            tint = AgriForestGreen,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = currentLang.nativeName,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = AgriForestGreen
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Onboarding Progress Stepper Header
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = AgriCardSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, AgriLineSubtle),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    StepIndicator(stepNumber = 1, title = "common.account", isActive = currentStep == 1, isCompleted = currentStep > 1)
                    Divider(modifier = Modifier.weight(1f).padding(horizontal = 6.dp), color = AgriLineSubtle)
                    StepIndicator(stepNumber = 2, title = "settings.title", isActive = currentStep == 2, isCompleted = currentStep > 2)
                    Divider(modifier = Modifier.weight(1f).padding(horizontal = 6.dp), color = AgriLineSubtle)
                    StepIndicator(stepNumber = 3, title = "common.farmAndCrop", isActive = currentStep == 3, isCompleted = false)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // ERROR BANNER IF ANY
            if (errorMessage != null) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = AgriTomatoRed.copy(alpha = 0.1f)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, AgriTomatoRed.copy(alpha = 0.4f)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Warning, contentDescription = null, tint = AgriTomatoRed)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = st(errorMessage!!),
                            color = AgriTomatoRed,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // STEP CONTENT
            when (currentStep) {
                1 -> {
                    // STEP 1: AUTH METHOD / WELCOME
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = AgriCardSurface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, AgriLineSubtle),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Image(
                                painter = painterResource(id = R.drawable.img_farmer_hero),
                                contentDescription = st("Indian farmer with crop field"),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(140.dp)
                                    .clip(RoundedCornerShape(12.dp)),
                                contentScale = ContentScale.Fit
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = st("auth.welcomeTitle"),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = AgriTextPrimary
                            )
                            Text(
                                text = st("auth.welcomeSubtitle"),
                                style = MaterialTheme.typography.bodySmall,
                                color = AgriTextSecondary,
                                lineHeight = 18.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    when (authMode) {
                        "welcome" -> {
                            Button(
                                onClick = { currentStep = 2 },
                                colors = ButtonDefaults.buttonColors(containerColor = AgriForestGreen),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp)
                                    .testTag("auth_new_farmer_setup")
                            ) {
                                Icon(Icons.Default.PersonAdd, contentDescription = null, tint = Color.White)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(st("auth.createProfile"), fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedButton(
                                onClick = { authMode = "login" },
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = AgriForestGreen),
                                border = androidx.compose.foundation.BorderStroke(1.2.dp, AgriForestGreen),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth().height(50.dp).testTag("auth_existing_login")
                            ) {
                                Icon(Icons.Default.Phone, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(st("auth.signIn"), fontSize = 15.sp, fontWeight = FontWeight.Bold, color = AgriForestGreen)
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            TextButton(
                                onClick = { authMode = "session_key" },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.Key, contentDescription = null, tint = AgriEarthBrown, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(st("auth.restoreSession"), color = AgriEarthBrown, fontWeight = FontWeight.SemiBold)
                            }
                        }

                        "login" -> {
                            Card(
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = AgriCardSurface),
                                border = androidx.compose.foundation.BorderStroke(1.dp, AgriLineSubtle),
                                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        text = st("Sign In to Existing Account"),
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = AgriTextPrimary
                                    )

                                    Spacer(modifier = Modifier.height(12.dp))

                                    OutlinedTextField(
                                        value = phoneNumber,
                                        onValueChange = { phoneNumber = it },
                                        label = { Text(st("Mobile Number (+91)")) },
                                        leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = AgriForestGreen) },
                                        shape = RoundedCornerShape(12.dp),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = AgriForestGreen,
                                            unfocusedBorderColor = AgriLineSubtle
                                        ),
                                        modifier = Modifier.fillMaxWidth().testTag("auth_phone_input")
                                    )

                                    Spacer(modifier = Modifier.height(10.dp))

                                    OutlinedTextField(
                                        value = passwordOtp,
                                        onValueChange = { passwordOtp = it },
                                        label = { Text(st("Password / OTP")) },
                                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = AgriForestGreen) },
                                        shape = RoundedCornerShape(12.dp),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = AgriForestGreen,
                                            unfocusedBorderColor = AgriLineSubtle
                                        ),
                                        modifier = Modifier.fillMaxWidth()
                                    )

                                    Spacer(modifier = Modifier.height(14.dp))

                                    Button(
                                        onClick = {
                                            if (phoneNumber.length < 5) {
                                                errorMessage = "Please enter your valid mobile number."
                                            } else {
                                                errorMessage = null
                                                fullName = "Farmer ($phoneNumber)"
                                                currentStep = 2
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = AgriForestGreen),
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier.fillMaxWidth().height(48.dp)
                                    ) {
                                        Text(st("Continue to Setup"), fontWeight = FontWeight.Bold, color = Color.White)
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    TextButton(onClick = { authMode = "welcome" }) {
                                        Text(st("Back"), color = AgriTextSecondary)
                                    }
                                }
                            }
                        }

                        "session_key" -> {
                            Card(
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = AgriCardSurface),
                                border = androidx.compose.foundation.BorderStroke(1.dp, AgriLineSubtle),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        text = st("Enter Session Key"),
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = AgriTextPrimary
                                    )
                                    Text(
                                        text = st("Sync your active farm context across devices using your unique Kisan session key (e.g. KSN-8842-TN)."),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = AgriTextSecondary
                                    )

                                    Spacer(modifier = Modifier.height(12.dp))

                                    OutlinedTextField(
                                        value = sessionKeyInput,
                                        onValueChange = { sessionKeyInput = it },
                                        label = { Text(st("Session Key (KSN-XXXX-IN)")) },
                                        leadingIcon = { Icon(Icons.Default.Key, contentDescription = null, tint = AgriForestGreen) },
                                        shape = RoundedCornerShape(12.dp),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = AgriForestGreen,
                                            unfocusedBorderColor = AgriLineSubtle
                                        ),
                                        modifier = Modifier.fillMaxWidth().testTag("auth_session_key_input")
                                    )

                                    Spacer(modifier = Modifier.height(14.dp))

                                    Button(
                                        onClick = {
                                            if (sessionKeyInput.isBlank()) {
                                                errorMessage = "Please enter your Kisan Session Key."
                                            } else {
                                                errorMessage = null
                                                onSessionRestore(sessionKeyInput.trim())
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = AgriForestGreen),
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier.fillMaxWidth().height(48.dp)
                                    ) {
                                        Text(st("Connect Session"), fontWeight = FontWeight.Bold, color = Color.White)
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    TextButton(onClick = { authMode = "welcome" }) {
                                        Text(st("Back"), color = AgriTextSecondary)
                                    }
                                }
                            }
                        }
                    }
                }

                2 -> {
                    // STEP 2: FARMER PROFILE DETAILS
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = AgriCardSurface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, AgriLineSubtle),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Text(
                                text = st("Step 2: Farmer Identity & Preferences"),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = AgriForestGreen
                            )
                            Text(
                                text = st("Provide your farmer details so the AI engine can tailor localized weather, soil health, and advisory recommendations."),
                                style = MaterialTheme.typography.bodySmall,
                                color = AgriTextSecondary
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Name Field
                            OutlinedTextField(
                                value = fullName,
                                onValueChange = { fullName = it },
                                label = { Text(st("Full Name")) },
                                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = AgriForestGreen) },
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = AgriForestGreen,
                                    unfocusedBorderColor = AgriLineSubtle
                                ),
                                modifier = Modifier.fillMaxWidth().testTag("farmer_name_input")
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // Phone Field
                            OutlinedTextField(
                                value = phoneNumber,
                                onValueChange = { phoneNumber = it },
                                label = { Text(st("Mobile Number")) },
                                leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = AgriForestGreen) },
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = AgriForestGreen,
                                    unfocusedBorderColor = AgriLineSubtle
                                ),
                                modifier = Modifier.fillMaxWidth().testTag("farmer_phone_input")
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // District Location Field
                            OutlinedTextField(
                                value = districtLocation,
                                onValueChange = { districtLocation = it },
                                label = { Text(st("District / Location (e.g. Coimbatore, Tamil Nadu)")) },
                                leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null, tint = AgriForestGreen) },
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = AgriForestGreen,
                                    unfocusedBorderColor = AgriLineSubtle
                                ),
                                modifier = Modifier.fillMaxWidth().testTag("farmer_location_input")
                            )

                            Spacer(modifier = Modifier.height(18.dp))

                            Button(
                                onClick = {
                                    if (fullName.isBlank()) {
                                        errorMessage = "Please enter your full name."
                                    } else if (districtLocation.isBlank()) {
                                        errorMessage = "Please enter your district or location."
                                    } else {
                                        errorMessage = null
                                        currentStep = 3
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = AgriForestGreen),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth().height(50.dp).testTag("proceed_to_farm_setup")
                            ) {
                                Text(st("Proceed to Farm & Crop Setup →"), fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            TextButton(onClick = { currentStep = 1 }, modifier = Modifier.align(Alignment.CenterHorizontally)) {
                                Text(st("Back"), color = AgriTextSecondary)
                            }
                        }
                    }
                }

                3 -> {
                    // STEP 3: FARM PARCEL & CROP SETUP (WITH SKIPPABLE CROP SELECTION)
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = AgriCardSurface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, AgriLineSubtle),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Text(
                                text = st("Step 3: Farm Parcel & Crop Setup"),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = AgriForestGreen
                            )
                            Text(
                                text = st("Configure your field parcel. You can specify an active crop or skip crop selection to get AI suggestions first."),
                                style = MaterialTheme.typography.bodySmall,
                                color = AgriTextSecondary
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Farm Name
                            OutlinedTextField(
                                value = farmName,
                                onValueChange = { farmName = it },
                                label = { Text(st("Farm / Parcel Name")) },
                                leadingIcon = { Icon(Icons.Default.Terrain, contentDescription = null, tint = AgriForestGreen) },
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = AgriForestGreen,
                                    unfocusedBorderColor = AgriLineSubtle
                                ),
                                modifier = Modifier.fillMaxWidth().testTag("farm_name_input")
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            // Land Area (Acres)
                            OutlinedTextField(
                                value = landAcresText,
                                onValueChange = { landAcresText = it },
                                label = { Text(st("Land Area (Acres)")) },
                                leadingIcon = { Icon(Icons.Default.Straighten, contentDescription = null, tint = AgriForestGreen) },
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = AgriForestGreen,
                                    unfocusedBorderColor = AgriLineSubtle
                                ),
                                modifier = Modifier.fillMaxWidth().testTag("farm_acres_input")
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // Soil Type
                            Text(text = st("Soil Type"), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                soilOptions.forEach { soil ->
                                    FilterChip(
                                        selected = selectedSoilType == soil,
                                        onClick = { selectedSoilType = soil },
                                        label = { Text(st(soil), fontSize = 12.sp) },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = AgriForestGreen,
                                            selectedLabelColor = Color.White
                                        )
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Water / Irrigation Source
                            Text(text = st("Water / Irrigation Source"), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                waterOptions.forEach { water ->
                                    FilterChip(
                                        selected = selectedWaterSource == water,
                                        onClick = { selectedWaterSource = water },
                                        label = { Text(st(water), fontSize = 12.sp) },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = AgriForestGreen,
                                            selectedLabelColor = Color.White
                                        )
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))
                            Divider(color = AgriLineSubtle)
                            Spacer(modifier = Modifier.height(16.dp))

                            // CROP CHOICE MODE (ACTIVE CROP vs PRE-PLANTING SKIPPABLE PLANNER)
                            Text(
                                text = st("Crop Status in Field"),
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = AgriForestGreen
                            )
                            Spacer(modifier = Modifier.height(6.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                // Option A: Crop Already Planted
                                Card(
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (isCropAlreadyPlanted) AgriSageLight else AgriSurfaceSubtle
                                    ),
                                    border = androidx.compose.foundation.BorderStroke(
                                        if (isCropAlreadyPlanted) 1.5.dp else 1.dp,
                                        if (isCropAlreadyPlanted) AgriForestGreen else AgriLineSubtle
                                    ),
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { isCropAlreadyPlanted = true }
                                        .testTag("crop_mode_planted")
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            RadioButton(
                                                selected = isCropAlreadyPlanted,
                                                onClick = { isCropAlreadyPlanted = true },
                                                colors = RadioButtonDefaults.colors(selectedColor = AgriForestGreen)
                                            )
                                            Text(
                                                text = st("Crop Already Planted"),
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isCropAlreadyPlanted) AgriForestGreen else AgriTextPrimary
                                            )
                                        }
                                        Text(
                                            text = st("Specify crop name, variety, and age."),
                                            fontSize = 10.5.sp,
                                            color = AgriTextSecondary
                                        )
                                    }
                                }

                                // Option B: Haven't Planted Yet (Skippable)
                                Card(
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (!isCropAlreadyPlanted) AgriSageLight else AgriSurfaceSubtle
                                    ),
                                    border = androidx.compose.foundation.BorderStroke(
                                        if (!isCropAlreadyPlanted) 1.5.dp else 1.dp,
                                        if (!isCropAlreadyPlanted) AgriForestGreen else AgriLineSubtle
                                    ),
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { isCropAlreadyPlanted = false }
                                        .testTag("crop_mode_planning_skip")
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            RadioButton(
                                                selected = !isCropAlreadyPlanted,
                                                onClick = { isCropAlreadyPlanted = false },
                                                colors = RadioButtonDefaults.colors(selectedColor = AgriForestGreen)
                                            )
                                            Text(
                                                text = st("Haven't Planted Yet (Skip)"),
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (!isCropAlreadyPlanted) AgriForestGreen else AgriTextPrimary
                                            )
                                        }
                                        Text(
                                            text = st("Get AI crop suggestions first."),
                                            fontSize = 10.5.sp,
                                            color = AgriTextSecondary
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            if (isCropAlreadyPlanted) {
                                // ACTIVE CROP INPUT FIELDS
                                Text(
                                    text = st("Select Active Crop Choice"),
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = AgriTextPrimary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                FlowRow(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    commonCrops.forEach { crop ->
                                        FilterChip(
                                            selected = cropName.equals(crop, ignoreCase = true),
                                            onClick = { cropName = crop },
                                            label = { Text(st(crop), fontSize = 12.sp) },
                                            colors = FilterChipDefaults.filterChipColors(
                                                selectedContainerColor = AgriForestGreen,
                                                selectedLabelColor = Color.White
                                            )
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                OutlinedTextField(
                                    value = cropName,
                                    onValueChange = { cropName = it },
                                    label = { Text(st("Crop Name (or custom)")) },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = AgriForestGreen,
                                        unfocusedBorderColor = AgriLineSubtle
                                    ),
                                    modifier = Modifier.fillMaxWidth().testTag("crop_name_input")
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                OutlinedTextField(
                                    value = cropVariety,
                                    onValueChange = { cropVariety = it },
                                    label = { Text(st("Crop Variety / Hybrid Name")) },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = AgriForestGreen,
                                        unfocusedBorderColor = AgriLineSubtle
                                    ),
                                    modifier = Modifier.fillMaxWidth().testTag("crop_variety_input")
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                OutlinedTextField(
                                    value = cropAgeDaysText,
                                    onValueChange = { cropAgeDaysText = it },
                                    label = { Text(st("Days Since Sowing / Planting (e.g. 35)")) },
                                    leadingIcon = { Icon(Icons.Default.Event, contentDescription = null, tint = AgriForestGreen) },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = AgriForestGreen,
                                        unfocusedBorderColor = AgriLineSubtle
                                    ),
                                    modifier = Modifier.fillMaxWidth().testTag("crop_age_input")
                                )
                            } else {
                                // PRE-PLANTING SKIPPED BANNER
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = AgriSageLight.copy(alpha = 0.6f)),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, AgriForestGreen.copy(alpha = 0.4f)),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.Lightbulb, contentDescription = null, tint = AgriForestGreen)
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = st("Pre-Planting Planning Mode Active"),
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp,
                                                color = AgriForestGreen
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = st("Your farm setup will launch directly into Stage 01 Crop Planner. You can review soil suitability, profit estimates, and choose your crop anytime!"),
                                            fontSize = 11.5.sp,
                                            color = AgriTextSecondary,
                                            lineHeight = 16.sp
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            Button(
                                onClick = {
                                    val acres = landAcresText.toDoubleOrNull() ?: 2.0
                                    val ageDays = if (isCropAlreadyPlanted) (cropAgeDaysText.toIntOrNull() ?: 35) else 0
                                    val farmerId = "farmer_${UUID.randomUUID().toString().take(6)}"
                                    val farmId = "farm_${UUID.randomUUID().toString().take(6)}"
                                    val cycleId = "cycle_${UUID.randomUUID().toString().take(6)}"
                                    val sessionKey = "KSN-${(1000..9999).random()}-IN"

                                    val farmer = Farmer(
                                        id = farmerId,
                                        name = fullName.ifBlank { "Farmer" },
                                        phone = phoneNumber.ifBlank { "+91 98765 43210" },
                                        preferredLanguage = currentLang.englishName,
                                        location = districtLocation.ifBlank { "Coimbatore, Tamil Nadu" },
                                        profileCompletionPercent = 100
                                    )

                                    val farm = Farm(
                                        id = farmId,
                                        farmerId = farmerId,
                                        farmName = farmName.ifBlank { "Parcel A" },
                                        location = districtLocation.ifBlank { "Coimbatore, Tamil Nadu" },
                                        landSizeAcres = acres,
                                        soilType = selectedSoilType,
                                        waterAvailability = selectedWaterSource,
                                        isDefault = true
                                    )

                                    val (sName, sDesc, finalCropName, finalVariety, stageIdx) = if (isCropAlreadyPlanted) {
                                        val idx = when {
                                            ageDays < 15 -> 1
                                            ageDays < 30 -> 2
                                            ageDays < 50 -> 3
                                            ageDays < 70 -> 4
                                            ageDays < 90 -> 5
                                            else -> 6
                                        }
                                        val stageMap = mapOf(
                                            Pair(1, Pair("01 PLAN", "Crop Planning & Bed Preparation")),
                                            Pair(2, Pair("02 GROW", "Vegetative Nursery & Sowing Care")),
                                            Pair(3, Pair("03 MONITOR", "Vegetative Canopy & Leaf Health")),
                                            Pair(4, Pair("04 PROTECT", "Flowering & Pest Protection")),
                                            Pair(5, Pair("05 HARVEST", "Fruit Set & Harvest Forecast")),
                                            Pair(6, Pair("06 SELL", "Market Sales & Transport"))
                                        )
                                        val pair = stageMap[idx] ?: Pair("03 MONITOR", "Crop Monitoring")
                                        Tuple5(pair.first, pair.second, cropName.ifBlank { "Tomato" }, cropVariety.ifBlank { "Hybrid" }, idx)
                                    } else {
                                        Tuple5("01 PLAN", "Crop Selection & Soil Evaluation", "Planning Phase (Pre-sowing)", "To Be Decided", 1)
                                    }

                                    val cropCycle = CropCycle(
                                        id = cycleId,
                                        farmId = farmId,
                                        cropName = finalCropName,
                                        variety = finalVariety,
                                        acres = acres,
                                        cropAgeDays = ageDays,
                                        currentStageName = sName,
                                        currentStageIndex = stageIdx,
                                        currentStageDescription = sDesc
                                    )

                                    onOnboardingComplete(farmer, farm, cropCycle, sessionKey)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = AgriForestGreen),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth().height(52.dp).testTag("finish_onboarding_button")
                            ) {
                                Icon(Icons.Default.Check, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isCropAlreadyPlanted) st("Complete Setup & Launch Dashboard") else st("Start with Stage 01 Crop Planner →"),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            TextButton(onClick = { currentStep = 2 }, modifier = Modifier.align(Alignment.CenterHorizontally)) {
                                Text(st("Back"), color = AgriTextSecondary)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        // Language Switcher Dialog for Login Screen
        if (showLanguageModal) {
            LanguageSelectionDialog(
                currentLanguage = currentLang,
                onSelectLanguage = { lang ->
                    onLanguageChange?.invoke(lang)
                    showLanguageModal = false
                },
                onDismiss = { showLanguageModal = false }
            )
        }
    }
}
}

private data class Tuple5<A, B, C, D, E>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D,
    val fifth: E
)

@Composable
private fun StepIndicator(
    stepNumber: Int,
    title: String,
    isActive: Boolean,
    isCompleted: Boolean
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(
                    when {
                        isCompleted || isActive -> AgriForestGreen
                        else -> AgriLineSubtle
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isCompleted) {
                Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
            } else {
                Text(
                    text = stepNumber.toString(),
                    color = if (isActive) Color.White else AgriTextSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = st(title),
            fontSize = 11.sp,
            fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
            color = if (isActive) AgriForestGreen else AgriTextSecondary
        )
    }
}
