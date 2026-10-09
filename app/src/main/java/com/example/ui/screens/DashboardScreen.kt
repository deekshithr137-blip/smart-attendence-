package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AttendanceTier
import com.example.model.Subject
import com.example.ui.components.AttendanceCircularGauge
import com.example.ui.components.StatusCapsule
import com.example.ui.components.SubjectCard
import com.example.ui.theme.CardBase
import com.example.ui.theme.CockpitCanvas
import com.example.ui.theme.GhostBorder
import com.example.ui.theme.GhostBorderSubtle
import com.example.ui.theme.OutfitFontFamily
import com.example.ui.theme.PrimaryElectricViolet
import com.example.ui.theme.SecondaryTeal
import com.example.ui.theme.SurfaceCanvas
import com.example.ui.theme.SurfaceContainer
import com.example.ui.theme.TertiaryIndigo
import com.example.ui.theme.TierCautionAmber
import com.example.ui.theme.TierCriticalRose
import com.example.ui.theme.TierSafeEmerald
import com.example.viewmodel.AttendanceViewModel
import java.util.Locale

@Composable
fun DashboardScreen(
    viewModel: AttendanceViewModel,
    onNavigateToSimulator: (Long?) -> Unit,
    onSelectSubject: (Subject) -> Unit,
    modifier: Modifier = Modifier
) {
    val subjects by viewModel.subjects.collectAsState()
    val overallStats by viewModel.overallStats.collectAsState()
    val selectedTierFilter by viewModel.selectedTierFilter.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val recentLogs by viewModel.recentLogs.collectAsState()

    val filteredSubjects = subjects.filter { subject ->
        val matchesTier = selectedTierFilter == null || subject.tier == selectedTierFilter
        val matchesSearch = searchQuery.isBlank() ||
                subject.name.contains(searchQuery, ignoreCase = true) ||
                subject.code.contains(searchQuery, ignoreCase = true)
        matchesTier && matchesSearch
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(CockpitCanvas)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // App Branding & Cockpit Title
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(PrimaryElectricViolet)
                            )
                            Text(
                                text = "ACADEMIC COMMAND CENTER",
                                style = MaterialTheme.typography.labelSmall,
                                color = PrimaryElectricViolet,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Attendance Predictor",
                            fontFamily = OutfitFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp,
                            color = Color.White
                        )
                    }

                    val userProfile by viewModel.userProfile.collectAsState()

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (recentLogs.isNotEmpty()) {
                            IconButton(
                                onClick = { viewModel.undoLastLog() },
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(CardBase)
                                    .border(1.dp, GhostBorder, RoundedCornerShape(8.dp))
                                    .testTag("undo_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Undo,
                                    contentDescription = "Undo Last Action",
                                    tint = SecondaryTeal,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        // Student Profile at Right Corner
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(
                                        listOf(PrimaryElectricViolet, TertiaryIndigo)
                                    )
                                )
                                .border(1.5.dp, Color.White.copy(alpha = 0.35f), CircleShape)
                                .clickable { viewModel.openProfileDialog() }
                                .testTag("profile_avatar_top_right"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = userProfile.initials,
                                fontFamily = OutfitFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = Color.White
                            )
                        }
                    }
                }
            }

            // Overall Aggregate KPI Card
            item {
                OverallKpiCard(
                    overallStats = overallStats,
                    onSimulateOverall = { onNavigateToSimulator(null) }
                )
            }

            // Strategic Prediction Insight Card
            item {
                StrategicPredictionBanner(
                    overallStats = overallStats,
                    subjects = subjects,
                    onOpenSimulator = { onNavigateToSimulator(null) }
                )
            }

            // Search bar
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.setSearchQuery(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("search_subjects_input"),
                    placeholder = {
                        Text(
                            text = "Search by course code or title...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF94A3B8)
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = Color(0xFF94A3B8)
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Clear",
                                    tint = Color(0xFF94A3B8)
                                )
                            }
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = CardBase,
                        unfocusedContainerColor = CardBase,
                        focusedBorderColor = PrimaryElectricViolet,
                        unfocusedBorderColor = GhostBorder,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    singleLine = true
                )
            }

            // Filter Chips Row
            item {
                FilterTierChips(
                    selectedTier = selectedTierFilter,
                    totalCount = subjects.size,
                    safeCount = overallStats.safeCount,
                    cautionCount = overallStats.cautionCount,
                    criticalCount = overallStats.criticalCount,
                    onSelectTier = { viewModel.setTierFilter(it) }
                )
            }

            // Subject Cards Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Tracked Courses (${filteredSubjects.size})",
                        fontFamily = OutfitFontFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp,
                        color = Color.White
                    )
                    Text(
                        text = "Benchmark: 75%",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF94A3B8)
                    )
                }
            }

            // Subject Cards List
            if (filteredSubjects.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = null,
                                tint = Color(0xFF64748B),
                                modifier = Modifier.size(40.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "No subjects match your criteria",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFF94A3B8)
                            )
                        }
                    }
                }
            } else {
                items(filteredSubjects, key = { it.id }) { subject ->
                    SubjectCard(
                        subject = subject,
                        onMarkPresent = { viewModel.markPresent(subject.id) },
                        onMarkAbsent = { viewModel.markAbsent(subject.id) },
                        onSimulate = { onNavigateToSimulator(subject.id) },
                        onClick = { onSelectSubject(subject) },
                        onEdit = { viewModel.openEditSubjectDialog(subject) }
                    )
                }
            }
        }

        // Floating Action Button to Add Subject
        FloatingActionButton(
            onClick = { viewModel.openAddSubjectDialog() },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 80.dp)
                .testTag("add_subject_fab"),
            containerColor = PrimaryElectricViolet,
            contentColor = Color.White,
            shape = RoundedCornerShape(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add Subject",
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun OverallKpiCard(
    overallStats: com.example.viewmodel.OverallStats,
    onSimulateOverall: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(
                1.dp,
                Brush.linearGradient(
                    listOf(
                        PrimaryElectricViolet.copy(alpha = 0.4f),
                        TertiaryIndigo.copy(alpha = 0.2f),
                        Color(0x1AFFFFFF)
                    )
                ),
                RoundedCornerShape(16.dp)
            )
            .testTag("overall_kpi_card"),
        color = CardBase,
        tonalElevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "OVERALL COMPLIANCE",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF94A3B8),
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Semester Aggregate",
                        fontFamily = OutfitFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color.White
                    )
                }

                StatusCapsule(tier = overallStats.tier)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                AttendanceCircularGauge(
                    percentage = overallStats.percentage,
                    tier = overallStats.tier,
                    size = 94.dp,
                    strokeWidth = 9.dp
                )

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Total Attended:",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF94A3B8)
                        )
                        Text(
                            text = "${overallStats.totalAttended} / ${overallStats.totalHeld} Classes",
                            fontFamily = OutfitFontFamily,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White,
                            fontSize = 13.sp
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Course Breakdown:",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF94A3B8)
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = "${overallStats.safeCount} Safe",
                                color = TierSafeEmerald,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(text = "•", color = Color(0xFF64748B))
                            Text(
                                text = "${overallStats.cautionCount} Caution",
                                color = TierCautionAmber,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(text = "•", color = Color(0xFF64748B))
                            Text(
                                text = "${overallStats.criticalCount} Risk",
                                color = TierCriticalRose,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Tactical Safe Margin
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF0F172A))
                            .border(1.dp, GhostBorderSubtle, RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoGraph,
                                contentDescription = null,
                                tint = SecondaryTeal,
                                modifier = Modifier.size(14.dp)
                            )
                            val bufferText = if (overallStats.percentage >= 75.0) {
                                "Can miss ~${overallStats.canBunkTotal} classes safely across subjects"
                            } else {
                                "Must attend ~${overallStats.mustAttendTotal} consecutive classes"
                            }
                            Text(
                                text = bufferText,
                                style = MaterialTheme.typography.bodySmall,
                                color = SecondaryTeal,
                                fontSize = 11.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StrategicPredictionBanner(
    overallStats: com.example.viewmodel.OverallStats,
    subjects: List<Subject>,
    onOpenSimulator: () -> Unit,
    modifier: Modifier = Modifier
) {
    val criticalSubjects = subjects.filter { it.tier == AttendanceTier.CRITICAL }
    val cautionSubjects = subjects.filter { it.tier == AttendanceTier.CAUTION }

    val hasRisk = criticalSubjects.isNotEmpty() || cautionSubjects.isNotEmpty()

    val bgGradient = if (hasRisk) {
        Brush.linearGradient(listOf(Color(0xFF2A1526), Color(0xFF1E293B)))
    } else {
        Brush.linearGradient(listOf(Color(0xFF142C2A), Color(0xFF1E293B)))
    }

    val borderColor = if (hasRisk) TierCriticalRose.copy(alpha = 0.35f) else TierSafeEmerald.copy(alpha = 0.35f)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(bgGradient)
            .border(1.dp, borderColor, RoundedCornerShape(14.dp))
            .clickable(onClick = onOpenSimulator)
            .padding(14.dp)
            .testTag("strategic_prediction_banner")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(if (hasRisk) TierCriticalRose.copy(alpha = 0.15f) else TierSafeEmerald.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (hasRisk) Icons.Default.Warning else Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = if (hasRisk) TierCriticalRose else TierSafeEmerald,
                    modifier = Modifier.size(22.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (hasRisk) "Attention Required in ${criticalSubjects.size + cautionSubjects.size} Course(s)" else "All Courses Safe & Compliant",
                    fontFamily = OutfitFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(2.dp))
                val desc = if (hasRisk) {
                    val first = criticalSubjects.firstOrNull() ?: cautionSubjects.first()
                    "${first.name} is at ${String.format(Locale.US, "%.1f%%", first.percentage)}. Tap to simulate recovery."
                } else {
                    "You have comfortable attendance buffers for all upcoming midterms. Tap to test what-if scenarios."
                }
                Text(
                    text = desc,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFFCBD5E1),
                    fontSize = 12.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Icon(
                imageVector = Icons.Default.AutoGraph,
                contentDescription = "Simulate",
                tint = SecondaryTeal,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun FilterTierChips(
    selectedTier: AttendanceTier?,
    totalCount: Int,
    safeCount: Int,
    cautionCount: Int,
    criticalCount: Int,
    onSelectTier: (AttendanceTier?) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            FilterChipItem(
                label = "All ($totalCount)",
                isSelected = selectedTier == null,
                color = PrimaryElectricViolet,
                onClick = { onSelectTier(null) },
                tag = "filter_chip_all"
            )
        }
        item {
            FilterChipItem(
                label = "Safe ($safeCount)",
                isSelected = selectedTier == AttendanceTier.SAFE,
                color = TierSafeEmerald,
                onClick = { onSelectTier(AttendanceTier.SAFE) },
                tag = "filter_chip_safe"
            )
        }
        item {
            FilterChipItem(
                label = "Near Limit ($cautionCount)",
                isSelected = selectedTier == AttendanceTier.CAUTION,
                color = TierCautionAmber,
                onClick = { onSelectTier(AttendanceTier.CAUTION) },
                tag = "filter_chip_caution"
            )
        }
        item {
            FilterChipItem(
                label = "Critical ($criticalCount)",
                isSelected = selectedTier == AttendanceTier.CRITICAL,
                color = TierCriticalRose,
                onClick = { onSelectTier(AttendanceTier.CRITICAL) },
                tag = "filter_chip_critical"
            )
        }
    }
}

@Composable
fun FilterChipItem(
    label: String,
    isSelected: Boolean,
    color: Color,
    onClick: () -> Unit,
    tag: String
) {
    val bg = if (isSelected) color.copy(alpha = 0.2f) else CardBase
    val border = if (isSelected) color else GhostBorder

    Box(
        modifier = Modifier
            .clip(CircleShape)
            .background(bg)
            .border(1.dp, border, CircleShape)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 6.dp)
            .testTag(tag),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontFamily = OutfitFontFamily,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            fontSize = 12.sp,
            color = if (isSelected) Color.White else Color(0xFF94A3B8)
        )
    }
}
