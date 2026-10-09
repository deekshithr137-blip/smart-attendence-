package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.Course
import com.example.data.CourseDao
import com.example.data.CourseDatabase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  private lateinit var db: CourseDatabase
  private lateinit var courseDao: CourseDao

  @Before
  fun createDb() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    db = Room.inMemoryDatabaseBuilder(context, CourseDatabase::class.java)
      .allowMainThreadQueries()
      .build()
    courseDao = db.courseDao()
  }

  @After
  fun closeDb() {
    db.close()
  }

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Smart Attendance Predictor", appName)
  }

  @Test
  fun `course database links course entity and course dao successfully`() = runBlocking {
    val course = Course(
      id = 1L,
      courseName = "Algorithms & Data Structures",
      currentAttendance = 18,
      totalClasses = 20,
      thresholdPercentage = 75.0,
      courseCode = "CS-201",
      instructor = "Dr. Turing",
      credits = 4
    )

    // Insert course via DAO
    courseDao.insertCourse(course)

    // Retrieve single course
    val retrieved = courseDao.getCourse(1L)
    assertNotNull(retrieved)
    assertEquals("Algorithms & Data Structures", retrieved?.courseName)
    assertEquals(18, retrieved?.currentAttendance)
    assertEquals(20, retrieved?.totalClasses)
    assertEquals(75.0, retrieved?.thresholdPercentage ?: 0.0, 0.01)
    assertEquals(90.0, retrieved?.percentage ?: 0.0, 0.01)
    assertTrue(retrieved?.isCompliant == true)

    // Observe flow
    val allCourses = courseDao.getAllCourses().first()
    assertEquals(1, allCourses.size)

    // Mark present
    courseDao.markPresent(1L)
    val afterPresent = courseDao.getCourse(1L)
    assertEquals(19, afterPresent?.currentAttendance)
    assertEquals(21, afterPresent?.totalClasses)
  }
}
