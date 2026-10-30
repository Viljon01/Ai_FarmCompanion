package com.example.features.settings

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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CropContext
import com.example.ui.components.PageHeader
import com.example.ui.theme.*
import com.example.data.localization.*

/**
 * FEATURE 13 — SETTINGS & OFFLINE PREFERENCES
 * Language localization, units preference, offline caching toggle, and data sync controls.
 */
@Composable
fun SettingsScreen(
    cropContext: CropContext,
    onToggleOffline: () -> Unit,
    onSignOut: (() -> Unit)? = null,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    var selectedLanguage by remember { mutableStateOf("English") }
    var selectedUnit by remember { mutableStateOf("Acres / kg") }
    var syncMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 18.dp, vertical = 12.dp)
    ) {
        PageHeader(
            title = st("Settings"),
            subtitle = st("Field connectivity, languages & regional units"),
            onBack = onBack
        )

        // Offline Mode Card
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = AgriCardSurface),
            border = androidx.compose.foundation.BorderStroke(1.2.dp, AgriLineSubtle),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (cropContext.isOffline) Icons.Default.CloudOff else Icons.Default.CloudQueue,
                                contentDescription = null,
                                tint = if (cropContext.isOffline) AgriTomatoRed else AgriForestGreen,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = st("Offline Field Mode"),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = AgriTextPrimary
                            )
                        }
                        Text(
                            text = st("Keep observation records & stage guidance available without active 4G/5G connection in remote fields."),
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 11.sp,
                            color = AgriTextSecondary
                        )
                    }

                    Switch(
                        checked = cropContext.isOffline,
                        onCheckedChange = { onToggleOffline() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = AgriForestGreen,
                            checkedTrackColor = AgriSageLight
                        ),
                        modifier = Modifier.testTag("offline_mode_switch")
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Language Selection Card
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = AgriCardSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, AgriLineSubtle),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = st("REGIONAL LANGUAGE PREFERENCE"),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = AgriTextSecondary
                )
                Spacer(modifier = Modifier.height(10.dp))

                val languages = listOf("English", "தமிழ் (Tamil)", "हिन्दी (Hindi)", "తెలుగు (Telugu)", "ಕನ್ನಡ (Kannada)")
                languages.forEach { lang ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedLanguage = lang }
                            .padding(vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedLanguage == lang,
                            onClick = { selectedLanguage = lang },
                            colors = RadioButtonDefaults.colors(selectedColor = AgriForestGreen)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(lang, fontSize = 13.sp, color = AgriTextPrimary)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Units Preference Card
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = AgriCardSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, AgriLineSubtle),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = st("MEASUREMENT UNITS"),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = AgriTextSecondary
                )
                Spacer(modifier = Modifier.height(10.dp))

                val units = listOf("Acres / kg / Tonnes", "Hectares / Quintals")
                units.forEach { unit ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedUnit = unit }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedUnit == unit,
                            onClick = { selectedUnit = unit },
                            colors = RadioButtonDefaults.colors(selectedColor = AgriForestGreen)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(st(unit), fontSize = 13.sp, color = AgriTextPrimary)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Cloud Sync & Cache Card
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = AgriCardSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, AgriLineSubtle),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = st("DATA SYNCHRONIZATION"),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = AgriTextSecondary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = st("Last synced: Today, 08:30 AM • 4 observations & 3 market quotes cached locally."),
                    fontSize = 11.sp,
                    color = AgriTextSecondary
                )
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = { syncMessage = "Field database synced successfully with Tamil Nadu Agri Cloud." },
                    colors = ButtonDefaults.buttonColors(containerColor = AgriForestGreen),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Sync, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(st("Sync Data Now"), fontSize = 12.sp)
                }

                if (syncMessage != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(st(syncMessage!!), fontSize = 11.sp, color = AgriForestGreen, fontWeight = FontWeight.Medium)
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Account & Session Log Out Card
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = AgriTomatoRed.copy(alpha = 0.08f)),
            border = androidx.compose.foundation.BorderStroke(1.2.dp, AgriTomatoRed.copy(alpha = 0.3f)),
            modifier = Modifier.fillMaxWidth().testTag("settings_logout_card")
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Logout, contentDescription = null, tint = AgriTomatoRed, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = st("Log Out & Reset Session"),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = AgriTomatoRed
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = st("Disconnect active Kisan session (${cropContext.farmer.name}) and return to Login & Setup flow."),
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 11.sp,
                            color = AgriTextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = { onSignOut?.invoke() },
                    colors = ButtonDefaults.buttonColors(containerColor = AgriTomatoRed),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth().height(42.dp).testTag("settings_logout_button")
                ) {
                    Icon(Icons.Default.Logout, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(st("Log Out Now"), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // App Information
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("AI Farm Companion v1.0", fontSize = 11.sp, color = AgriTextSecondary, fontWeight = FontWeight.Bold)
            Text(st("Knowledge base powered by ICAR & TNAU agronomy protocols"), fontSize = 10.sp, color = AgriTextSecondary)
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
