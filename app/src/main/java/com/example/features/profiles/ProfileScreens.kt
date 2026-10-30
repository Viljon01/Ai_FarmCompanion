package com.example.features.profiles

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CropContext
import com.example.data.Farm
import com.example.ui.components.PageHeader
import com.example.ui.theme.*
import com.example.data.localization.*

/**
 * FEATURE 02 — FARMER PROFILE & SESSION KEY MANAGEMENT
 */
@Composable
fun FarmerProfileScreen(
    cropContext: CropContext,
    onSaveProfile: (name: String, phone: String, language: String, location: String) -> Unit,
    onSignOut: (() -> Unit)? = null,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var name by remember { mutableStateOf(cropContext.farmer.name) }
    var phone by remember { mutableStateOf(cropContext.farmer.phone) }
    var language by remember { mutableStateOf(cropContext.farmer.preferredLanguage) }
    var location by remember { mutableStateOf(cropContext.farmer.location) }
    var showSavedMessage by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 14.dp)
    ) {
        PageHeader(
            title = st("Farmer Profile"),
            subtitle = st("Personal credentials & farm preferences"),
            onBack = onBack
        )

        // Account Status Banner Card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = AgriSageLight),
            border = androidx.compose.foundation.BorderStroke(1.2.dp, AgriForestGreen.copy(alpha = 0.4f)),
            modifier = Modifier.fillMaxWidth().testTag("farmer_account_banner")
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = AgriForestGreen, modifier = Modifier.size(28.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(st("ACCOUNT ACTIVE"), style = MaterialTheme.typography.labelSmall, color = AgriForestGreen, fontWeight = FontWeight.Bold)
                    Text(cropContext.farmer.name.ifBlank { "Farmer" }, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = AgriForestGreenDark)
                    Text(cropContext.farmer.phone, style = MaterialTheme.typography.bodySmall, color = AgriTextSecondary, fontSize = 11.sp)
                }
                if (onSignOut != null) {
                    IconButton(onClick = onSignOut) {
                        Icon(Icons.Default.Logout, contentDescription = st("Sign Out"), tint = AgriForestGreen)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Profile Completion Card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = AgriCardSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, AgriLineSubtle),
            modifier = Modifier.fillMaxWidth().testTag("farmer_profile_card")
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(AgriSageLight)
                        .border(1.5.dp, AgriForestGreen, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = name.take(1).uppercase(),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = AgriForestGreen
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    Text(
                        text = name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = AgriTextPrimary
                    )
                    Text(
                        text = "${st("Profile")} ${cropContext.farmer.profileCompletionPercent}% ${st("Complete")}",
                        style = MaterialTheme.typography.bodySmall,
                        color = AgriForestGreen,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = st(location),
                        style = MaterialTheme.typography.bodySmall,
                        color = AgriTextSecondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        Text(
            text = st("Farmer Details"),
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = AgriTextPrimary
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text(st("Farmer Name")) },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth().testTag("profile_name_input")
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text(st("Mobile Number")) },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth().testTag("profile_phone_input")
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = location,
            onValueChange = { location = it },
            label = { Text(st("Location (Taluk / District)")) },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth().testTag("profile_location_input")
        )

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = st("Preferred Language"),
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
            color = AgriTextPrimary
        )

        Spacer(modifier = Modifier.height(6.dp))

        val languages = listOf("English", "தமிழ் (Tamil)", "हिंदी (Hindi)", "తెలుగు (Telugu)")
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            languages.take(2).forEach { lang ->
                val isSelected = language.startsWith(lang.take(4))
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (isSelected) AgriSageLight else AgriCardSurface,
                    border = androidx.compose.foundation.BorderStroke(
                        1.2.dp,
                        if (isSelected) AgriForestGreen else AgriLineSubtle
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .clickable { language = lang }
                ) {
                    Text(
                        text = lang,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) AgriForestGreen else AgriTextPrimary,
                        modifier = Modifier.padding(vertical = 10.dp, horizontal = 8.dp)
                    )
                }
            }
        }

        if (showSavedMessage) {
            Spacer(modifier = Modifier.height(12.dp))
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = AgriSageLight,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = st("✓ Farmer Profile updated successfully."),
                    color = AgriForestGreen,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(10.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                onSaveProfile(name, phone, language, location)
                showSavedMessage = true
            },
            colors = ButtonDefaults.buttonColors(containerColor = AgriForestGreen),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth().height(48.dp).testTag("save_profile_button")
        ) {
            Text(st("Save Profile"), fontWeight = FontWeight.Bold)
        }
    }
}

/**
 * FEATURE 03 — FARM PROFILE & MULTI-FARM CREATION
 */
