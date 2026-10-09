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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AttendanceTier
import com.example.ui.components.AttendanceCircularGauge
import com.example.ui.components.ProfileAvatarButton
import com.example.ui.components.StatusCapsule
import com.example.ui.theme.CardBase
import com.example.ui.theme.CockpitCanvas
import com.example.ui.theme.GhostBorder
import com.example.ui.theme.GhostBorderSubtle
import com.example.ui.theme.OutfitFontFamily
import com.example.ui.theme.PrimaryElectricViolet
import com.example.ui.theme.SecondaryTeal
import com.example.ui.theme.TertiaryIndigo
import com.example.ui.theme.TierCriticalRose
import com.example.ui.theme.TierSafeEmerald
import com.example.viewmodel.AttendanceViewModel
import java.util.Locale

@Composable
fun SimulatorScreen(
    viewModel: AttendanceViewModel,
    modifier: Modifier = Modifier
) {
    val subjects by viewModel.subjects.collectAsState()
    val selectedSubId by viewModel.simulationSubjectId.collectAsState()
    val upcomingAttend by viewModel.simUpcomingAttend.collectAsState()
    val upcomingMiss by viewModel.simUpcomingMiss.collectAsState()
    val targetPct by viewModel.simTargetPercentage.collectAsState()

    val simulation = viewModel.calculateSimulation()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(CockpitCanvas)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoGraph,
                            contentDescription = null,
                            tint = SecondaryTeal,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "PREDICTIVE ENGINE",
                            style = MaterialTheme.typography.labelSmall,
                            color = SecondaryTeal,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "What-If Simulator",
                        fontFamily = OutfitFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        color = Color.White
                    )
                    Text(
                        text = "Simulate attendance outcomes before taking leaves or skipping lectures.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF94A3B8)
                    )
                }

                ProfileAvatarButton(
                    viewModel = viewModel,
                    modifier = Modifier.padding(start = 12.dp)
                )
            }
        }

        // Subject Selector Chips
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Select Course to Simulate",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    item {
                        SubjectSelectChip(
                            label = "Overall Aggregate",
                            isSelected = selectedSubId == null,
                            onClick = { viewModel.setSimulationSubject(null) },
                            tag = "sim_select_overall"
                        )
                    }

                    items(subjects, key = { it.id }) { sub ->
                        SubjectSelectChip(
                            label = sub.code,
                            isSelected = selectedSubId == sub.id,
                            onClick = { viewModel.setSimulationSubject(sub.id) },
                            tag = "sim_select_${sub.id}"
                        )
                    }
                }
            }
        }

        // Result Projection Card
        item {
            SimulationOutcomeCard(simulation = simulation)
        }

        // Stepper Controls
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, GhostBorder, RoundedCornerShape(16.dp)),
                color = CardBase
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Simulation Parameters",
                        fontFamily = OutfitFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color.White
                    )

                    // Upcoming Classes to Attend
                    StepperRow(
                        title = "Upcoming Classes to Attend",
                        subtitle = "Mark planned attendance streak",
                        value = upcomingAttend,
                        color = TierSafeEmerald,
                        onValueChange = { viewModel.setSimUpcomingAttend(it) },
                        testTagPrefix = "attend_stepper"
                    )

                    // Upcoming Classes to Miss / Bunk
                    StepperRow(
                        title = "Upcoming Classes to Miss",
                        subtitle = "Simulate planned leaves / bunks",
                        value = upcomingMiss,
                        color = TierCriticalRose,
                        onValueChange = { viewModel.setSimUpcomingMiss(it) },
                        testTagPrefix = "miss_stepper"
                    )

                    // Target Threshold Selector
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Required Target Benchmark",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White
                            )
                            Text(
                                text = "${targetPct.toInt()}%",
                                fontFamily = OutfitFontFamily,
                                fontWeight = FontWeight.Bold,
                                color = SecondaryTeal
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf(70.0, 75.0, 80.0, 85.0).forEach { target ->
                                val isSelected = targetPct == target
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) PrimaryElectricViolet.copy(alpha = 0.3f) else Color(0xFF0F172A))
                                        .border(1.dp, if (isSelected) PrimaryElectricViolet else Color(0x33FFFFFF), RoundedCornerShape(8.dp))
                                        .clickable { viewModel.setSimTargetPercentage(target) }
                                        .padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "${target.toInt()}%",
                                        fontFamily = OutfitFontFamily,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) Color.White else Color(0xFF94A3B8),
                                        fontSize = 13.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Fast Preset Scenarios
        item {
            Text(
                text = "Quick Tactical Presets",
                fontFamily = OutfitFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color.White
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                PresetCard(
                    title = "Skip Next 2",
                    desc = "Test 2-class leave impact",
                    modifier = Modifier.weight(1f),
                    onClick = {
                        viewModel.setSimUpcomingAttend(0)
                        viewModel.setSimUpcomingMiss(2)
                    }
                )

                PresetCard(
                    title = "Recovery Streak",
                    desc = "Attend next 8 consecutively",
                    modifier = Modifier.weight(1f),
                    onClick = {
                        viewModel.setSimUpcomingAttend(8)
                        viewModel.setSimUpcomingMiss(0)
                    }
                )

                PresetCard(
                    title = "Realistic Week",
                    desc = "Attend 4, miss 1",
                    modifier = Modifier.weight(1f),
                    onClick = {
                        viewModel.setSimUpcomingAttend(4)
                        viewModel.setSimUpcomingMiss(1)
                    }
                )
            }
        }
    }
}

