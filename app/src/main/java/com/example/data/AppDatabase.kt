package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        Course::class,
        SubjectEntity::class,
        AttendanceLogEntity::class,
        TimetableSlotEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun attendanceDao(): AttendanceDao
    abstract fun courseDao(): CourseDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return getDatabase(context, CoroutineScope(Dispatchers.IO))
        }

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "smart_attendance_db"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(DatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialData(database.attendanceDao(), database.courseDao())
                    }
                }
            }

            private suspend fun populateInitialData(dao: AttendanceDao, courseDao: CourseDao) {
                // Populate courses entity
                val courses = listOf(
                    Course(1, "Distributed Systems", 32, 36, 75.0, "CS-401", "Dr. Alan Mercer", 4),
                    Course(2, "Machine Learning", 27, 34, 75.0, "CS-402", "Prof. Elena Rostov", 4),
                    Course(3, "Computer Networks", 22, 30, 75.0, "CS-403", "Dr. K. Vance", 3),
                    Course(4, "Database Internals", 19, 29, 75.0, "CS-404", "Prof. Marcus Brody", 3),
                    Course(5, "Cloud Architecture", 28, 32, 75.0, "CS-405", "Dr. Sarah Chen", 3),
                    Course(6, "Mobile Computing Lab", 11, 14, 80.0, "CS-406L", "Eng. Liam Thorne", 2)
                )
                courseDao.insertAll(courses)

                // Populate realistic subjects spanning Safe, Caution, and Critical tiers
                val subjects = listOf(
                    SubjectEntity(
                        id = 1,
                        name = "Distributed Systems",
                        code = "CS-401",
                        professor = "Dr. Alan Mercer",
                        attendedClasses = 32,
                        totalClasses = 36,
                        targetPercentage = 75.0,
                        credits = 4,
                        colorHex = "#7C3AED" // Safe 88.9%
                    ),
                    SubjectEntity(
                        id = 2,
                        name = "Machine Learning",
                        code = "CS-402",
                        professor = "Prof. Elena Rostov",
                        attendedClasses = 27,
                        totalClasses = 34,
                        targetPercentage = 75.0,
                        credits = 4,
                        colorHex = "#0D9488" // Safe 79.4%
                    ),
                    SubjectEntity(
                        id = 3,
                        name = "Computer Networks",
                        code = "CS-403",
                        professor = "Dr. K. Vance",
                        attendedClasses = 22,
                        totalClasses = 30,
                        targetPercentage = 75.0,
                        credits = 3,
                        colorHex = "#F59E0B" // Caution 73.3%
                    ),
                    SubjectEntity(
                        id = 4,
                        name = "Database Internals",
                        code = "CS-404",
                        professor = "Prof. Marcus Brody",
                        attendedClasses = 19,
                        totalClasses = 29,
                        targetPercentage = 75.0,
                        credits = 3,
                        colorHex = "#F43F5E" // Critical 65.5%
                    ),
                    SubjectEntity(
                        id = 5,
                        name = "Cloud Architecture",
                        code = "CS-405",
                        professor = "Dr. Sarah Chen",
                        attendedClasses = 28,
                        totalClasses = 32,
                        targetPercentage = 75.0,
                        credits = 3,
                        colorHex = "#6366F1" // Safe 87.5%
                    ),
                    SubjectEntity(
                        id = 6,
                        name = "Mobile Computing Lab",
                        code = "CS-406L",
                        professor = "Eng. Liam Thorne",
                        attendedClasses = 11,
                        totalClasses = 14,
                        targetPercentage = 80.0,
                        credits = 2,
                        colorHex = "#29A195" // Caution 78.6% (target 80%)
                    )
                )
                dao.insertAllSubjects(subjects)

                // Populate timetable slots for weekdays
                val timetable = listOf(
                    // Monday (1)
                    TimetableSlotEntity(0, 1, 1, "09:00 AM", "10:15 AM", "Hall 302"),
                    TimetableSlotEntity(0, 2, 1, "10:30 AM", "11:45 AM", "Lab B4"),
                    TimetableSlotEntity(0, 3, 1, "01:30 PM", "02:45 PM", "Seminar Room 1"),

                    // Tuesday (2)
                    TimetableSlotEntity(0, 4, 2, "09:30 AM", "10:45 AM", "Auditorium C"),
                    TimetableSlotEntity(0, 5, 2, "11:00 AM", "12:15 PM", "Hall 201"),
                    TimetableSlotEntity(0, 6, 2, "02:00 PM", "04:30 PM", "Compute Lab 3"),

                    // Wednesday (3)
                    TimetableSlotEntity(0, 1, 3, "09:00 AM", "10:15 AM", "Hall 302"),
                    TimetableSlotEntity(0, 3, 3, "10:30 AM", "11:45 AM", "Seminar Room 1"),
                    TimetableSlotEntity(0, 2, 3, "01:00 PM", "02:15 PM", "Lab B4"),

                    // Thursday (4)
                    TimetableSlotEntity(0, 4, 4, "09:30 AM", "10:45 AM", "Auditorium C"),
                    TimetableSlotEntity(0, 5, 4, "11:00 AM", "12:15 PM", "Hall 201"),

                    // Friday (5)
                    TimetableSlotEntity(0, 2, 5, "09:00 AM", "10:15 AM", "Lab B4"),
                    TimetableSlotEntity(0, 1, 5, "10:30 AM", "11:45 AM", "Hall 302"),
                    TimetableSlotEntity(0, 3, 5, "01:30 PM", "02:45 PM", "Seminar Room 1")
                )
                dao.insertAllTimetableSlots(timetable)
            }
        }
    }
}
