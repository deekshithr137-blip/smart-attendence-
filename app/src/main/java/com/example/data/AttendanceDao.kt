package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface AttendanceDao {
    @Query("SELECT * FROM subjects ORDER BY id ASC")
    fun getAllSubjects(): Flow<List<SubjectEntity>>

    @Query("SELECT * FROM subjects WHERE id = :id LIMIT 1")
    suspend fun getSubjectById(id: Long): SubjectEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubject(subject: SubjectEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllSubjects(subjects: List<SubjectEntity>): List<Long>

    @Update
    suspend fun updateSubject(subject: SubjectEntity)

    @Delete
    suspend fun deleteSubject(subject: SubjectEntity)

    @Query("UPDATE subjects SET attendedClasses = attendedClasses + 1, totalClasses = totalClasses + 1 WHERE id = :subjectId")
    suspend fun markPresent(subjectId: Long)

    @Query("UPDATE subjects SET totalClasses = totalClasses + 1 WHERE id = :subjectId")
    suspend fun markAbsent(subjectId: Long)

    @Query("UPDATE subjects SET attendedClasses = CASE WHEN attendedClasses > 0 AND :wasPresent = 1 THEN attendedClasses - 1 ELSE attendedClasses END, totalClasses = CASE WHEN totalClasses > 0 THEN totalClasses - 1 ELSE 0 END WHERE id = :subjectId")
    suspend fun undoClass(subjectId: Long, wasPresent: Boolean)

    // Logs
    @Query("SELECT * FROM attendance_logs WHERE subjectId = :subjectId ORDER BY timestamp DESC")
    fun getLogsForSubject(subjectId: Long): Flow<List<AttendanceLogEntity>>

    @Query("SELECT * FROM attendance_logs ORDER BY timestamp DESC LIMIT 50")
    fun getRecentLogs(): Flow<List<AttendanceLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: AttendanceLogEntity): Long

    @Delete
    suspend fun deleteLog(log: AttendanceLogEntity)

    // Timetable
    @Query("SELECT * FROM timetable_slots ORDER BY dayOfWeek ASC, startTime ASC")
    fun getAllTimetableSlots(): Flow<List<TimetableSlotEntity>>

    @Query("SELECT * FROM timetable_slots WHERE dayOfWeek = :dayOfWeek ORDER BY startTime ASC")
    fun getSlotsForDay(dayOfWeek: Int): Flow<List<TimetableSlotEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTimetableSlot(slot: TimetableSlotEntity): Long

    @Update
    suspend fun updateTimetableSlot(slot: TimetableSlotEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllTimetableSlots(slots: List<TimetableSlotEntity>)

    @Delete
    suspend fun deleteTimetableSlot(slot: TimetableSlotEntity)
}
