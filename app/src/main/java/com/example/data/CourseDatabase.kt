package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Room Database abstract class linking the [Course] entity and [CourseDao].
 *
 * Ensures thread-safe singleton access for the application to persist course
 * attendance tracking records.
 */
@Database(
    entities = [Course::class],
    version = 1,
    exportSchema = false
)
abstract class CourseDatabase : RoomDatabase() {

    abstract fun courseDao(): CourseDao

    companion object {
        @Volatile
        private var INSTANCE: CourseDatabase? = null

        fun getDatabase(context: Context): CourseDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CourseDatabase::class.java,
                    "course_attendance_db"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(DatabaseCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        populateInitialCourses(database.courseDao())
                    }
                }
            }

            private suspend fun populateInitialCourses(dao: CourseDao) {
                val initialCourses = listOf(
                    Course(
                        id = 1,
                        courseName = "Distributed Systems",
                        currentAttendance = 32,
                        totalClasses = 36,
                        thresholdPercentage = 75.0,
                        courseCode = "CS-401",
                        instructor = "Dr. Alan Mercer",
                        credits = 4
                    ),
                    Course(
                        id = 2,
                        courseName = "Machine Learning",
                        currentAttendance = 27,
                        totalClasses = 34,
                        thresholdPercentage = 75.0,
                        courseCode = "CS-402",
                        instructor = "Prof. Elena Rostov",
                        credits = 4
                    ),
                    Course(
                        id = 3,
                        courseName = "Computer Networks",
                        currentAttendance = 22,
                        totalClasses = 30,
                        thresholdPercentage = 75.0,
                        courseCode = "CS-403",
                        instructor = "Dr. K. Vance",
                        credits = 3
                    ),
                    Course(
                        id = 4,
                        courseName = "Database Internals",
                        currentAttendance = 19,
                        totalClasses = 29,
                        thresholdPercentage = 75.0,
                        courseCode = "CS-404",
                        instructor = "Prof. Marcus Brody",
                        credits = 3
                    ),
                    Course(
                        id = 5,
                        courseName = "Cloud Architecture",
                        currentAttendance = 28,
                        totalClasses = 32,
                        thresholdPercentage = 75.0,
                        courseCode = "CS-405",
                        instructor = "Dr. Sarah Chen",
                        credits = 3
                    ),
                    Course(
                        id = 6,
                        courseName = "Mobile Computing Lab",
                        currentAttendance = 11,
                        totalClasses = 14,
                        thresholdPercentage = 80.0,
                        courseCode = "CS-406L",
                        instructor = "Eng. Liam Thorne",
                        credits = 2
                    )
                )
                dao.insertAll(initialCourses)
            }
        }
    }
}
