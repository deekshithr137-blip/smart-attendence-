package com.example

import com.example.data.Course
import com.example.model.AttendanceTier
import com.example.model.Subject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

  @Test
  fun testCourseEntityCalculations() {
    // Test Course Room Entity creation and properties
    val course = Course(
      id = 10,
      courseName = "Operating Systems",
      currentAttendance = 24,
      totalClasses = 30,
      thresholdPercentage = 75.0,
      courseCode = "CS-301",
      instructor = "Prof. Davis",
      credits = 4
    )

    assertEquals("Operating Systems", course.courseName)
    assertEquals(24, course.currentAttendance)
    assertEquals(30, course.totalClasses)
    assertEquals(75.0, course.thresholdPercentage, 0.001)

    // 24 / 30 = 80.0%
    assertEquals(80.0, course.percentage, 0.001)
    assertTrue(course.isCompliant)

    // 24 / (30 + m) >= 0.75 => 30 + m <= 32 => m = 2
    assertEquals(2, course.canMissClasses)
    assertEquals(0, course.mustAttendClasses)
  }

  @Test
  fun testCourseEntityBelowThreshold() {
    val courseBelow = Course(
      id = 11,
      courseName = "Compiler Design",
      currentAttendance = 14,
      totalClasses = 22,
      thresholdPercentage = 75.0
    )

    // 14 / 22 = 63.63%
    assertEquals(63.636, courseBelow.percentage, 0.01)
    assertFalse(courseBelow.isCompliant)
    assertEquals(0, courseBelow.canMissClasses)

    // (14 + c) / (22 + c) >= 0.75 => 14 + c >= 16.5 + 0.75c => 0.25c >= 2.5 => c >= 10
    assertEquals(10, courseBelow.mustAttendClasses)
  }

  @Test
  fun testSafeSubjectCalculations() {
    // 32 attended out of 36 total with 75% target -> 88.88%
    val subject = Subject(
      id = 1,
      name = "Distributed Systems",
      code = "CS-401",
      attendedClasses = 32,
      totalClasses = 36,
      targetPercentage = 75.0
    )

    assertEquals(AttendanceTier.SAFE, subject.tier)
    assertTrue(subject.percentage >= 75.0)

    // Can miss classes calculation:
    // With 32 attended: 32 / (36 + m) >= 0.75 => 36 + m <= 42.66 => m <= 6.
    assertEquals(6, subject.canMissClasses)
    assertEquals(0, subject.mustAttendClasses)
  }

  @Test
  fun testCriticalSubjectCalculations() {
    // 19 attended out of 29 total with 75% target -> 65.5% (Critical tier)
    val subject = Subject(
      id = 2,
      name = "Database Internals",
      code = "CS-404",
      attendedClasses = 19,
      totalClasses = 29,
      targetPercentage = 75.0
    )

    assertEquals(AttendanceTier.CRITICAL, subject.tier)
    assertEquals(0, subject.canMissClasses)

    // Must attend consecutive classes:
    // (19 + c) / (29 + c) >= 0.75 => 19 + c >= 21.75 + 0.75c => 0.25c >= 2.75 => c >= 11
    assertEquals(11, subject.mustAttendClasses)
  }

  @Test
  fun testCautionSubjectCalculations() {
    // 22 attended out of 30 total with 75% target -> 73.3% (Caution tier)
    val subject = Subject(
      id = 3,
      name = "Computer Networks",
      code = "CS-403",
      attendedClasses = 22,
      totalClasses = 30,
      targetPercentage = 75.0
    )

    assertEquals(AttendanceTier.CAUTION, subject.tier)
    assertEquals(0, subject.canMissClasses)

    // (22 + c) / (30 + c) >= 0.75 => 0.25c >= 0.5 => c >= 2
    assertEquals(2, subject.mustAttendClasses)
  }
}
