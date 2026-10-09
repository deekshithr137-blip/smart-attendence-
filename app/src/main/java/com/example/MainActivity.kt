package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.AutoGraph
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.AppDatabase
import com.example.data.AttendanceRepository
import com.example.ui.components.AddOrEditSlotDialog
import com.example.ui.components.AddOrEditSubjectDialog
import com.example.ui.components.StudentProfileDialog
import com.example.ui.screens.AppAssistantScreen
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.SimulatorScreen
import com.example.ui.screens.SubjectDetailScreen
import com.example.ui.screens.TimetableScreen
import com.example.ui.theme.CardBase
import com.example.ui.theme.CockpitCanvas
import com.example.ui.theme.GhostBorder
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.OutfitFontFamily
import com.example.ui.theme.PrimaryElectricViolet
import com.example.ui.theme.SecondaryTeal
import com.example.viewmodel.AttendanceViewModel
import com.example.viewmodel.AttendanceViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainApp()
            }
        }
    }
}

enum class AppTab(val label: String) {
    DASHBOARD("Dashboard"),
    SIMULATOR("Simulator"),
    SCHEDULE("Schedule"),
    ASSISTANT("AI Guide")
}

@Composable
fun MainApp() {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val database = remember { AppDatabase.getDatabase(context, coroutineScope) }
    val repository = remember { AttendanceRepository(database.attendanceDao()) }
    val viewModel: AttendanceViewModel = viewModel(
        factory = AttendanceViewModelFactory(repository)
    )

    var currentTab by remember { mutableStateOf(AppTab.DASHBOARD) }
    var selectedSubjectIdForDetail by remember { mutableStateOf<Long?>(null) }

    val subjects by viewModel.subjects.collectAsState()
    val isAddDialogOpen by viewModel.isAddDialogOpen.collectAsState()
    val editingSubject by viewModel.editingSubject.collectAsState()

    val isSlotDialogOpen by viewModel.isSlotDialogOpen.collectAsState()
    val editingSlot by viewModel.editingSlot.collectAsState()

    val isAuthenticated by viewModel.isAuthenticated.collectAsState()

    if (!isAuthenticated) {
        AuthScreen(viewModel = viewModel)
    } else {
        // Handle back button when viewing subject detail
        BackHandler(enabled = selectedSubjectIdForDetail != null) {
            selectedSubjectIdForDetail = null
        }

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = CockpitCanvas,
            contentWindowInsets = WindowInsets.navigationBars,
            bottomBar = {
                if (selectedSubjectIdForDetail == null) {
                    NavigationBar(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp))
                            .border(1.dp, GhostBorder, RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp))
                            .windowInsetsPadding(WindowInsets.navigationBars)
                            .testTag("main_navigation_bar"),
                        containerColor = CardBase,
                        tonalElevation = 8.dp
                    ) {
                        // Dashboard Tab
                        NavigationBarItem(
                            selected = currentTab == AppTab.DASHBOARD,
                            onClick = { currentTab = AppTab.DASHBOARD },
                            icon = {
                                Icon(
                                    imageVector = if (currentTab == AppTab.DASHBOARD) Icons.Filled.Dashboard else Icons.Outlined.Dashboard,
                                    contentDescription = "Dashboard",
                                    modifier = Modifier.size(22.dp)
                                )
                            },
                            label = {
                                Text(
                                    text = "Overview",
                                    fontFamily = OutfitFontFamily,
                                    fontWeight = if (currentTab == AppTab.DASHBOARD) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 11.sp
                                )
                            },
                            colors = navigationItemColors(),
                            modifier = Modifier.testTag("nav_tab_dashboard")
                        )

                        // Simulator Tab
                        NavigationBarItem(
                            selected = currentTab == AppTab.SIMULATOR,
                            onClick = { currentTab = AppTab.SIMULATOR },
                            icon = {
                                Icon(
                                    imageVector = if (currentTab == AppTab.SIMULATOR) Icons.Filled.AutoGraph else Icons.Outlined.AutoGraph,
                                    contentDescription = "Simulator",
                                    modifier = Modifier.size(22.dp)
                                )
                            },
                            label = {
                                Text(
                                    text = "What-If",
                                    fontFamily = OutfitFontFamily,
                                    fontWeight = if (currentTab == AppTab.SIMULATOR) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 11.sp
                                )
                            },
                            colors = navigationItemColors(),
                            modifier = Modifier.testTag("nav_tab_simulator")
                        )

                        // Schedule / Timetable Tab
                        NavigationBarItem(
                            selected = currentTab == AppTab.SCHEDULE,
                            onClick = { currentTab = AppTab.SCHEDULE },
                            icon = {
                                Icon(
                                    imageVector = if (currentTab == AppTab.SCHEDULE) Icons.Filled.CalendarMonth else Icons.Outlined.CalendarMonth,
                                    contentDescription = "Schedule",
                                    modifier = Modifier.size(22.dp)
                                )
                            },
                            label = {
                                Text(
                                    text = "Timetable",
                                    fontFamily = OutfitFontFamily,
                                    fontWeight = if (currentTab == AppTab.SCHEDULE) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 11.sp
                                )
                            },
                            colors = navigationItemColors(),
                            modifier = Modifier.testTag("nav_tab_schedule")
                        )

                        // AI Assistant & App Guide Tab
                        NavigationBarItem(
                            selected = currentTab == AppTab.ASSISTANT,
                            onClick = { currentTab = AppTab.ASSISTANT },
                            icon = {
                                Icon(
                                    imageVector = if (currentTab == AppTab.ASSISTANT) Icons.Filled.AutoAwesome else Icons.Outlined.AutoAwesome,
                                    contentDescription = "AI Guide",
                                    modifier = Modifier.size(22.dp)
                                )
                            },
                            label = {
                                Text(
                                    text = "AI Guide",
                                    fontFamily = OutfitFontFamily,
                                    fontWeight = if (currentTab == AppTab.ASSISTANT) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 11.sp
                                )
                            },
                            colors = navigationItemColors(),
                            modifier = Modifier.testTag("nav_tab_assistant")
                        )
                    }
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                val detailId = selectedSubjectIdForDetail
                if (detailId != null) {
                    SubjectDetailScreen(
                        subjectId = detailId,
                        viewModel = viewModel,
                        onBack = { selectedSubjectIdForDetail = null },
                        onNavigateToSimulator = { subId ->
                            viewModel.setSimulationSubject(subId)
                            selectedSubjectIdForDetail = null
                            currentTab = AppTab.SIMULATOR
                        }
                    )
                } else {
                    when (currentTab) {
                        AppTab.DASHBOARD -> {
                            DashboardScreen(
                                viewModel = viewModel,
                                onNavigateToSimulator = { subId ->
                                    viewModel.setSimulationSubject(subId)
                                    currentTab = AppTab.SIMULATOR
                                },
                                onSelectSubject = { subject ->
                                    selectedSubjectIdForDetail = subject.id
                                }
                            )
                        }
                        AppTab.SIMULATOR -> {
                            SimulatorScreen(viewModel = viewModel)
                        }
                        AppTab.SCHEDULE -> {
                            TimetableScreen(viewModel = viewModel)
                        }
                        AppTab.ASSISTANT -> {
                            AppAssistantScreen(viewModel = viewModel)
                        }
                    }
                }
            }
        }

        // Add / Edit Subject Modal Dialog
        if (isAddDialogOpen) {
            AddOrEditSubjectDialog(
                initialSubject = editingSubject,
                onDismiss = { viewModel.closeAddDialog() },
                onSave = { subject -> viewModel.saveSubject(subject) }
            )
        }

        // Add / Edit Timetable Slot Modal Dialog
        if (isSlotDialogOpen) {
            AddOrEditSlotDialog(
                initialSlot = editingSlot,
                subjects = subjects,
                onDismiss = { viewModel.closeSlotDialog() },
                onSave = { slot -> viewModel.saveTimetableSlot(slot) },
                onDelete = { slot -> viewModel.deleteTimetableSlot(slot) }
            )
        }

        // Student Profile Modal Dialog
        val isProfileDialogOpen by viewModel.isProfileDialogOpen.collectAsState()
        val userProfile by viewModel.userProfile.collectAsState()
        val overallStats by viewModel.overallStats.collectAsState()
        val totalCredits = subjects.sumOf { it.credits }

        if (isProfileDialogOpen) {
            StudentProfileDialog(
                profile = userProfile,
                overallStats = overallStats,
                totalCredits = totalCredits,
                onDismiss = { viewModel.closeProfileDialog() },
                onSave = { updatedProfile -> viewModel.updateUserProfile(updatedProfile) },
                onLogout = { viewModel.logout() }
            )
        }
    }
}

@Composable
private fun navigationItemColors() = NavigationBarItemDefaults.colors(
    selectedIconColor = PrimaryElectricViolet,
    unselectedIconColor = Color(0xFF94A3B8),
    selectedTextColor = PrimaryElectricViolet,
    unselectedTextColor = Color(0xFF94A3B8),
    indicatorColor = PrimaryElectricViolet.copy(alpha = 0.15f)
)
