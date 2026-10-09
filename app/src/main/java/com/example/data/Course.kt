package com.example.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlin.math.ceil
import kotlin.math.floor

/**
 * Room database entity representing a Course for attendance tracking.
 * Stores course name, current attendance, total classes, and threshold percentage.
 */
@Entity(tableName = "courses")
data class Course(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "course_name")
    val courseName: String,

    @ColumnInfo(name = "current_attendance")
    val currentAttendance: Int = 0,

    @ColumnInfo(name = "total_classes")
    val totalClasses: Int = 0,

    @ColumnInfo(name = "threshold_percentage")
    val thresholdPercentage: Double = 75.0,

    @ColumnInfo(name = "course_code")
    val courseCode: String = "",

    @ColumnInfo(name = "instructor")
    val instructor: String = "",

    @ColumnInfo(name = "credits")
    val credits: Int = 3
) {
    /**
     * Calculated attendance percentage (0.0% - 100.0%).
     */
    val percentage: Double
        get() = if (totalClasses == 0) 100.0 else (currentAttendance.toDouble() / totalClasses) * 100.0

    /**
     * Checks if current attendance satisfies the threshold percentage.
     */
    val isCompliant: Boolean
        get() = percentage >= thresholdPercentage

    /**
     * Number of upcoming consecutive classes the student can safely miss
     * while keeping attendance at or above the threshold percentage.
     */
    val canMissClasses: Int
        get() {
            if (totalClasses == 0 || percentage < thresholdPercentage) return 0
            val target = thresholdPercentage / 100.0
            val maxClasses = currentAttendance / target
            val allowance = floor(maxClasses - totalClasses).toInt()
            return allowance.coerceAtLeast(0)
        }

    /**
     * Number of upcoming consecutive classes the student must attend
     * to recover attendance to the threshold percentage.
     */
    val mustAttendClasses: Int
        get() {
            if (percentage >= thresholdPercentage) return 0
            val target = thresholdPercentage / 100.0
            val required = (target * totalClasses - currentAttendance) / (1.0 - target)
            return ceil(required).toInt().coerceAtLeast(1)
        }
}
