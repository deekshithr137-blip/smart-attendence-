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
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EventBusy
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Subject
import com.example.model.TimetableSlot
import com.example.ui.components.ProfileAvatarButton
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
fun TimetableScreen(
    viewModel: AttendanceViewModel,
    modifier: Modifier = Modifier
) {
    val timetableSlots by viewModel.timetableSlots.collectAsState()
    val subjects by viewModel.subjects.collectAsState()
    val selectedDay by viewModel.selectedDay.collectAsState()

    val days = listOf(
        1 to "Mon",
        2 to "Tue",
        3 to "Wed",
        4 to "Thu",
        5 to "Fri",
        6 to "Sat"
    )

    val currentDaySlots = timetableSlots.filter { it.dayOfWeek == selectedDay }
    val subjectMap = subjects.associateBy { it.id }

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
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = null,
                            tint = PrimaryElectricViolet,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "SCHEDULE & DAILY LOGS",
                            style = MaterialTheme.typography.labelSmall,
                            color = PrimaryElectricViolet,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Class Timetable",
                        fontFamily = OutfitFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        color = Color.White
                    )
                    Text(
                        text = "Log attendance with 1 tap directly from your daily lecture schedule.",
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

        // Weekday Switcher Row
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(days) { (dayNum, dayLabel) ->
                    val isSelected = selectedDay == dayNum
                    val bg = if (isSelected) PrimaryElectricViolet.copy(alpha = 0.25f) else CardBase
                    val border = if (isSelected) PrimaryElectricViolet else GhostBorder

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(bg)
                            .border(1.dp, border, RoundedCornerShape(10.dp))
                            .clickable { viewModel.setSelectedDay(dayNum) }
                            .padding(horizontal = 16.dp, vertical = 10.dp)
                            .testTag("day_tab_$dayNum"),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = dayLabel,
                                fontFamily = OutfitFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = if (isSelected) Color.White else Color(0xFF94A3B8)
                            )
                        }
                    }
                }
            }
        }

        // Slots Count Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Lectures Scheduled (${currentDaySlots.size})",
                    fontFamily = OutfitFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    color = Color.White
                )

                // Add Slot Button
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(PrimaryElectricViolet)
                        .clickable { viewModel.openAddSlotDialog(selectedDay) }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                        .testTag("add_timetable_slot_btn"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "+ Add Lecture",
                        fontFamily = OutfitFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = Color.White
                    )
                }
            }
        }

        // Slot cards
        if (currentDaySlots.isEmpty()) {
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .border(1.dp, GhostBorder, RoundedCornerShape(16.dp)),
                    color = CardBase
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.Schedule,
                                contentDescription = null,
                                tint = Color(0xFF64748B),
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "No lectures scheduled for this day",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFF94A3B8)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(
                                onClick = { viewModel.openAddSlotDialog(selectedDay) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = PrimaryElectricViolet,
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("+ Add First Lecture", fontFamily = OutfitFontFamily, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        } else {
            items(currentDaySlots, key = { it.id }) { slot ->
                val subject = subjectMap[slot.subjectId]
                TimetableLectureCard(
                    slot = slot,
                    subject = subject,
                    onEdit = { viewModel.openEditSlotDialog(slot) },
                    onMarkPresent = { viewModel.markPresent(slot.subjectId, "Period ${slot.startTime}") },
                    onMarkAbsent = { viewModel.markAbsent(slot.subjectId, "Period ${slot.startTime}") },
                    onMarkCancelled = { viewModel.markCancelled(slot.subjectId, "Cancelled: ${slot.startTime}") }
                )
            }
        }
    }
}

@Composable
fun TimetableLectureCard(
    slot: TimetableSlot,
    subject: Subject?,
    onEdit: () -> Unit,
    onMarkPresent: () -> Unit,
    onMarkAbsent: () -> Unit,
    onMarkCancelled: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, GhostBorder, RoundedCornerShape(16.dp)),
        color = CardBase,
        tonalElevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header: Time & Room & Edit Action
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
                        imageVector = Icons.Default.Schedule,
                        contentDescription = null,
                        tint = SecondaryTeal,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "${slot.startTime} - ${slot.endTime}",
                        fontFamily = OutfitFontFamily,
                        fontWeight = FontWeight.SemiBold,
                        color = SecondaryTeal,
                        fontSize = 13.sp
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = Color(0xFF94A3B8),
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = slot.room,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF94A3B8)
                        )
                    }

                    // Edit Slot Button
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF334155))
                            .clickable(onClick = onEdit)
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                            .testTag("edit_slot_${slot.id}")
                    ) {
                        Text(
                            text = "Edit",
                            fontFamily = OutfitFontFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 11.sp,
                            color = SecondaryTeal
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Course Info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = slot.subjectCode,
                        style = MaterialTheme.typography.labelSmall,
                        color = PrimaryElectricViolet,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = slot.subjectName,
                        fontFamily = OutfitFontFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (subject != null) {
                        Text(
                            text = "Current: ${subject.attendedClasses}/${subject.totalClasses} (${String.format(Locale.US, "%.1f%%", subject.percentage)})",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF94A3B8)
                        )
                    }
                }

                if (subject != null) {
                    StatusCapsule(tier = subject.tier)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Quick 1-tap Attendance Log Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Present
                Button(
                    onClick = onMarkPresent,
                    modifier = Modifier
                        .weight(1f)
                        .height(36.dp)
                        .testTag("timetable_present_${slot.id}"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = TierSafeEmerald.copy(alpha = 0.15f),
                        contentColor = TierSafeEmerald
                    ),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp))
                        Text("Present", fontFamily = OutfitFontFamily, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                    }
                }

                // Absent
                Button(
                    onClick = onMarkAbsent,
                    modifier = Modifier
                        .weight(1f)
                        .height(36.dp)
                        .testTag("timetable_absent_${slot.id}"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = TierCriticalRose.copy(alpha = 0.15f),
                        contentColor = TierCriticalRose
                    ),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = null, modifier = Modifier.size(14.dp))
                        Text("Absent", fontFamily = OutfitFontFamily, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                    }
                }

                // Cancelled
                Button(
                    onClick = onMarkCancelled,
                    modifier = Modifier
                        .weight(1f)
                        .height(36.dp)
                        .testTag("timetable_cancel_${slot.id}"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF334155),
                        contentColor = Color(0xFFCBD5E1)
                    ),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(imageVector = Icons.Default.EventBusy, contentDescription = null, modifier = Modifier.size(14.dp))
                        Text("No Class", fontFamily = OutfitFontFamily, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}
