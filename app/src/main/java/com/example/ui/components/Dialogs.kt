package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Subject
import com.example.ui.theme.CardBase
import com.example.ui.theme.GhostBorder
import com.example.ui.theme.OutfitFontFamily
import com.example.ui.theme.PrimaryElectricViolet
import com.example.ui.theme.SecondaryTeal
import com.example.ui.theme.TertiaryIndigo
import com.example.ui.theme.TierSafeEmerald
import com.example.ui.theme.TierCriticalRose
import androidx.compose.material.icons.automirrored.filled.Logout

@Composable
fun AddOrEditSubjectDialog(
    initialSubject: Subject? = null,
    onDismiss: () -> Unit,
    onSave: (Subject) -> Unit
) {
    var name by remember { mutableStateOf(initialSubject?.name ?: "") }
    var code by remember { mutableStateOf(initialSubject?.code ?: "") }
    var professor by remember { mutableStateOf(initialSubject?.professor ?: "") }
    var attendedText by remember { mutableStateOf(initialSubject?.attendedClasses?.toString() ?: "0") }
    var totalText by remember { mutableStateOf(initialSubject?.totalClasses?.toString() ?: "0") }
    var targetPercentage by remember { mutableDoubleStateOf(initialSubject?.targetPercentage ?: 75.0) }
    var creditsText by remember { mutableStateOf(initialSubject?.credits?.toString() ?: "3") }

    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedContainerColor = Color(0xFF0F172A),
        unfocusedContainerColor = Color(0xFF0F172A),
        focusedBorderColor = PrimaryElectricViolet,
        unfocusedBorderColor = Color(0x33FFFFFF),
        focusedTextColor = Color.White,
        unfocusedTextColor = Color.White,
        focusedLabelColor = SecondaryTeal,
        unfocusedLabelColor = Color(0xFF94A3B8)
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, GhostBorder, RoundedCornerShape(16.dp))
            .testTag("add_subject_dialog"),
        containerColor = CardBase,
        title = {
            Text(
                text = if (initialSubject == null) "Add New Subject" else "Edit Subject",
                fontFamily = OutfitFontFamily,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                fontSize = 20.sp
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Subject Name (e.g. Distributed Systems)") },
                    modifier = Modifier.fillMaxWidth().testTag("input_subject_name"),
                    shape = RoundedCornerShape(8.dp),
                    colors = textFieldColors,
                    singleLine = true
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = code,
                        onValueChange = { code = it },
                        label = { Text("Course Code") },
                        modifier = Modifier.weight(1f).testTag("input_subject_code"),
                        shape = RoundedCornerShape(8.dp),
                        colors = textFieldColors,
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = creditsText,
                        onValueChange = { creditsText = it },
                        label = { Text("Credits") },
                        modifier = Modifier.weight(0.7f).testTag("input_subject_credits"),
                        shape = RoundedCornerShape(8.dp),
                        colors = textFieldColors,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )
                }

                OutlinedTextField(
                    value = professor,
                    onValueChange = { professor = it },
                    label = { Text("Instructor / Professor") },
                    modifier = Modifier.fillMaxWidth().testTag("input_subject_professor"),
                    shape = RoundedCornerShape(8.dp),
                    colors = textFieldColors,
                    singleLine = true
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = attendedText,
                        onValueChange = { attendedText = it },
                        label = { Text("Attended") },
                        modifier = Modifier.weight(1f).testTag("input_subject_attended"),
                        shape = RoundedCornerShape(8.dp),
                        colors = textFieldColors,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = totalText,
                        onValueChange = { totalText = it },
                        label = { Text("Total Held") },
                        modifier = Modifier.weight(1f).testTag("input_subject_total"),
                        shape = RoundedCornerShape(8.dp),
                        colors = textFieldColors,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Attendance Target Threshold: ${targetPercentage.toInt()}%",
                    style = MaterialTheme.typography.labelMedium,
                    color = SecondaryTeal
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(75.0, 80.0, 85.0).forEach { target ->
                        val isSelected = targetPercentage == target
                        TextButton(
                            onClick = { targetPercentage = target },
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) PrimaryElectricViolet.copy(alpha = 0.25f) else Color(0xFF0F172A))
                                .border(1.dp, if (isSelected) PrimaryElectricViolet else Color(0x33FFFFFF), RoundedCornerShape(8.dp))
                        ) {
                            Text(
                                text = "${target.toInt()}%",
                                color = if (isSelected) Color.White else Color(0xFF94A3B8),
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            PrimaryGradientButton(
                text = "Save Subject",
                onClick = {
                    if (name.isNotBlank()) {
                        val att = attendedText.toIntOrNull() ?: 0
                        val tot = totalText.toIntOrNull() ?: 0
                        val cr = creditsText.toIntOrNull() ?: 3
                        val validTot = if (tot < att) att else tot
                        onSave(
                            Subject(
                                id = initialSubject?.id ?: 0,
                                name = name.trim(),
                                code = if (code.isBlank()) "GEN-101" else code.trim(),
                                professor = professor.trim(),
                                attendedClasses = att,
                                totalClasses = validTot,
                                targetPercentage = targetPercentage,
                                credits = cr,
                                colorHex = initialSubject?.colorHex ?: "#7C3AED"
                            )
                        )
                    }
                },
                modifier = Modifier.testTag("save_subject_button")
            )
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("cancel_subject_button")
            ) {
                Text(
                    text = "Cancel",
                    color = Color(0xFF94A3B8)
                )
            }
        }
    )
}

