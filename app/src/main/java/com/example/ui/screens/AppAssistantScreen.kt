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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.rememberCoroutineScope
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
import com.example.BuildConfig
import com.example.model.AttendanceTier
import com.example.model.Subject
import com.example.ui.components.ProfileAvatarButton
import com.example.ui.theme.CardBase
import com.example.ui.theme.CockpitCanvas
import com.example.ui.theme.GhostBorder
import com.example.ui.theme.OutfitFontFamily
import com.example.ui.theme.PrimaryElectricViolet
import com.example.ui.theme.SecondaryTeal
import com.example.ui.theme.TertiaryIndigo
import com.example.ui.theme.TierCriticalRose
import com.example.ui.theme.TierSafeEmerald
import com.example.viewmodel.AttendanceViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.Locale
import java.util.concurrent.TimeUnit

data class AssistantChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val isUser: Boolean,
    val text: String,
    val categoryBadge: String? = null
)

/**
 * In-app AI Assistant & Feature Guide.
 * Explains everything in the app:
 * - What features and sections are available
 * - How the What-If simulator and formulas work
 * - How to customize subjects and timetable slots
 * - Real-time audit of the user's actual courses and compliance status
 */
@Composable
fun AppAssistantScreen(
    viewModel: AttendanceViewModel,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val subjects by viewModel.subjects.collectAsState()
    val overallStats by viewModel.overallStats.collectAsState()

    var inputPrompt by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    var messages by remember {
        mutableStateOf(
            listOf(
                AssistantChatMessage(
                    isUser = false,
                    text = "Hello! I am your Smart Attendance AI Assistant.\n\n" +
                            "I can guide you through every feature in this app, explain how the calculations work, " +
                            "show you how to change your timetable & subjects, or analyze your current attendance standing.\n\n" +
                            "Select a topic below or ask me anything about the app!",
                    categoryBadge = "App Guide"
                )
            )
        )
    }

    val quickHelpTopics = listOf(
        "What is in this app?",
        "How to change Timetable & Subjects?",
        "How does What-If Simulator work?",
        "Explain Attendance Formulas",
        "Audit my current courses"
    )

    val listState = rememberLazyListState()
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    fun handleQuery(query: String) {
        if (query.isBlank() || isLoading) return
        val userMsg = AssistantChatMessage(isUser = true, text = query)
        messages = messages + userMsg
        isLoading = true

        coroutineScope.launch {
            val response = generateAssistantResponse(query, subjects, overallStats)
            isLoading = false
            messages = messages + response
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CockpitCanvas)
    ) {
        // Cockpit Header
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
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
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
                                imageVector = Icons.Default.SmartToy,
                                contentDescription = null,
                                tint = PrimaryElectricViolet,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = "App Guide & AI Assistant",
                                    fontFamily = OutfitFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 17.sp,
                                    color = Color.White
                                )
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(SecondaryTeal.copy(alpha = 0.2f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "Active",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = SecondaryTeal,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            Text(
                                text = "Learn how to use features & master attendance goals",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF94A3B8),
                                fontSize = 11.sp
                            )
                        }
                    }

                    ProfileAvatarButton(
                        viewModel = viewModel,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
        }

        // Quick Help Topics Chips
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp, horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(quickHelpTopics) { topic ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(CardBase)
                        .border(1.dp, PrimaryElectricViolet.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                        .clickable { handleQuery(topic) }
                        .padding(horizontal = 12.dp, vertical = 7.dp)
                        .testTag("help_chip_${topic.take(8)}")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.HelpOutline,
                            contentDescription = null,
                            tint = SecondaryTeal,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = topic,
                            fontFamily = OutfitFontFamily,
                            fontWeight = FontWeight.Medium,
                            fontSize = 12.sp,
                            color = Color.White
                        )
                    }
                }
            }
        }

        // Chat Conversation Transcript
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
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.82f)
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color(0xFF281C4A))
                                .border(1.dp, PrimaryElectricViolet.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
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
                } else {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth(0.95f)
                            .clip(RoundedCornerShape(16.dp))
                            .border(
                                1.dp,
                                Brush.linearGradient(
                                    listOf(PrimaryElectricViolet.copy(alpha = 0.5f), TertiaryIndigo.copy(alpha = 0.3f))
                                ),
                                RoundedCornerShape(16.dp)
                            ),
                        color = Color(0xEB1E293B)
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
                                        imageVector = Icons.Default.AutoAwesome,
                                        contentDescription = null,
                                        tint = SecondaryTeal,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = "AI Guide",
                                        fontFamily = OutfitFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = SecondaryTeal
                                    )
                                }

                                if (msg.categoryBadge != null) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(PrimaryElectricViolet.copy(alpha = 0.2f))
                                            .padding(horizontal = 8.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = msg.categoryBadge,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = PrimaryElectricViolet,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 10.sp
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

            if (isLoading) {
                item {
                    Row(
                        modifier = Modifier.padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            color = PrimaryElectricViolet,
                            strokeWidth = 2.dp
                        )
                        Text(
                            text = "Analyzing app features and attendance records...",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF94A3B8)
                        )
                    }
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
                    value = inputPrompt,
                    onValueChange = { inputPrompt = it },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("assistant_input_field"),
                    placeholder = {
                        Text(
                            text = "Ask e.g. 'How do I add a new subject?'...",
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
                        if (inputPrompt.isNotBlank() && !isLoading) {
                            val prompt = inputPrompt.trim()
                            inputPrompt = ""
                            handleQuery(prompt)
                        }
                    },
                    enabled = !isLoading,
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(PrimaryElectricViolet)
                        .testTag("assistant_send_button")
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

/**
 * Generates an intelligent, comprehensive explanation of the app features,
 * with deterministic offline fallback and live Gemini API expansion if available.
 */
private suspend fun generateAssistantResponse(
    query: String,
    subjects: List<Subject>,
    overallStats: com.example.viewmodel.OverallStats
): AssistantChatMessage = withContext(Dispatchers.IO) {
    val q = query.lowercase(Locale.US)

    // Check if query matches common app guide topics for instant, rich answers
    val (badge, localAnswer) = when {
        q.contains("what in that app") || q.contains("what is in this app") || q.contains("overview") || q.contains("features") -> {
            "App Tour" to """
                Here is a complete breakdown of what is inside the **Smart Attendance Predictor**:

                1. 📊 **Overview / Dashboard Tab:**
                   • Displays your aggregate semester attendance KPI ring gauge.
                   • Class ratios (e.g. 142/174 classes attended).
                   • Safe bunk allowance calculation (how many classes you can skip across all courses without dropping below 75%).
                   • Interactive Subject Cards with quick **+ Present**, **+ Absent**, and **Simulate** buttons.
                   • Undo button to revert any accidental attendance tap.

                2. 📈 **What-If Simulator Tab:**
                   • Interactive prediction sandbox!
                   • Adjust sliders for upcoming classes to attend vs. classes to miss.
                   • Calculates live projected percentage, delta change (+/-%), and safe margin or debarment hazard before you take leaves.

                3. 📅 **Timetable / Schedule Tab:**
                   • Monday to Saturday lecture schedule with timings and hall numbers.
                   • **Change Timetable**: Tap **+ Add Lecture** to add slots, or tap the **Edit** icon on any lecture card to modify timing or delete it.
                   • Log attendance in 1 tap directly from the schedule (Present, Absent, or Cancelled/No Class).

                4. 📚 **Manage Subjects / Courses:**
                   • Tap the **+ FAB** on Dashboard or the **Edit icon** on any subject card to edit course name, code, instructor, credits, target threshold %, and attendance numbers.

                5. 🤖 **AI Assistant & Guide:**
                   • Explains all app features, audits your current course records, and recommends recovery plans!
            """.trimIndent()
        }

        q.contains("timetable") || q.contains("subject") || q.contains("change") || q.contains("edit") || q.contains("add") -> {
            "Management Guide" to """
                Here is how to change and customize your Timetable and Subjects:

                📌 **How to Change the Timetable:**
                1. Open the **Timetable** tab from the bottom navigation.
                2. Select the weekday (Mon, Tue, Wed, Thu, Fri, Sat).
                3. Tap the **+ Add Lecture** button in the header to create a new lecture period.
                4. To modify or remove an existing lecture, tap the **Edit (pencil)** icon on any lecture card. You can change start/end times, room location, or tap **Delete This Slot**.

                📚 **How to Change Subjects / Courses:**
                1. On the **Dashboard**, tap the violet **+ FAB** at the bottom-right to create a new subject.
                2. To edit an existing subject, tap on the subject card to open its detail view, then tap the **Edit icon** in the top-right.
                3. You can edit the course title, code, instructor, attended classes, total classes, and target percentage (75%, 80%, or 85%).
                4. You can also delete any subject by tapping the red **Delete icon** in the course detail view.
            """.trimIndent()
        }

        q.contains("what-if") || q.contains("simulator") || q.contains("predict") -> {
            "Simulator Guide" to """
                🎯 **What-If Attendance Simulator Explained:**

                The What-If Simulator eliminates guesswork before you take leaves:

                • **Course Selection:** Choose either an individual course or your overall semester aggregate.
                • **Upcoming Classes to Attend:** Use the steppers to test a streak of attended classes.
                • **Upcoming Classes to Miss:** Test what happens if you take personal leaves or skip lectures.
                • **Target Benchmark:** Set your requirement (70%, 75%, 80%, 85%).
                • **Real-Time Projection:**
                  - Shows your resulting percentage instantly with a delta badge (e.g. +3.2% or -4.5%).
                  - Tells you the exact buffer margin or consecutive classes needed to return to the safe zone.
                • **Quick Presets:** One-tap scenarios like "Skip Next 2", "Recovery Streak (+8)", and "Realistic Week".
            """.trimIndent()
        }

        q.contains("formula") || q.contains("calculate") || q.contains("safe bunk") || q.contains("tier") || q.contains("75") -> {
            "Math & Tiers" to """
                📐 **Mathematical Calculation Rules:**

                1. **Attendance Ratio:**
                   Percentage = (Attended Classes / Total Classes) * 100

                2. **Safe Bunk Allowance (m):**
                   How many classes you can skip while staying >= 75%:
                   m = floor((Attended / 0.75) - Total)

                3. **Consecutive Classes Required (c):**
                   How many classes you must attend to escape debarment:
                   c = ceil((0.75 * Total - Attended) / (1 - 0.75))

                4. **Semantic Status Tiers:**
                   • 🟢 **Safe Zone (>=75%):** Compliant, meets exam hall ticket criteria.
                   • 🟡 **Near Limit (70% - 74%):** High caution, at risk of debarment.
                   • 🔴 **Debarment Risk (<70%):** Critical shortage, exam hall ticket blocked.
            """.trimIndent()
        }

        q.contains("audit") || q.contains("my") || q.contains("standing") || q.contains("standing") || q.contains("current") -> {
            val criticals = subjects.filter { it.tier == AttendanceTier.CRITICAL }
            val cautions = subjects.filter { it.tier == AttendanceTier.CAUTION }
            val safes = subjects.filter { it.tier == AttendanceTier.SAFE }

            val sb = java.lang.StringBuilder()
            sb.append("📋 **Your Current Academic Standing Audit:**\n\n")
            sb.append("• **Aggregate Attendance:** ${String.format(Locale.US, "%.1f%%", overallStats.percentage)} (${overallStats.totalAttended}/${overallStats.totalHeld} classes attended)\n")
            sb.append("• **Safe Buffer:** You can safely miss up to ${overallStats.canBunkTotal} classes across subjects without dropping below 75%.\n\n")

            if (criticals.isNotEmpty()) {
                sb.append("🚨 **CRITICAL DEBARMENT RISKS:**\n")
                criticals.forEach {
                    sb.append("• ${it.code} ${it.name}: ${String.format(Locale.US, "%.1f%%", it.percentage)} ➔ Must attend next **${it.mustAttendClasses} consecutive classes**!\n")
                }
                sb.append("\n")
            }

            if (cautions.isNotEmpty()) {
                sb.append("⚠️ **NEAR LIMIT COURSES:**\n")
                cautions.forEach {
                    sb.append("• ${it.code} ${it.name}: ${String.format(Locale.US, "%.1f%%", it.percentage)} ➔ Attend next **${it.mustAttendClasses} class(es)** to reach safe zone.\n")
                }
                sb.append("\n")
            }

            if (safes.isNotEmpty()) {
                sb.append("✅ **SAFE COURSES:**\n")
                safes.forEach {
                    val m = it.canMissClasses
                    sb.append("• ${it.code} ${it.name}: ${String.format(Locale.US, "%.1f%%", it.percentage)} (Can miss $m class${if (m != 1) "es" else ""} safely)\n")
                }
            }

            "Attendance Audit" to sb.toString()
        }

        else -> null to null
    }

    if (localAnswer != null) {
        return@withContext AssistantChatMessage(
            isUser = false,
            text = localAnswer,
            categoryBadge = badge
        )
    }

    // Call Gemini 3.5 Flash for dynamic questions
    val apiKey = BuildConfig.GEMINI_API_KEY
    if (apiKey.isBlank()) {
        return@withContext AssistantChatMessage(
            isUser = false,
            text = "I am your Smart Attendance Predictor AI Assistant. You can ask me how to use any screen, " +
                    "how the What-If simulator works, how to edit timetable slots or subjects, or ask for an audit of your attendance.",
            categoryBadge = "Assistant"
        )
    }

    try {
        val client = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()

        val systemPrompt = "You are the AI Assistant inside the 'Smart Attendance Predictor' Android application. " +
                "You guide university students on how to use the app, including: " +
                "1) Dashboard with attendance KPI rings and quick logging. " +
                "2) What-If Simulator with attendance steppers and projected compliance. " +
                "3) Timetable schedule where users can log Present/Absent/Cancelled and edit lecture slots. " +
                "4) Subject management (add/edit/delete courses, target percentages). " +
                "Current student status: Aggregate ${String.format(Locale.US, "%.1f%%", overallStats.percentage)} " +
                "(${overallStats.totalAttended}/${overallStats.totalHeld} classes). " +
                "Provide clear, encouraging, formatted answers."

        val requestJson = JSONObject().apply {
            val contents = JSONArray().apply {
                put(JSONObject().apply {
                    val parts = JSONArray().apply {
                        put(JSONObject().apply {
                            put("text", query)
                        })
                    }
                    put("parts", parts)
                })
            }
            put("contents", contents)
            put("systemInstruction", JSONObject().apply {
                val parts = JSONArray().apply {
                    put(JSONObject().apply { put("text", systemPrompt) })
                }
                put("parts", parts)
            })
        }

        val request = Request.Builder()
            .url("https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey")
            .post(requestJson.toString().toRequestBody("application/json".toMediaType()))
            .build()

        val response = client.newCall(request).execute()
        val bodyStr = response.body?.string() ?: ""

        val json = JSONObject(bodyStr)
        val candidate = json.optJSONArray("candidates")?.optJSONObject(0)
        val parts = candidate?.optJSONObject("content")?.optJSONArray("parts")
        val text = parts?.optJSONObject(0)?.optString("text") ?: "No response generated."

        AssistantChatMessage(isUser = false, text = text, categoryBadge = "AI Copilot")
    } catch (e: Exception) {
        AssistantChatMessage(
            isUser = false,
            text = "Here to help! In this app you can track attendance, run What-If simulations, edit timetable slots, and manage your courses. Tap the chips above for guided tours.",
            categoryBadge = "Guide"
        )
    }
}
