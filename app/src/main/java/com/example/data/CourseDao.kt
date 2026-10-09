package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface CourseDao {
    @Query("SELECT * FROM courses ORDER BY id ASC")
    fun getAllCourses(): Flow<List<Course>>

    @Query("SELECT * FROM courses WHERE id = :id LIMIT 1")
    fun getCourseById(id: Long): Flow<Course?>

    @Query("SELECT * FROM courses WHERE id = :id LIMIT 1")
    suspend fun getCourse(id: Long): Course?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCourse(course: Course): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(courses: List<Course>): List<Long>

    @Update
    suspend fun updateCourse(course: Course)

    @Delete
    suspend fun deleteCourse(course: Course)

    @Query("UPDATE courses SET current_attendance = current_attendance + 1, total_classes = total_classes + 1 WHERE id = :courseId")
    suspend fun markPresent(courseId: Long)

    @Query("UPDATE courses SET total_classes = total_classes + 1 WHERE id = :courseId")
    suspend fun markAbsent(courseId: Long)
}
