package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AttendanceLog
import com.example.model.Subject
import com.example.ui.components.AttendanceCircularGauge
import com.example.ui.components.StatusCapsule
import com.example.ui.theme.CardBase
import com.example.ui.theme.CockpitCanvas
import com.example.ui.theme.GhostBorder
import com.example.ui.theme.GhostBorderSubtle
import com.example.ui.theme.OutfitFontFamily
import com.example.ui.theme.PrimaryElectricViolet
import com.example.ui.theme.SecondaryTeal
import com.example.ui.theme.TierCriticalRose
import com.example.ui.theme.TierSafeEmerald
import com.example.viewmodel.AttendanceViewModel
import java.util.Locale

@Composable
fun SubjectDetailScreen(
    subjectId: Long,
    viewModel: AttendanceViewModel,
    onBack: () -> Unit,
    onNavigateToSimulator: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val subjects by viewModel.subjects.collectAsState()
    val subject = subjects.find { it.id == subjectId }
    val logs by viewModel.recentLogs.collectAsState()
    val subjectLogs = logs.filter { it.subjectId == subjectId }

    if (subject == null) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(CockpitCanvas),
            contentAlignment = Alignment.Center
        ) {
            Text("Course not found", color = Color.White)
        }
        return
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(CockpitCanvas)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // App Bar
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(CardBase)
                        .border(1.dp, GhostBorder, RoundedCornerShape(10.dp))
                        .testTag("detail_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    IconButton(
                        onClick = { viewModel.openEditSubjectDialog(subject) },
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(CardBase)
                            .border(1.dp, GhostBorder, RoundedCornerShape(10.dp))
                            .testTag("edit_subject_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = SecondaryTeal
                        )
                    }

                    IconButton(
                        onClick = {
                            viewModel.deleteSubject(subject)
                            onBack()
                        },
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(CardBase)
                            .border(1.dp, GhostBorder, RoundedCornerShape(10.dp))
                            .testTag("delete_subject_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = TierCriticalRose
                        )
                    }
                }
            }
        }

        // Subject Header Card
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
                        .padding(18.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = subject.code,
                                style = MaterialTheme.typography.labelMedium,
                                color = SecondaryTeal,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = subject.name,
                                fontFamily = OutfitFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 22.sp,
                                color = Color.White
                            )
                            if (subject.professor.isNotBlank()) {
                                Text(
                                    text = "Instructor: ${subject.professor}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFF94A3B8)
                                )
                            }
                        }

                        StatusCapsule(tier = subject.tier)
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(18.dp)
                    ) {
                        AttendanceCircularGauge(
                            percentage = subject.percentage,
                            tier = subject.tier,
                            size = 86.dp,
                            strokeWidth = 8.dp
                        )

                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "${subject.attendedClasses} / ${subject.totalClasses} Attended",
                                fontFamily = OutfitFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = Color.White
                            )
                            Text(
                                text = "Requirement: ${subject.targetPercentage.toInt()}% minimum",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF94A3B8)
                            )
                            Text(
                                text = "Credits: ${subject.credits}",
                                style = MaterialTheme.typography.bodySmall,
                                color = SecondaryTeal
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Predictive Advice
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(subject.tier.backgroundColor.copy(alpha = 0.12f))
                            .border(1.dp, subject.tier.borderColor.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                            .padding(12.dp)
                    ) {
                        Text(
                            text = subject.predictionAdvice,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = subject.tier.textColor
                        )
                    }
                }
            }
        }

        // Action Buttons: + Present, + Absent, Run Simulation
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { viewModel.markPresent(subject.id) },
                    modifier = Modifier
                        .weight(1f)
                        .height(42.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = TierSafeEmerald.copy(alpha = 0.2f),
                        contentColor = TierSafeEmerald
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("+ Present", fontFamily = OutfitFontFamily, fontWeight = FontWeight.SemiBold)
                }

                Button(
                    onClick = { viewModel.markAbsent(subject.id) },
                    modifier = Modifier
                        .weight(1f)
                        .height(42.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = TierCriticalRose.copy(alpha = 0.2f),
                        contentColor = TierCriticalRose
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("+ Absent", fontFamily = OutfitFontFamily, fontWeight = FontWeight.SemiBold)
                }

                Button(
                    onClick = { onNavigateToSimulator(subject.id) },
                    modifier = Modifier
                        .height(42.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryElectricViolet,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(imageVector = Icons.Default.AutoGraph, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Simulate", fontFamily = OutfitFontFamily, fontWeight = FontWeight.SemiBold)
                }
            }
        }

        // Activity / Log History
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recent Attendance Logs",
                    fontFamily = OutfitFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    color = Color.White
                )
                Text(
                    text = "${subjectLogs.size} logs",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF94A3B8)
                )
            }
        }

        if (subjectLogs.isEmpty()) {
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .border(1.dp, GhostBorderSubtle, RoundedCornerShape(12.dp)),
                    color = CardBase
                ) {
                    Box(modifier = Modifier.padding(24.dp), contentAlignment = Alignment.Center) {
                        Text(
                            text = "No recent logs recorded for this course yet.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF94A3B8)
                        )
                    }
                }
            }
        } else {
            items(subjectLogs, key = { it.id }) { log ->
                val isPresent = log.status == "PRESENT"
                val isAbsent = log.status == "ABSENT"

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .border(1.dp, GhostBorderSubtle, RoundedCornerShape(10.dp)),
                    color = CardBase
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when {
                                            isPresent -> TierSafeEmerald.copy(alpha = 0.15f)
                                            isAbsent -> TierCriticalRose.copy(alpha = 0.15f)
                                            else -> Color(0xFF334155)
                                        }
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = when {
                                        isPresent -> Icons.Default.Check
                                        isAbsent -> Icons.Default.Close
                                        else -> Icons.Default.History
                                    },
                                    contentDescription = null,
                                    tint = when {
                                        isPresent -> TierSafeEmerald
                                        isAbsent -> TierCriticalRose
                                        else -> Color(0xFF94A3B8)
                                    },
                                    modifier = Modifier.size(16.dp)
                                )
                            }

                            Column {
                                Text(
                                    text = log.status,
                                    fontFamily = OutfitFontFamily,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 13.sp,
                                    color = when {
                                        isPresent -> TierSafeEmerald
                                        isAbsent -> TierCriticalRose
                                        else -> Color(0xFF94A3B8)
                                    }
                                )
                                Text(
                                    text = log.dateString + if (log.note.isNotBlank()) " • ${log.note}" else "",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFF94A3B8),
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
