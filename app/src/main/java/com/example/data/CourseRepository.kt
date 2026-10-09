package com.example.data

import kotlinx.coroutines.flow.Flow

/**
 * Repository abstracting data access for [Course] entities via [CourseDao].
 */
class CourseRepository(private val courseDao: CourseDao) {

    /**
     * Flow of all tracked courses observed reactively.
     */
    val allCourses: Flow<List<Course>> = courseDao.getAllCourses()

    /**
     * Flow observing a single course by its unique ID.
     */
    fun getCourseById(id: Long): Flow<Course?> = courseDao.getCourseById(id)

    /**
     * Suspend fetch for a single course.
     */
    suspend fun getCourse(id: Long): Course? = courseDao.getCourse(id)

    /**
     * Inserts a new course.
     */
    suspend fun insertCourse(course: Course): Long = courseDao.insertCourse(course)

    /**
     * Updates an existing course.
     */
    suspend fun updateCourse(course: Course) = courseDao.updateCourse(course)

    /**
     * Deletes a course.
     */
    suspend fun deleteCourse(course: Course) = courseDao.deleteCourse(course)

    /**
     * Marks one class present (increments attended and total classes).
     */
    suspend fun markPresent(courseId: Long) = courseDao.markPresent(courseId)

    /**
     * Marks one class absent (increments total classes only).
     */
    suspend fun markAbsent(courseId: Long) = courseDao.markAbsent(courseId)
}