@Composable
fun AddOrEditSlotDialog(
    initialSlot: com.example.model.TimetableSlot?,
    subjects: List<com.example.model.Subject>,
    onDismiss: () -> Unit,
    onSave: (com.example.model.TimetableSlot) -> Unit,
    onDelete: ((com.example.model.TimetableSlot) -> Unit)? = null
) {
    var selectedSubjectId by remember {
        mutableStateOf(initialSlot?.subjectId ?: subjects.firstOrNull()?.id ?: 1L)
    }
    var dayOfWeek by remember {
        mutableStateOf(initialSlot?.dayOfWeek ?: 1)
    }
    var startTime by remember {
        mutableStateOf(initialSlot?.startTime ?: "09:00 AM")
    }
    var endTime by remember {
        mutableStateOf(initialSlot?.endTime ?: "10:15 AM")
    }
    var room by remember {
        mutableStateOf(initialSlot?.room ?: "Hall 101")
    }

    val days = listOf(
        1 to "Mon",
        2 to "Tue",
        3 to "Wed",
        4 to "Thu",
        5 to "Fri",
        6 to "Sat"
    )

    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedContainerColor = Color(0xFF0F172A),
        unfocusedContainerColor = Color(0xFF0F172A),
        focusedBorderColor = PrimaryElectricViolet,
        unfocusedBorderColor = Color(0x33FFFFFF),
        focusedTextColor = Color.White,
        unfocusedTextColor = Color.White,
        focusedLabelColor = SecondaryTeal,
        unfocusedLabelColor = Color(0xFF94A3B8)
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, GhostBorder, RoundedCornerShape(16.dp))
            .testTag("add_slot_dialog"),
        containerColor = CardBase,
        title = {
            Text(
                text = if (initialSlot?.id == 0L || initialSlot == null) "Add Lecture Slot" else "Edit Lecture Slot",
                fontFamily = OutfitFontFamily,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                fontSize = 20.sp
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Day of Week Selector
                Text(
                    text = "Day of Week",
                    style = MaterialTheme.typography.labelMedium,
                    color = SecondaryTeal
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    days.forEach { (dNum, dLabel) ->
                        val isSelected = dayOfWeek == dNum
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) PrimaryElectricViolet.copy(alpha = 0.3f) else Color(0xFF0F172A))
                                .border(1.dp, if (isSelected) PrimaryElectricViolet else Color(0x33FFFFFF), RoundedCornerShape(8.dp))
                                .clickable { dayOfWeek = dNum }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = dLabel,
                                fontFamily = OutfitFontFamily,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) Color.White else Color(0xFF94A3B8),
                                fontSize = 12.sp
                            )
                        }
                    }
                }

                // Course Selector
                Text(
                    text = "Select Course",
                    style = MaterialTheme.typography.labelMedium,
                    color = SecondaryTeal
                )
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    subjects.forEach { sub ->
                        val isSelected = selectedSubjectId == sub.id
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) PrimaryElectricViolet.copy(alpha = 0.25f) else Color(0xFF0F172A))
                                .border(1.dp, if (isSelected) PrimaryElectricViolet else Color(0x33FFFFFF), RoundedCornerShape(8.dp))
                                .clickable { selectedSubjectId = sub.id }
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "${sub.code}: ${sub.name}",
                                        fontFamily = OutfitFontFamily,
                                        fontWeight = FontWeight.SemiBold,
                                        color = if (isSelected) Color.White else Color(0xFFCBD5E1),
                                        fontSize = 13.sp
                                    )
                                    Text(
                                        text = "Current: ${sub.attendedClasses}/${sub.totalClasses} classes",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color(0xFF94A3B8),
                                        fontSize = 11.sp
                                    )
                                }
                                if (isSelected) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(androidx.compose.foundation.shape.CircleShape)
                                            .background(PrimaryElectricViolet)
                                    )
                                }
                            }
                        }
                    }
                }

                // Time Pickers
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = startTime,
                        onValueChange = { startTime = it },
                        label = { Text("Start Time") },
                        modifier = Modifier.weight(1f).testTag("input_slot_start_time"),
                        shape = RoundedCornerShape(8.dp),
                        colors = textFieldColors,
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = endTime,
                        onValueChange = { endTime = it },
                        label = { Text("End Time") },
                        modifier = Modifier.weight(1f).testTag("input_slot_end_time"),
                        shape = RoundedCornerShape(8.dp),
                        colors = textFieldColors,
                        singleLine = true
                    )
                }

                // Room / Location
                OutlinedTextField(
                    value = room,
                    onValueChange = { room = it },
                    label = { Text("Room / Hall / Lab") },
                    modifier = Modifier.fillMaxWidth().testTag("input_slot_room"),
                    shape = RoundedCornerShape(8.dp),
                    colors = textFieldColors,
                    singleLine = true
                )

                // Delete Slot option if editing
                if (initialSlot != null && initialSlot.id != 0L && onDelete != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    TextButton(
                        onClick = { onDelete(initialSlot) },
                        modifier = Modifier.fillMaxWidth().testTag("delete_slot_btn")
                    ) {
                        Text(
                            text = "Delete This Slot",
                            color = com.example.ui.theme.TierCriticalRose,
                            fontFamily = OutfitFontFamily,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        },
        confirmButton = {
            PrimaryGradientButton(
                text = "Save Lecture Slot",
                onClick = {
                    val sub = subjects.find { it.id == selectedSubjectId }
                    onSave(
                        com.example.model.TimetableSlot(
                            id = initialSlot?.id ?: 0L,
                            subjectId = selectedSubjectId,
                            subjectName = sub?.name ?: "",
                            subjectCode = sub?.code ?: "",
                            dayOfWeek = dayOfWeek,
                            startTime = startTime.trim(),
                            endTime = endTime.trim(),
                            room = room.trim()
                        )
                    )
                },
                modifier = Modifier.testTag("save_slot_button")
            )
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("cancel_slot_button")
            ) {
                Text(
                    text = "Cancel",
                    color = Color(0xFF94A3B8)
                )
            }
        }
    )
}

