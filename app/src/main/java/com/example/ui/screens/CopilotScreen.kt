package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CardBase
import com.example.ui.theme.CockpitCanvas
import com.example.ui.theme.GhostBorder
import com.example.ui.theme.GhostBorderSubtle
import com.example.ui.theme.OutfitFontFamily
import com.example.ui.theme.PrimaryElectricViolet
import com.example.ui.theme.SecondaryTeal
import com.example.ui.theme.TertiaryIndigo
import com.example.viewmodel.AttendanceViewModel
import com.example.viewmodel.CopilotMessage

@Composable
fun CopilotScreen(
    viewModel: AttendanceViewModel,
    modifier: Modifier = Modifier
) {
    val messages by viewModel.copilotMessages.collectAsState()
    var inputQuery by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    val quickPrompts = listOf(
        "Can I skip Friday's lecture?",
        "Debarment Risk Recovery Plan",
        "Exam Eligibility Audit",
        "Medical Leave Impact (3 Days)"
    )

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CockpitCanvas)
    ) {
        // Copilot Top Cockpit Bar
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = CardBase,
            tonalElevation = 4.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(PrimaryElectricViolet.copy(alpha = 0.2f))
                            .border(1.dp, PrimaryElectricViolet, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = PrimaryElectricViolet,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Column {
                        Text(
                            text = "AI Academic Copilot",
                            fontFamily = OutfitFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Color.White
                        )
                        Text(
                            text = "Predictive compliance & strategy advisor",
                            style = MaterialTheme.typography.bodySmall,
                            color = SecondaryTeal,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        // Quick Suggestion Chips
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp, horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(quickPrompts) { prompt ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF1E293B))
                        .border(1.dp, PrimaryElectricViolet.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                        .clickable { viewModel.runCopilotScenario(prompt) }
                        .padding(horizontal = 12.dp, vertical = 7.dp)
                        .testTag("copilot_prompt_${prompt.take(10)}")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Psychology,
                            contentDescription = null,
                            tint = SecondaryTeal,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = prompt,
                            fontFamily = OutfitFontFamily,
                            fontWeight = FontWeight.Medium,
                            fontSize = 12.sp,
                            color = Color.White
                        )
                    }
                }
            }
        }

        // Messages Transcript
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(messages, key = { it.id }) { msg ->
                if (msg.isUser) {
                    UserMessageBubble(msg = msg)
                } else {
                    AiMessageBubble(msg = msg)
                }
            }
        }

        // Bottom Input Field
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 80.dp),
            color = CardBase
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = inputQuery,
                    onValueChange = { inputQuery = it },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("copilot_input_field"),
                    placeholder = {
                        Text(
                            text = "Ask e.g. 'Can I miss next 2 lab classes?'",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF94A3B8),
                            fontSize = 13.sp
                        )
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFF0F172A),
                        unfocusedContainerColor = Color(0xFF0F172A),
                        focusedBorderColor = PrimaryElectricViolet,
                        unfocusedBorderColor = GhostBorder,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    singleLine = true
                )

                IconButton(
                    onClick = {
                        if (inputQuery.isNotBlank()) {
                            val q = inputQuery.trim()
                            inputQuery = ""
                            viewModel.runCopilotScenario(q)
                        }
                    },
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(PrimaryElectricViolet, TertiaryIndigo)
                            )
                        )
                        .testTag("copilot_send_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Send",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun UserMessageBubble(msg: CopilotMessage) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.CenterEnd
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.82f)
                .clip(RoundedCornerShape(14.dp))
                .background(Color(0xFF281C4A)) // Indigo-tinted container as per design spec
                .border(1.dp, PrimaryElectricViolet.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                .padding(14.dp)
        ) {
            Text(
                text = msg.text,
                fontFamily = OutfitFontFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                color = Color.White
            )
        }
    }
}

@Composable
fun AiMessageBubble(msg: CopilotMessage) {
    // Frosted glass background rgba(30, 41, 59, 0.85) with dark-to-violet linear border stroke
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.CenterStart
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .clip(RoundedCornerShape(16.dp))
                .border(
                    1.dp,
                    Brush.linearGradient(
                        listOf(
                            PrimaryElectricViolet.copy(alpha = 0.6f),
                            TertiaryIndigo.copy(alpha = 0.3f),
                            Color(0x1AFFFFFF)
                        )
                    ),
                    RoundedCornerShape(16.dp)
                ),
            color = Color(0xDA1E293B) // Frosted dark surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.SmartToy,
                            contentDescription = null,
                            tint = SecondaryTeal,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "Copilot Advisor",
                            fontFamily = OutfitFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = SecondaryTeal
                        )
                    }

                    if (msg.metricHighlight != null) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(PrimaryElectricViolet.copy(alpha = 0.2f))
                                .border(1.dp, PrimaryElectricViolet.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = msg.metricHighlight,
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Text(
                    text = msg.text,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFFE2E8F0),
                    lineHeight = 21.sp
                )
            }
        }
    }
}