@Composable
fun FarmProfileScreen(
    cropContext: CropContext,
    registeredFarms: List<Farm> = emptyList(),
    onSwitchFarm: (Farm) -> Unit,
    onAddFarmParcel: (
        farmName: String,
        location: String,
        acres: Double,
        soilType: String,
        waterType: String,
        cropName: String
    ) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showAddDialog by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 14.dp)
    ) {
        PageHeader(
            title = st("My Farm"),
            subtitle = st("Land parcels, soil profile & water availability"),
            onBack = onBack
        )

        // Active Farm Card
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = AgriCardSurface),
            border = androidx.compose.foundation.BorderStroke(1.2.dp, AgriForestGreen.copy(alpha = 0.5f)),
            modifier = Modifier.fillMaxWidth().testTag("active_farm_card")
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = st("ACTIVE FARM PARCEL"),
                            style = MaterialTheme.typography.labelSmall,
                            color = AgriForestGreen,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = st(cropContext.farm.farmName),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = AgriTextPrimary
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = AgriForestGreen
                    ) {
                        Text(
                            text = st("Active"),
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                val farmDetails = listOf(
                    st("Land Size") to "${cropContext.farm.landSizeAcres} ${st("Acres (Cultivated)")}",
                    st("Location") to st(cropContext.farm.location),
                    st("Soil Profile") to st(cropContext.farm.soilType),
                    st("Water Setup") to st(cropContext.farm.waterAvailability),
                    st("Coordinates") to cropContext.farm.coordinates
                )

                farmDetails.forEach { (label, value) ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(label, style = MaterialTheme.typography.bodySmall, color = AgriTextSecondary)
                        Text(value, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold, color = AgriTextPrimary)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = st("Registered Farm Parcels"),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = AgriTextPrimary
        )

        Spacer(modifier = Modifier.height(10.dp))

        val displayFarms = if (registeredFarms.isNotEmpty()) registeredFarms else listOf(
            cropContext.farm,
            Farm(
                id = "farm_demo_2",
                farmerId = cropContext.farmer.id,
                farmName = "Pollachi Orchard & Veggie Patch",
                location = "Pollachi, Tamil Nadu",
                landSizeAcres = 3.5,
                soilType = "Black Clay Loam",
                waterAvailability = "Canal + Well Irrigation",
                coordinates = "10.6609° N, 77.0048° E",
                isDefault = false
            )
        )

        displayFarms.forEach { farm ->
            val isActive = farm.id == cropContext.farm.id
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = if (isActive) AgriSageLight else AgriCardSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, if (isActive) AgriForestGreen else AgriLineSubtle),
                modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(st(farm.farmName), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = AgriTextPrimary)
                        Text("${farm.landSizeAcres} ${st("Acres")} • ${st(farm.location)}", style = MaterialTheme.typography.bodySmall, color = AgriTextSecondary)
                        Text("${st("Soil")}: ${st(farm.soilType)}", style = MaterialTheme.typography.bodySmall, fontSize = 11.sp, color = AgriForestGreen)
                    }

                    if (!isActive) {
                        OutlinedButton(
                            onClick = { onSwitchFarm(farm) },
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(st("Switch"), fontSize = 12.sp)
                        }
                    } else {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = AgriForestGreen
                        ) {
                            Text(st("Current"), color = Color.White, fontSize = 11.sp, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Add Farm Button
        OutlinedButton(
            onClick = { showAddDialog = true },
            colors = ButtonDefaults.outlinedButtonColors(contentColor = AgriForestGreen),
            border = androidx.compose.foundation.BorderStroke(1.2.dp, AgriForestGreen),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth().height(48.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(st("Add New Farm Parcel"), fontWeight = FontWeight.Bold)
        }
    }

    // New Farm Parcel Modal Dialog
    if (showAddDialog) {
        var newFarmName by remember { mutableStateOf("") }
        var newLocation by remember { mutableStateOf("") }
        var newAcres by remember { mutableStateOf("") }
        var newSoil by remember { mutableStateOf("Red Sandy Loam") }
        var newWater by remember { mutableStateOf("Drip Irrigation") }
        var newCrop by remember { mutableStateOf("Tomato") }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text(st("Add New Farm Parcel"), fontWeight = FontWeight.Bold) },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    OutlinedTextField(
                        value = newFarmName,
                        onValueChange = { newFarmName = it },
                        label = { Text(st("Farm Parcel Name")) },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = newLocation,
                        onValueChange = { newLocation = it },
                        label = { Text(st("Location / District (e.g. Salem)")) },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = newAcres,
                        onValueChange = { newAcres = it },
                        label = { Text(st("Land Size in Acres (e.g. 2.5)")) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = newCrop,
                        onValueChange = { newCrop = it },
                        label = { Text(st("Primary Crop Name")) },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = newSoil,
                        onValueChange = { newSoil = it },
                        label = { Text(st("Soil Type")) },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val acresDouble = newAcres.toDoubleOrNull() ?: 2.0
                        onAddFarmParcel(
                            newFarmName.ifBlank { "New Field Parcel" },
                            newLocation.ifBlank { "Coimbatore, Tamil Nadu" },
                            acresDouble,
                            newSoil,
                            newWater,
                            newCrop
                        )
                        showAddDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AgriForestGreen)
                ) {
                    Text(st("Create Farm"))
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text(st("Cancel"))
                }
            }
        )
    }
}
