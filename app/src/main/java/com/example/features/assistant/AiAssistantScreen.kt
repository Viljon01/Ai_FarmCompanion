package com.example.features.assistant

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
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
import com.example.data.AssistantMessage
import com.example.data.AssistantSender
import com.example.data.localization.st
import com.example.data.localization.t
import com.example.data.CropContext
import com.example.ui.components.LeafAiSparkIcon
import com.example.ui.components.PageHeader
import com.example.ui.theme.*

import com.example.data.localization.*

/**
 * FEATURE 14 — DEDICATED AI FARM COMPANION
 * Context-aware agricultural assistant powered by Gemini API,
 * grounded in real telemetry (Coimbatore, 2 Acres, Day 35, 78% Humidity).
 */
@Composable
fun AiAssistantScreen(
    cropContext: CropContext,
    messages: List<AssistantMessage>,
    isLoading: Boolean,
    onSendMessage: (String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        PageHeader(
            title = t("assistant.title"),
            subtitle = t("assistant.subtitle"),
            onBack = onBack
        )

        // Telemetry Context Bar
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = AgriSageLight,
            border = androidx.compose.foundation.BorderStroke(1.dp, AgriForestGreen.copy(alpha = 0.3f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                LeafAiSparkIcon(size = 18.dp)
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "${st("Field Context")}: ${st(cropContext.cropCycle.cropName)} • ${t("common.day")} ${cropContext.cropCycle.cropAgeDays} • ${cropContext.farm.location.substringBefore(",")} • ${st("Hum")}: ${cropContext.currentWeather.humidityPercent}%",
                    style = MaterialTheme.typography.bodySmall,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = AgriForestGreen
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Quick Suggestions
        val quickPrompts = listOf(
            "Why did my health score change?",
            "Should I spray today given 78% humidity?",
            "Which market gives best net profit?",
            "What should I do at this stage?"
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            quickPrompts.take(2).forEach { prompt ->
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = AgriWarmBg,
                    border = androidx.compose.foundation.BorderStroke(1.dp, AgriLineSubtle),
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onSendMessage(prompt) }
                ) {
                    Text(
                        text = st(prompt),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        color = AgriForestGreen,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                        maxLines = 2
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Chat Message Stream
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(messages) { msg ->
                val isUser = msg.sender == AssistantSender.USER
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
                ) {
                    if (!isUser) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(AgriSageLight)
                                .border(1.dp, AgriForestGreen.copy(alpha = 0.3f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            LeafAiSparkIcon(size = 16.dp)
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                    }

                    Surface(
                        shape = RoundedCornerShape(
                            topStart = 14.dp,
                            topEnd = 14.dp,
                            bottomStart = if (isUser) 14.dp else 2.dp,
                            bottomEnd = if (isUser) 2.dp else 14.dp
                        ),
                        color = if (isUser) AgriForestGreen else AgriCardSurface,
                        border = if (!isUser) androidx.compose.foundation.BorderStroke(1.dp, AgriLineSubtle) else null,
                        modifier = Modifier.widthIn(max = 300.dp)
                    ) {
                        Text(
                            text = st(msg.text),
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 12.sp,
                            color = if (isUser) Color.White else AgriTextPrimary,
                            lineHeight = 17.sp,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
            }

            if (isLoading) {
                item {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        LeafAiSparkIcon(size = 18.dp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Analyzing field conditions & agronomy data...",
                            fontSize = 11.sp,
                            color = AgriTextSecondary,
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Input Box
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = inputText,
                onValueChange = { inputText = it },
                placeholder = { Text(st("Ask about crops, pests, weather or mandi..."), fontSize = 12.sp) },
                shape = RoundedCornerShape(20.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AgriForestGreen,
                    unfocusedBorderColor = AgriLineSubtle
                ),
                modifier = Modifier
                    .weight(1f)
                    .testTag("assistant_input_field")
            )

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = {
                    if (inputText.isNotBlank()) {
                        onSendMessage(inputText)
                        inputText = ""
                    }
                },
                enabled = inputText.isNotBlank() && !isLoading,
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(if (inputText.isNotBlank()) AgriForestGreen else AgriLineSubtle)
                    .testTag("assistant_send_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Send message",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