@Composable
fun StudentProfileDialog(
    profile: com.example.viewmodel.UserProfile,
    overallStats: com.example.viewmodel.OverallStats,
    totalCredits: Int,
    onDismiss: () -> Unit,
    onSave: (com.example.viewmodel.UserProfile) -> Unit,
    onLogout: () -> Unit = {}
) {
    var isEditing by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf(profile.name) }
    var studentId by remember { mutableStateOf(profile.studentId) }
    var major by remember { mutableStateOf(profile.major) }
    var semester by remember { mutableStateOf(profile.semester) }
    var targetGoal by remember { mutableDoubleStateOf(profile.targetGoal) }

    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedContainerColor = Color(0xFF0F172A),
        unfocusedContainerColor = Color(0xFF0F172A),
        focusedBorderColor = PrimaryElectricViolet,
        unfocusedBorderColor = Color(0x33FFFFFF),
        focusedTextColor = Color.White,
        unfocusedTextColor = Color.White,
        focusedLabelColor = SecondaryTeal,
        unfocusedLabelColor = Color(0xFF94A3B8)
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .border(1.dp, GhostBorder, RoundedCornerShape(20.dp))
            .testTag("student_profile_dialog"),
        containerColor = CardBase,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Student Profile",
                    fontFamily = OutfitFontFamily,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 20.sp
                )

                IconButton(
                    onClick = { isEditing = !isEditing },
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isEditing) PrimaryElectricViolet.copy(alpha = 0.3f) else Color(0xFF334155))
                        .testTag("edit_profile_toggle_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Profile",
                        tint = if (isEditing) PrimaryElectricViolet else SecondaryTeal,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Profile Avatar Hero
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(PrimaryElectricViolet, TertiaryIndigo)
                                )
                            )
                            .border(2.dp, Color.White.copy(alpha = 0.3f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = profile.initials,
                            fontFamily = OutfitFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp,
                            color = Color.White
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isEditing) name else profile.name,
                            fontFamily = OutfitFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Color.White
                        )
                        Text(
                            text = if (isEditing) studentId else profile.studentId,
                            style = MaterialTheme.typography.bodySmall,
                            color = SecondaryTeal,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = if (isEditing) "$major • $semester" else "${profile.major} • ${profile.semester}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF94A3B8)
                        )
                    }
                }

                // Academic Attendance Standing Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF0F172A))
                        .border(1.dp, Color(0x26FFFFFF), RoundedCornerShape(12.dp))
                        .padding(14.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "ACADEMIC SUMMARY",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF94A3B8),
                                fontWeight = FontWeight.Bold
                            )
                            StatusCapsule(tier = overallStats.tier)
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = "Attendance",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFF94A3B8)
                                )
                                Text(
                                    text = String.format(java.util.Locale.US, "%.1f%%", overallStats.percentage),
                                    fontFamily = OutfitFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = overallStats.tier.textColor
                                )
                            }

                            Column {
                                Text(
                                    text = "Classes",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFF94A3B8)
                                )
                                Text(
                                    text = "${overallStats.totalAttended} / ${overallStats.totalHeld}",
                                    fontFamily = OutfitFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = Color.White
                                )
                            }

                            Column {
                                Text(
                                    text = "Credits",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFF94A3B8)
                                )
                                Text(
                                    text = "$totalCredits Cr",
                                    fontFamily = OutfitFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = SecondaryTeal
                                )
                            }
                        }
                    }
                }

                if (isEditing) {
                    Text(
                        text = "Edit Student Details",
                        style = MaterialTheme.typography.labelMedium,
                        color = SecondaryTeal
                    )

                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Full Name") },
                        modifier = Modifier.fillMaxWidth().testTag("profile_name_input"),
                        shape = RoundedCornerShape(8.dp),
                        colors = textFieldColors,
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = studentId,
                        onValueChange = { studentId = it },
                        label = { Text("Roll / Student ID Number") },
                        modifier = Modifier.fillMaxWidth().testTag("profile_id_input"),
                        shape = RoundedCornerShape(8.dp),
                        colors = textFieldColors,
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = major,
                        onValueChange = { major = it },
                        label = { Text("Major / Branch (e.g. Computer Science)") },
                        modifier = Modifier.fillMaxWidth().testTag("profile_major_input"),
                        shape = RoundedCornerShape(8.dp),
                        colors = textFieldColors,
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = semester,
                        onValueChange = { semester = it },
                        label = { Text("Semester / Year") },
                        modifier = Modifier.fillMaxWidth().testTag("profile_semester_input"),
                        shape = RoundedCornerShape(8.dp),
                        colors = textFieldColors,
                        singleLine = true
                    )

                    // Target Attendance Benchmark
                    Text(
                        text = "Goal Target Threshold: ${targetGoal.toInt()}%",
                        style = MaterialTheme.typography.labelMedium,
                        color = SecondaryTeal
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(75.0, 80.0, 85.0).forEach { goal ->
                            val isSelected = targetGoal == goal
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) PrimaryElectricViolet.copy(alpha = 0.3f) else Color(0xFF0F172A))
                                    .border(1.dp, if (isSelected) PrimaryElectricViolet else Color(0x33FFFFFF), RoundedCornerShape(8.dp))
                                    .clickable { targetGoal = goal }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${goal.toInt()}%",
                                    fontFamily = OutfitFontFamily,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) Color.White else Color(0xFF94A3B8),
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }

                // Sign Out & Switch Account Option
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF2A121A))
                        .border(1.dp, TierCriticalRose.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                        .clickable { onLogout() }
                        .padding(vertical = 10.dp, horizontal = 14.dp)
                        .testTag("logout_button"),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Logout,
                        contentDescription = "Log Out",
                        tint = TierCriticalRose,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Sign Out / Switch Account",
                        fontFamily = OutfitFontFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp,
                        color = TierCriticalRose
                    )
                }
            }
        },
        confirmButton = {
            if (isEditing) {
                PrimaryGradientButton(
                    text = "Save Profile",
                    onClick = {
                        onSave(
                            profile.copy(
                                name = name.trim(),
                                studentId = studentId.trim(),
                                major = major.trim(),
                                semester = semester.trim(),
                                targetGoal = targetGoal
                            )
                        )
                    },
                    modifier = Modifier.testTag("save_profile_button")
                )
            } else {
                PrimaryGradientButton(
                    text = "Done",
                    onClick = onDismiss,
                    modifier = Modifier.testTag("done_profile_button")
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("cancel_profile_button")
            ) {
                Text(text = "Close", color = Color(0xFF94A3B8))
            }
        }
    )
}
