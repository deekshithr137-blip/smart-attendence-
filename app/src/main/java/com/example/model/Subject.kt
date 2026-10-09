package com.example.model

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.TierCautionAmber
import com.example.ui.theme.TierCautionBg
import com.example.ui.theme.TierCautionBorder
import com.example.ui.theme.TierCriticalBg
import com.example.ui.theme.TierCriticalBorder
import com.example.ui.theme.TierCriticalRose
import com.example.ui.theme.TierSafeBg
import com.example.ui.theme.TierSafeBorder
import com.example.ui.theme.TierSafeEmerald
import kotlin.math.ceil
import kotlin.math.floor

enum class AttendanceTier(
    val label: String,
    val textColor: Color,
    val backgroundColor: Color,
    val borderColor: Color
) {
    SAFE("Safe Zone", TierSafeEmerald, TierSafeBg, TierSafeBorder),
    CAUTION("Near Limit", TierCautionAmber, TierCautionBg, TierCautionBorder),
    CRITICAL("Debarment Risk", TierCriticalRose, TierCriticalBg, TierCriticalBorder)
}

data class Subject(
    val id: Long = 0,
    val name: String,
    val code: String,
    val professor: String = "",
    val attendedClasses: Int = 0,
    val totalClasses: Int = 0,
    val targetPercentage: Double = 75.0,
    val credits: Int = 3,
    val colorHex: String = "#7C3AED"
) {
    val percentage: Double
        get() = if (totalClasses == 0) 100.0 else (attendedClasses.toDouble() / totalClasses) * 100.0

    val tier: AttendanceTier
        get() = when {
            percentage >= targetPercentage -> AttendanceTier.SAFE
            percentage >= (targetPercentage - 5.0).coerceAtLeast(65.0) -> AttendanceTier.CAUTION
            else -> AttendanceTier.CRITICAL
        }

    // Number of classes student can safely miss while keeping attendance >= targetPercentage
    val canMissClasses: Int
        get() {
            if (totalClasses == 0) return 0
            val target = targetPercentage / 100.0
            if (percentage < targetPercentage) return 0
            val maxTotal = attendedClasses / target
            val missCount = floor(maxTotal - totalClasses).toInt()
            return missCount.coerceAtLeast(0)
        }

    // Number of consecutive classes student must attend to reach targetPercentage
    val mustAttendClasses: Int
        get() {
            if (percentage >= targetPercentage) return 0
            val target = targetPercentage / 100.0
            val req = (target * totalClasses - attendedClasses) / (1.0 - target)
            return ceil(req).toInt().coerceAtLeast(1)
        }

    val predictionAdvice: String
        get() = when (tier) {
            AttendanceTier.SAFE -> {
                val miss = canMissClasses
                if (miss > 0) "Can miss $miss class${if (miss > 1) "es" else ""} safely (Stay ≥ ${targetPercentage.toInt()}%)"
                else "On edge! Do not miss the next class"
            }
            AttendanceTier.CAUTION -> {
                val need = mustAttendClasses
                "Near limit: Attend next $need class${if (need > 1) "es" else ""} to reach ${targetPercentage.toInt()}%"
            }
            AttendanceTier.CRITICAL -> {
                val need = mustAttendClasses
                "Debarment Alert: Must attend $need consecutive classes"
            }
        }
}

data class AttendanceLog(
    val id: Long = 0,
    val subjectId: Long,
    val dateString: String,
    val status: String, // "PRESENT", "ABSENT", "CANCELLED"
    val note: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

data class TimetableSlot(
    val id: Long = 0,
    val subjectId: Long,
    val subjectName: String = "",
    val subjectCode: String = "",
    val dayOfWeek: Int, // 1=Mon, 2=Tue, 3=Wed, 4=Thu, 5=Fri, 6=Sat
    val startTime: String,
    val endTime: String,
    val room: String = "Hall 101"
)

data class SimulationResult(
    val currentPercentage: Double,
    val projectedPercentage: Double,
    val attendedAfter: Int,
    val totalAfter: Int,
    val deltaPercentage: Double,
    val meetsTarget: Boolean,
    val targetPercentage: Double,
    val statusSummary: String,
    val tacticalAdvice: String
)
