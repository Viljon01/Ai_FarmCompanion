package com.example.features.notifications

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.data.NotificationCategory
import com.example.data.NotificationItem
import com.example.ui.components.AppDestination
import com.example.ui.components.EmptyStateView
import com.example.ui.components.PageHeader
import com.example.ui.theme.*
import com.example.data.localization.*

/**
 * FEATURE 12 — GROUPED NOTIFICATION CENTER
 * Filterable notifications with unread states, direct navigation links,
 * and mark-all-read action.
 */
@Composable
fun NotificationCenterScreen(
    cropContext: CropContext,
    onMarkRead: (String) -> Unit,
    onMarkAllRead: () -> Unit,
    onNavigate: (AppDestination) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedFilter by remember { mutableStateOf<NotificationCategory?>(null) }

    val notifications = cropContext.notifications.filter { item ->
        selectedFilter == null || item.category == selectedFilter
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 18.dp, vertical = 12.dp)
    ) {
        PageHeader(
            title = st("Notifications"),
            subtitle = st("Prioritized farm alerts & real-time updates"),
            onBack = onBack,
            action = {
                TextButton(
                    onClick = onMarkAllRead,
                    modifier = Modifier.testTag("mark_all_read_button")
                ) {
                    Text(st("Clear All"), fontSize = 12.sp, color = AgriForestGreen, fontWeight = FontWeight.Bold)
                }
            }
        )

        // Filter Chips Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = selectedFilter == null,
                onClick = { selectedFilter = null },
                label = { Text(st("All"), fontSize = 11.sp) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = AgriSageLight,
                    selectedLabelColor = AgriForestGreen
                )
            )

            FilterChip(
                selected = selectedFilter == NotificationCategory.ALERT,
                onClick = { selectedFilter = NotificationCategory.ALERT },
                label = { Text(st("Alerts"), fontSize = 11.sp) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = AgriSageLight,
                    selectedLabelColor = AgriTomatoRed
                )
            )

            FilterChip(
                selected = selectedFilter == NotificationCategory.GUIDANCE,
                onClick = { selectedFilter = NotificationCategory.GUIDANCE },
                label = { Text(st("Guidance"), fontSize = 11.sp) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = AgriSageLight,
                    selectedLabelColor = AgriForestGreen
                )
            )

            FilterChip(
                selected = selectedFilter == NotificationCategory.MARKET,
                onClick = { selectedFilter = NotificationCategory.MARKET },
                label = { Text(st("Market"), fontSize = 11.sp) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = AgriSageLight,
                    selectedLabelColor = AgriForestGreen
                )
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (notifications.isEmpty()) {
            EmptyStateView(
                title = st("No Notifications"),
                description = st("You are up to date! Field telemetry and market quotes will notify you as soon as changes occur.")
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(notifications) { item ->
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (item.isRead) AgriCardSurface else AgriSageLight.copy(alpha = 0.4f)
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (!item.isRead) AgriForestGreen.copy(alpha = 0.5f) else AgriLineSubtle
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onMarkRead(item.id)
                                when (item.category) {
                                    NotificationCategory.ALERT -> onNavigate(AppDestination.RISK)
                                    NotificationCategory.GUIDANCE -> onNavigate(AppDestination.HEALTH)
                                    NotificationCategory.MARKET -> onNavigate(AppDestination.SELLING)
                                    NotificationCategory.WEATHER -> onNavigate(AppDestination.WEATHER)
                                    else -> onNavigate(AppDestination.DASHBOARD)
                                }
                            }
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when (item.category) {
                                            NotificationCategory.ALERT -> AgriPaleYellow
                                            NotificationCategory.MARKET -> AgriSageLight
                                            else -> AgriWarmBg
                                        }
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = when (item.category) {
                                        NotificationCategory.ALERT -> Icons.Default.Warning
                                        NotificationCategory.MARKET -> Icons.Default.Storefront
                                        NotificationCategory.WEATHER -> Icons.Default.Cloud
                                        else -> Icons.Default.Eco
                                    },
                                    contentDescription = null,
                                    tint = when (item.category) {
                                        NotificationCategory.ALERT -> AgriTomatoRed
                                        NotificationCategory.MARKET -> AgriForestGreen
                                        else -> AgriForestGreen
                                    },
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = st(item.title),
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = if (!item.isRead) FontWeight.Bold else FontWeight.SemiBold,
                                        color = AgriTextPrimary
                                    )
                                    Text(
                                        text = st(item.timestamp),
                                        style = MaterialTheme.typography.bodySmall,
                                        fontSize = 10.sp,
                                        color = AgriTextSecondary
                                    )
                                }

                                Spacer(modifier = Modifier.height(2.dp))

                                Text(
                                    text = st(item.message),
                                    style = MaterialTheme.typography.bodySmall,
                                    fontSize = 11.sp,
                                    color = AgriTextSecondary,
                                    lineHeight = 15.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