@Composable
fun SimulationOutcomeCard(
    simulation: com.example.model.SimulationResult,
    modifier: Modifier = Modifier
) {
    val tier = if (simulation.meetsTarget) AttendanceTier.SAFE else AttendanceTier.CRITICAL
    val deltaColor = if (simulation.deltaPercentage >= 0) TierSafeEmerald else TierCriticalRose
    val deltaSign = if (simulation.deltaPercentage >= 0) "+" else ""

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(
                1.dp,
                Brush.linearGradient(
                    listOf(
                        tier.textColor.copy(alpha = 0.5f),
                        TertiaryIndigo.copy(alpha = 0.3f),
                        Color(0x1AFFFFFF)
                    )
                ),
                RoundedCornerShape(16.dp)
            )
            .testTag("simulation_outcome_card"),
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
                Text(
                    text = "PROJECTED OUTCOME",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF94A3B8),
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )

                StatusCapsule(
                    tier = tier,
                    customText = if (simulation.meetsTarget) "Compliant" else "Debarment Risk"
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                AttendanceCircularGauge(
                    percentage = simulation.projectedPercentage,
                    tier = tier,
                    size = 94.dp,
                    strokeWidth = 9.dp
                )

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = String.format(Locale.US, "%.1f%%", simulation.projectedPercentage),
                            fontFamily = OutfitFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp,
                            color = Color.White
                        )

                        // Delta badge
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(deltaColor.copy(alpha = 0.15f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = String.format(Locale.US, "%s%.1f%%", deltaSign, simulation.deltaPercentage),
                                fontFamily = OutfitFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = deltaColor
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "From ${String.format(Locale.US, "%.1f%%", simulation.currentPercentage)} (Now ${simulation.attendedAfter}/${simulation.totalAfter} classes)",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF94A3B8)
                    )

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Benchmark: ${simulation.targetPercentage.toInt()}%",
                        style = MaterialTheme.typography.labelSmall,
                        color = SecondaryTeal
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Tactical Advice Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(tier.backgroundColor.copy(alpha = 0.1f))
                    .border(1.dp, tier.borderColor.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                    .padding(12.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = simulation.statusSummary,
                        fontFamily = OutfitFontFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp,
                        color = tier.textColor
                    )
                    Text(
                        text = simulation.tacticalAdvice,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFCBD5E1),
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@Composable
fun StepperRow(
    title: String,
    subtitle: String,
    value: Int,
    color: Color,
    onValueChange: (Int) -> Unit,
    testTagPrefix: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF94A3B8)
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            IconButton(
                onClick = { if (value > 0) onValueChange(value - 1) },
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF0F172A))
                    .border(1.dp, GhostBorder, CircleShape)
                    .testTag("${testTagPrefix}_decrement")
            ) {
                Icon(
                    imageVector = Icons.Default.Remove,
                    contentDescription = "Decrease",
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }

            Text(
                text = "$value",
                fontFamily = OutfitFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = color,
                modifier = Modifier.width(28.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            IconButton(
                onClick = { onValueChange(value + 1) },
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF0F172A))
                    .border(1.dp, GhostBorder, CircleShape)
                    .testTag("${testTagPrefix}_increment")
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Increase",
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
fun SubjectSelectChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    tag: String
) {
    val bg = if (isSelected) PrimaryElectricViolet.copy(alpha = 0.25f) else CardBase
    val border = if (isSelected) PrimaryElectricViolet else GhostBorder

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bg)
            .border(1.dp, border, RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 8.dp)
            .testTag(tag),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontFamily = OutfitFontFamily,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            fontSize = 13.sp,
            color = if (isSelected) Color.White else Color(0xFF94A3B8)
        )
    }
}

@Composable
fun PresetCard(
    title: String,
    desc: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(CardBase)
            .border(1.dp, GhostBorderSubtle, RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
            .padding(10.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = title,
                fontFamily = OutfitFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp,
                color = Color.White
            )
            Text(
                text = desc,
                style = MaterialTheme.typography.bodySmall,
                fontSize = 10.sp,
                color = Color(0xFF94A3B8)
            )
        }
    }
}
