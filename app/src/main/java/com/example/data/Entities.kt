package com.example.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.model.AttendanceLog
import com.example.model.Subject
import com.example.model.TimetableSlot

@Entity(tableName = "subjects")
data class SubjectEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val code: String,
    val professor: String,
    val attendedClasses: Int,
    val totalClasses: Int,
    val targetPercentage: Double,
    val credits: Int,
    val colorHex: String
) {
    fun toDomain(): Subject = Subject(
        id = id,
        name = name,
        code = code,
        professor = professor,
        attendedClasses = attendedClasses,
        totalClasses = totalClasses,
        targetPercentage = targetPercentage,
        credits = credits,
        colorHex = colorHex
    )

    companion object {
        fun fromDomain(subject: Subject): SubjectEntity = SubjectEntity(
            id = subject.id,
            name = subject.name,
            code = subject.code,
            professor = subject.professor,
            attendedClasses = subject.attendedClasses,
            totalClasses = subject.totalClasses,
            targetPercentage = subject.targetPercentage,
            credits = subject.credits,
            colorHex = subject.colorHex
        )
    }
}

@Entity(
    tableName = "attendance_logs",
    foreignKeys = [
        ForeignKey(
            entity = SubjectEntity::class,
            parentColumns = ["id"],
            childColumns = ["subjectId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["subjectId"])]
)
data class AttendanceLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val subjectId: Long,
    val dateString: String,
    val status: String, // "PRESENT", "ABSENT", "CANCELLED"
    val note: String,
    val timestamp: Long
) {
    fun toDomain(): AttendanceLog = AttendanceLog(
        id = id,
        subjectId = subjectId,
        dateString = dateString,
        status = status,
        note = note,
        timestamp = timestamp
    )
}

@Entity(
    tableName = "timetable_slots",
    foreignKeys = [
        ForeignKey(
            entity = SubjectEntity::class,
            parentColumns = ["id"],
            childColumns = ["subjectId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["subjectId"])]
)
data class TimetableSlotEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val subjectId: Long,
    val dayOfWeek: Int, // 1=Mon .. 6=Sat
    val startTime: String,
    val endTime: String,
    val room: String
) {
    fun toDomain(subjectName: String = "", subjectCode: String = ""): TimetableSlot = TimetableSlot(
        id = id,
        subjectId = subjectId,
        subjectName = subjectName,
        subjectCode = subjectCode,
        dayOfWeek = dayOfWeek,
        startTime = startTime,
        endTime = endTime,
        room = room
    )
}
