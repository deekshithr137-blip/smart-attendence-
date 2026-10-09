package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.AttendanceRepository
import com.example.model.AttendanceLog
import com.example.model.AttendanceTier
import com.example.model.SimulationResult
import com.example.model.Subject
import com.example.model.TimetableSlot
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.ceil
import kotlin.math.floor

data class OverallStats(
    val percentage: Double = 0.0,
    val totalAttended: Int = 0,
    val totalHeld: Int = 0,
    val tier: AttendanceTier = AttendanceTier.SAFE,
    val safeCount: Int = 0,
    val cautionCount: Int = 0,
    val criticalCount: Int = 0,
    val canBunkTotal: Int = 0,
    val mustAttendTotal: Int = 0
)

data class UserProfile(
    val name: String = "Alex Chen",
    val studentId: String = "CS23B1042",
    val email: String = "alex.chen@university.edu",
    val major: String = "B.Tech Computer Science",
    val semester: String = "Semester 6",
    val targetGoal: Double = 75.0
) {
    val initials: String
        get() {
            val parts = name.trim().split(" ").filter { it.isNotBlank() }
            return when {
                parts.isEmpty() -> "AC"
                parts.size == 1 -> parts[0].take(2).uppercase(Locale.US)
                else -> "${parts[0].first()}${parts[1].first()}".uppercase(Locale.US)
            }
        }
}

data class CopilotMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val isUser: Boolean,
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val metricHighlight: String? = null
)

