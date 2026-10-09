package com.example.data

import com.example.model.AttendanceLog
import com.example.model.Subject
import com.example.model.TimetableSlot
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

class AttendanceRepository(private val dao: AttendanceDao) {

    val allSubjects: Flow<List<Subject>> = dao.getAllSubjects().map { entities ->
        entities.map { it.toDomain() }
    }

    suspend fun getSubject(id: Long): Subject? {
        return dao.getSubjectById(id)?.toDomain()
    }

    suspend fun insertSubject(subject: Subject): Long {
        return dao.insertSubject(SubjectEntity.fromDomain(subject))
    }

    suspend fun updateSubject(subject: Subject) {
        dao.updateSubject(SubjectEntity.fromDomain(subject))
    }

    suspend fun deleteSubject(subject: Subject) {
        dao.deleteSubject(SubjectEntity.fromDomain(subject))
    }

    suspend fun markAttendance(subjectId: Long, isPresent: Boolean, dateString: String, note: String = "") {
        if (isPresent) {
            dao.markPresent(subjectId)
        } else {
            dao.markAbsent(subjectId)
        }
        dao.insertLog(
            AttendanceLogEntity(
                id = 0,
                subjectId = subjectId,
                dateString = dateString,
                status = if (isPresent) "PRESENT" else "ABSENT",
                note = note,
                timestamp = System.currentTimeMillis()
            )
        )
    }

    suspend fun markCancelled(subjectId: Long, dateString: String, note: String = "Class Cancelled") {
        // Cancelled class does not change total or attended counts
        dao.insertLog(
            AttendanceLogEntity(
                id = 0,
                subjectId = subjectId,
                dateString = dateString,
                status = "CANCELLED",
                note = note,
                timestamp = System.currentTimeMillis()
            )
        )
    }

    suspend fun undoLastLog(log: AttendanceLog) {
        if (log.status == "PRESENT") {
            dao.undoClass(log.subjectId, true)
        } else if (log.status == "ABSENT") {
            dao.undoClass(log.subjectId, false)
        }
        dao.deleteLog(
            AttendanceLogEntity(
                id = log.id,
                subjectId = log.subjectId,
                dateString = log.dateString,
                status = log.status,
                note = log.note,
                timestamp = log.timestamp
            )
        )
    }

    fun getLogsForSubject(subjectId: Long): Flow<List<AttendanceLog>> {
        return dao.getLogsForSubject(subjectId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    fun getRecentLogs(): Flow<List<AttendanceLog>> {
        return dao.getRecentLogs().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    val timetableSlots: Flow<List<TimetableSlot>> = combine(
        dao.getAllTimetableSlots(),
        dao.getAllSubjects()
    ) { slots, subjects ->
        val subjectMap = subjects.associateBy { it.id }
        slots.map { slot ->
            val sub = subjectMap[slot.subjectId]
            slot.toDomain(
                subjectName = sub?.name ?: "Unknown Subject",
                subjectCode = sub?.code ?: ""
            )
        }
    }

    suspend fun addTimetableSlot(slot: TimetableSlot): Long {
        return dao.insertTimetableSlot(
            TimetableSlotEntity(
                id = slot.id,
                subjectId = slot.subjectId,
                dayOfWeek = slot.dayOfWeek,
                startTime = slot.startTime,
                endTime = slot.endTime,
                room = slot.room
            )
        )
    }

    suspend fun updateTimetableSlot(slot: TimetableSlot) {
        dao.updateTimetableSlot(
            TimetableSlotEntity(
                id = slot.id,
                subjectId = slot.subjectId,
                dayOfWeek = slot.dayOfWeek,
                startTime = slot.startTime,
                endTime = slot.endTime,
                room = slot.room
            )
        )
    }

    suspend fun deleteTimetableSlot(slot: TimetableSlot) {
        dao.deleteTimetableSlot(
            TimetableSlotEntity(
                id = slot.id,
                subjectId = slot.subjectId,
                dayOfWeek = slot.dayOfWeek,
                startTime = slot.startTime,
                endTime = slot.endTime,
                room = slot.room
            )
        )
    }
}