class AttendanceViewModel(
    private val repository: AttendanceRepository
) : ViewModel() {

    val subjects: StateFlow<List<Subject>> = repository.allSubjects
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val timetableSlots: StateFlow<List<TimetableSlot>> = repository.timetableSlots
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recentLogs: StateFlow<List<AttendanceLog>> = repository.getRecentLogs()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Overall metrics derived from subjects
    val overallStats: StateFlow<OverallStats> = subjects.combine(MutableStateFlow(Unit)) { subList, _ ->
        if (subList.isEmpty()) {
            OverallStats()
        } else {
            val totalAtt = subList.sumOf { it.attendedClasses }
            val totalH = subList.sumOf { it.totalClasses }
            val pct = if (totalH == 0) 100.0 else (totalAtt.toDouble() / totalH) * 100.0
            val target = 75.0

            val tier = when {
                pct >= target -> AttendanceTier.SAFE
                pct >= 70.0 -> AttendanceTier.CAUTION
                else -> AttendanceTier.CRITICAL
            }

            val safe = subList.count { it.tier == AttendanceTier.SAFE }
            val caution = subList.count { it.tier == AttendanceTier.CAUTION }
            val critical = subList.count { it.tier == AttendanceTier.CRITICAL }

            // Aggregate bunk allowance or classes needed
            val canBunk = if (pct >= target) {
                floor((totalAtt / (target / 100.0)) - totalH).toInt().coerceAtLeast(0)
            } else 0

            val mustAttend = if (pct < target) {
                ceil(((target / 100.0) * totalH - totalAtt) / (1.0 - (target / 100.0))).toInt().coerceAtLeast(1)
            } else 0

            OverallStats(
                percentage = pct,
                totalAttended = totalAtt,
                totalHeld = totalH,
                tier = tier,
                safeCount = safe,
                cautionCount = caution,
                criticalCount = critical,
                canBunkTotal = canBunk,
                mustAttendTotal = mustAttend
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), OverallStats())

    // Filter and search
    private val _selectedTierFilter = MutableStateFlow<AttendanceTier?>(null)
    val selectedTierFilter: StateFlow<AttendanceTier?> = _selectedTierFilter.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    fun setTierFilter(tier: AttendanceTier?) {
        _selectedTierFilter.value = tier
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    // Timetable selected day (1=Mon..6=Sat)
    private val _selectedDay = MutableStateFlow(getTodayDayOfWeek())
    val selectedDay: StateFlow<Int> = _selectedDay.asStateFlow()

    fun setSelectedDay(day: Int) {
        _selectedDay.value = day
    }

    private fun getTodayDayOfWeek(): Int {
        val cal = Calendar.getInstance()
        val day = cal.get(Calendar.DAY_OF_WEEK)
        // Calendar.MONDAY is 2, SUNDAY is 1
        return when (day) {
            Calendar.MONDAY -> 1
            Calendar.TUESDAY -> 2
            Calendar.WEDNESDAY -> 3
            Calendar.THURSDAY -> 4
            Calendar.FRIDAY -> 5
            Calendar.SATURDAY -> 6
            else -> 1 // Sunday defaults to Monday schedule
        }
    }

    // Dialog state
    private val _editingSubject = MutableStateFlow<Subject?>(null)
    val editingSubject: StateFlow<Subject?> = _editingSubject.asStateFlow()

    private val _isAddDialogOpen = MutableStateFlow(false)
    val isAddDialogOpen: StateFlow<Boolean> = _isAddDialogOpen.asStateFlow()

    fun openAddSubjectDialog() {
        _editingSubject.value = null
        _isAddDialogOpen.value = true
    }

    fun openEditSubjectDialog(subject: Subject) {
        _editingSubject.value = subject
        _isAddDialogOpen.value = true
    }

    fun closeAddDialog() {
        _isAddDialogOpen.value = false
        _editingSubject.value = null
    }

    fun saveSubject(subject: Subject) {
        viewModelScope.launch {
            if (subject.id == 0L) {
                repository.insertSubject(subject)
            } else {
                repository.updateSubject(subject)
            }
            closeAddDialog()
        }
    }

    fun deleteSubject(subject: Subject) {
        viewModelScope.launch {
            repository.deleteSubject(subject)
        }
    }

    // Timetable slot dialog state
    private val _editingSlot = MutableStateFlow<TimetableSlot?>(null)
    val editingSlot: StateFlow<TimetableSlot?> = _editingSlot.asStateFlow()

    private val _isSlotDialogOpen = MutableStateFlow(false)
    val isSlotDialogOpen: StateFlow<Boolean> = _isSlotDialogOpen.asStateFlow()

    fun openAddSlotDialog(defaultDay: Int? = null) {
        _editingSlot.value = TimetableSlot(
            id = 0,
            subjectId = subjects.value.firstOrNull()?.id ?: 1L,
            dayOfWeek = defaultDay ?: selectedDay.value,
            startTime = "09:00 AM",
            endTime = "10:15 AM",
            room = "Hall 101"
        )
        _isSlotDialogOpen.value = true
    }

    fun openEditSlotDialog(slot: TimetableSlot) {
        _editingSlot.value = slot
        _isSlotDialogOpen.value = true
    }

    fun closeSlotDialog() {
        _isSlotDialogOpen.value = false
        _editingSlot.value = null
    }

    fun saveTimetableSlot(slot: TimetableSlot) {
        viewModelScope.launch {
            if (slot.id == 0L) {
                repository.addTimetableSlot(slot)
            } else {
                repository.updateTimetableSlot(slot)
            }
            closeSlotDialog()
        }
    }

    fun deleteTimetableSlot(slot: TimetableSlot) {
        viewModelScope.launch {
            repository.deleteTimetableSlot(slot)
            closeSlotDialog()
        }
    }

    // Student Profile State & Dialog
    private val _userProfile = MutableStateFlow(UserProfile())
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

    private val _isProfileDialogOpen = MutableStateFlow(false)
    val isProfileDialogOpen: StateFlow<Boolean> = _isProfileDialogOpen.asStateFlow()

    fun openProfileDialog() {
        _isProfileDialogOpen.value = true
    }

    fun closeProfileDialog() {
        _isProfileDialogOpen.value = false
    }

    fun updateUserProfile(profile: UserProfile) {
        _userProfile.value = profile
        closeProfileDialog()
    }

    // Authentication & Login Session State
    private val _isAuthenticated = MutableStateFlow(false)
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated.asStateFlow()

    private val _authError = MutableStateFlow<String?>(null)
    val authError: StateFlow<String?> = _authError.asStateFlow()

    fun login(identifier: String, password: String): Boolean {
        if (identifier.isBlank() || password.isBlank()) {
            _authError.value = "Please enter student ID / email and password"
            return false
        }
        _authError.value = null
        val current = _userProfile.value
        _userProfile.value = current.copy(
            email = if (identifier.contains("@")) identifier.trim() else "${identifier.trim().lowercase(Locale.US)}@university.edu",
            studentId = if (!identifier.contains("@")) identifier.trim().uppercase(Locale.US) else current.studentId
        )
        _isAuthenticated.value = true
        return true
    }

    fun register(
        name: String,
        studentId: String,
        email: String,
        major: String,
        semester: String,
        targetGoal: Double,
        password: String
    ): Boolean {
        if (name.isBlank() || studentId.isBlank() || email.isBlank() || password.isBlank()) {
            _authError.value = "Please fill in all required registration fields"
            return false
        }
        _authError.value = null
        _userProfile.value = UserProfile(
            name = name.trim(),
            studentId = studentId.trim().uppercase(Locale.US),
            email = email.trim(),
            major = major.trim(),
            semester = semester.trim(),
            targetGoal = targetGoal
        )
        _isAuthenticated.value = true
        return true
    }

    fun quickSignIn() {
        _authError.value = null
        _isAuthenticated.value = true
    }

    fun logout() {
        _isAuthenticated.value = false
        closeProfileDialog()
    }

    fun clearAuthError() {
        _authError.value = null
    }

    // Attendance Logging
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

    fun markPresent(subjectId: Long, note: String = "") {
        viewModelScope.launch {
            val dateStr = dateFormat.format(Date())
            repository.markAttendance(subjectId, true, dateStr, note)
        }
    }

    fun markAbsent(subjectId: Long, note: String = "") {
        viewModelScope.launch {
            val dateStr = dateFormat.format(Date())
            repository.markAttendance(subjectId, false, dateStr, note)
        }
    }

    fun markCancelled(subjectId: Long, note: String = "Class Cancelled") {
        viewModelScope.launch {
            val dateStr = dateFormat.format(Date())
            repository.markCancelled(subjectId, dateStr, note)
        }
    }

    fun undoLastLog() {
        viewModelScope.launch {
            val logs = recentLogs.value
            if (logs.isNotEmpty()) {
                val lastLog = logs.first()
                repository.undoLastLog(lastLog)
            }
        }
    }

    // What-If Simulation Sandbox State
    private val _simulationSubjectId = MutableStateFlow<Long?>(null)
    val simulationSubjectId: StateFlow<Long?> = _simulationSubjectId.asStateFlow()

    private val _simUpcomingAttend = MutableStateFlow(5)
    val simUpcomingAttend: StateFlow<Int> = _simUpcomingAttend.asStateFlow()

    private val _simUpcomingMiss = MutableStateFlow(1)
    val simUpcomingMiss: StateFlow<Int> = _simUpcomingMiss.asStateFlow()

    private val _simTargetPercentage = MutableStateFlow(75.0)
    val simTargetPercentage: StateFlow<Double> = _simTargetPercentage.asStateFlow()

    fun setSimulationSubject(subjectId: Long?) {
        _simulationSubjectId.value = subjectId
        val sub = subjects.value.find { it.id == subjectId }
        if (sub != null) {
            _simTargetPercentage.value = sub.targetPercentage
        }
    }

    fun setSimUpcomingAttend(count: Int) {
        _simUpcomingAttend.value = count.coerceIn(0, 50)
    }

    fun setSimUpcomingMiss(count: Int) {
        _simUpcomingMiss.value = count.coerceIn(0, 50)
    }

    fun setSimTargetPercentage(target: Double) {
        _simTargetPercentage.value = target
    }

    fun calculateSimulation(): SimulationResult {
        val subId = _simulationSubjectId.value
        val sub = subjects.value.find { it.id == subId }
        val target = _simTargetPercentage.value

        val currentAtt: Int
        val currentTot: Int
        val subjectName: String

        if (sub != null) {
            currentAtt = sub.attendedClasses
            currentTot = sub.totalClasses
            subjectName = sub.name
        } else {
            currentAtt = subjects.value.sumOf { it.attendedClasses }
            currentTot = subjects.value.sumOf { it.totalClasses }
            subjectName = "Overall Semester"
        }

        val curPct = if (currentTot == 0) 100.0 else (currentAtt.toDouble() / currentTot) * 100.0

        val projectedAtt = currentAtt + _simUpcomingAttend.value
        val projectedTot = currentTot + _simUpcomingAttend.value + _simUpcomingMiss.value
        val projectedPct = if (projectedTot == 0) 100.0 else (projectedAtt.toDouble() / projectedTot) * 100.0
        val delta = projectedPct - curPct
        val meets = projectedPct >= target

        val summary: String
        val tactical: String

        if (meets) {
            val margin = projectedPct - target
            summary = "Compliant! Projected attendance is %.1f%% (+%.1f%% buffer above target)".format(projectedPct, margin)
            tactical = if (_simUpcomingMiss.value > 0) {
                "You can afford missing ${_simUpcomingMiss.value} class${if (_simUpcomingMiss.value > 1) "es" else ""} because you attend ${_simUpcomingAttend.value} class${if (_simUpcomingAttend.value > 1) "es" else ""}."
            } else {
                "Attending ${_simUpcomingAttend.value} consecutive classes elevates your attendance to %.1f%%.".format(projectedPct)
            }
        } else {
            val deficit = target - projectedPct
            val neededConsecutive = ceil(((target / 100.0) * projectedTot - projectedAtt) / (1.0 - (target / 100.0))).toInt().coerceAtLeast(1)
            summary = "Risk Warning! Projected attendance drops to %.1f%% (%.1f%% below target)".format(projectedPct, deficit)
            tactical = "Missing ${_simUpcomingMiss.value} class${if (_simUpcomingMiss.value > 1) "es" else ""} would breach compliance. You would need $neededConsecutive additional consecutive classes to recover."
        }

        return SimulationResult(
            currentPercentage = curPct,
            projectedPercentage = projectedPct,
            attendedAfter = projectedAtt,
            totalAfter = projectedTot,
            deltaPercentage = delta,
            meetsTarget = meets,
            targetPercentage = target,
            statusSummary = summary,
            tacticalAdvice = tactical
        )
    }

    // AI Academic Copilot / Advisor
    private val _copilotMessages = MutableStateFlow<List<CopilotMessage>>(
        listOf(
            CopilotMessage(
                isUser = false,
                text = "Welcome to your Academic Command Copilot. I continuously monitor your course compliance, debarment hazards, and test upcoming attendance scenarios.\n\nAsk me any question or pick a scenario below to run predictive analysis.",
                metricHighlight = "Safe Threshold: 75%"
            )
        )
    )
    val copilotMessages: StateFlow<List<CopilotMessage>> = _copilotMessages.asStateFlow()

    fun runCopilotScenario(scenarioPrompt: String) {
        val userMsg = CopilotMessage(isUser = true, text = scenarioPrompt)
        val currentSubs = subjects.value
        val stats = overallStats.value

        val responseText: String
        val highlight: String

        when {
            scenarioPrompt.contains("Friday", ignoreCase = true) || scenarioPrompt.contains("skip", ignoreCase = true) -> {
                // Evaluate Friday classes or skipping risk
                val fridaySlots = timetableSlots.value.filter { it.dayOfWeek == 5 }
                val fridaySubIds = fridaySlots.map { it.subjectId }.distinct()
                val fridaySubs = currentSubs.filter { it.id in fridaySubIds }

                val riskSubs = fridaySubs.filter { it.canMissClasses == 0 }
                val safeSubs = fridaySubs.filter { it.canMissClasses > 0 }

                val sb = StringBuilder()
                if (fridaySubs.isEmpty()) {
                    sb.append("You have no scheduled classes on Friday in your timetable.")
                    highlight = "No Classes"
                } else if (riskSubs.isNotEmpty()) {
                    sb.append("⚠️ HIGH RISK! Skipping Friday will trigger attendance warnings in:\n")
                    riskSubs.forEach {
                        sb.append("• ${it.name} (${String.format(Locale.US, "%.1f%%", it.percentage)}): CANNOT miss any classes without falling below ${it.targetPercentage.toInt()}%\n")
                    }
                    if (safeSubs.isNotEmpty()) {
                        sb.append("\nHowever, you have safe buffer in:\n")
                        safeSubs.forEach {
                            sb.append("• ${it.name}: Can miss ${it.canMissClasses} class safely.\n")
                        }
                    }
                    sb.append("\nVerdict: Attend your ${riskSubs.first().name} class to protect your compliance!")
                    highlight = "High Risk Detected"
                } else {
                    sb.append("✅ SAFE TO SKIP! All your scheduled Friday classes have sufficient buffer:\n")
                    fridaySubs.forEach {
                        sb.append("• ${it.name} (${String.format(Locale.US, "%.1f%%", it.percentage)}): You can miss ${it.canMissClasses} class(es).\n")
                    }
                    sb.append("\nYour overall attendance will remain comfortably above 75%.")
                    highlight = "Safe: Buffer Available"
                }
                responseText = sb.toString()
            }

            scenarioPrompt.contains("Debarment", ignoreCase = true) || scenarioPrompt.contains("Recovery", ignoreCase = true) -> {
                val criticals = currentSubs.filter { it.tier == AttendanceTier.CRITICAL }
                val cautions = currentSubs.filter { it.tier == AttendanceTier.CAUTION }

                val sb = StringBuilder()
                if (criticals.isEmpty() && cautions.isEmpty()) {
                    sb.append("🎉 Outstanding news! None of your subjects are in the Debarment or Caution zone. All subjects currently comply with university threshold guidelines.")
                    highlight = "Zero Risk"
                } else {
                    sb.append("🚨 ACADEMIC EMERGENCY ACTION PLAN:\n\n")
                    if (criticals.isNotEmpty()) {
                        sb.append("CRITICAL DEBARMENT THREATS:\n")
                        criticals.forEach {
                            val need = it.mustAttendClasses
                            sb.append("• ${it.name} (${String.format(Locale.US, "%.1f%%", it.percentage)}): Must attend NEXT $need CONSECUTIVE classes to reach ${it.targetPercentage.toInt()}%.\n")
                        }
                    }
                    if (cautions.isNotEmpty()) {
                        sb.append("\nCAUTION ZONE (Near Limit):\n")
                        cautions.forEach {
                            val need = it.mustAttendClasses
                            sb.append("• ${it.name} (${String.format(Locale.US, "%.1f%%", it.percentage)}): Attend $need more class(es) to enter safe zone.\n")
                        }
                    }
                    sb.append("\nPriority Rule: Prioritize lectures marked above and refrain from personal leaves for the next 10 academic days.")
                    highlight = "${criticals.size} Debarment Threat(s)"
                }
                responseText = sb.toString()
            }

            scenarioPrompt.contains("Exam Eligibility", ignoreCase = true) || scenarioPrompt.contains("Eligibility", ignoreCase = true) -> {
                val eligible = currentSubs.filter { it.percentage >= 75.0 }
                val notEligible = currentSubs.filter { it.percentage < 75.0 }

                val sb = StringBuilder()
                sb.append("📋 MIDTERM & END-SEMESTER ELIGIBILITY AUDIT:\n\n")
                sb.append("Eligible Courses: ${eligible.size}/${currentSubs.size}\n")
                sb.append("Current Aggregate: ${String.format(Locale.US, "%.1f%%", stats.percentage)}\n\n")

                if (notEligible.isEmpty()) {
                    sb.append("✅ You are presently eligible for exam hall tickets across ALL subjects!")
                    highlight = "100% Eligible"
                } else {
                    sb.append("⚠️ Currently Ineligible for Exam Hall Ticket in:\n")
                    notEligible.forEach {
                        sb.append("• ${it.code} ${it.name}: ${String.format(Locale.US, "%.1f%%", it.percentage)} (Requires ${it.mustAttendClasses} more classes)\n")
                    }
                    highlight = "${notEligible.size} Blocked Courses"
                }
                responseText = sb.toString()
            }

            scenarioPrompt.contains("Medical Leave", ignoreCase = true) -> {
                val simulatedDays = 3
                val missedClassesPerSubject = 2
                val sb = StringBuilder()
                sb.append("🏥 MEDICAL LEAVE IMPACT SIMULATION (3 Academic Days / ~6-8 Classes):\n\n")

                var newlyDebarred = 0
                currentSubs.forEach { sub ->
                    val projAtt = sub.attendedClasses
                    val projTot = sub.totalClasses + missedClassesPerSubject
                    val projPct = if (projTot == 0) 100.0 else (projAtt.toDouble() / projTot) * 100.0
                    val drop = sub.percentage - projPct
                    sb.append("• ${sub.name}: ${String.format(Locale.US, "%.1f%%", sub.percentage)} ➔ ${String.format(Locale.US, "%.1f%%", projPct)} (-${String.format(Locale.US, "%.1f%%", drop)})\n")
                    if (sub.percentage >= sub.targetPercentage && projPct < sub.targetPercentage) {
                        newlyDebarred++
                    }
                }

                if (newlyDebarred > 0) {
                    sb.append("\n⚠️ Warning: A 3-day absence will cause $newlyDebarred course(s) to breach compliance unless medical certificates are countersigned by HOD.")
                    highlight = "Requires Medical Slip"
                } else {
                    sb.append("\n✅ All subjects will retain attendance above their required thresholds.")
                    highlight = "Manageable Impact"
                }
                responseText = sb.toString()
            }

            else -> {
                // Custom question analysis
                val sb = StringBuilder()
                sb.append("Tactical Analysis based on your current academic record:\n")
                sb.append("• Aggregate Attendance: ${String.format(Locale.US, "%.1f%%", stats.percentage)} (${stats.totalAttended}/${stats.totalHeld} classes attended)\n")
                sb.append("• Safe buffer: You can afford to miss up to ${stats.canBunkTotal} classes across your courses without dropping below the 75% university benchmark.\n\n")

                val weakest = currentSubs.minByOrNull { it.percentage }
                if (weakest != null) {
                    sb.append("Your most vulnerable course is '${weakest.name}' at ${String.format(Locale.US, "%.1f%%", weakest.percentage)}. Focus on not missing this subject.")
                }
                highlight = "Current Standing: ${String.format(Locale.US, "%.1f%%", stats.percentage)}"
                responseText = sb.toString()
            }
        }

        val aiMsg = CopilotMessage(
            isUser = false,
            text = responseText,
            metricHighlight = highlight
        )

        _copilotMessages.value = _copilotMessages.value + userMsg + aiMsg
    }
}

class AttendanceViewModelFactory(
    private val repository: AttendanceRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AttendanceViewModel::class.java)) {
            return AttendanceViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
