package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CardBase
import com.example.ui.theme.CockpitCanvas
import com.example.ui.theme.GhostBorder
import com.example.ui.theme.OutfitFontFamily
import com.example.ui.theme.PrimaryElectricViolet
import com.example.ui.theme.SecondaryTeal
import com.example.ui.theme.TertiaryIndigo
import com.example.ui.theme.TierCriticalBg
import com.example.ui.theme.TierCriticalBorder
import com.example.ui.theme.TierCriticalRose
import com.example.ui.theme.TierSafeEmerald
import com.example.viewmodel.AttendanceViewModel

enum class AuthMode {
    SIGN_IN,
    REGISTER
}

@Composable
fun AuthScreen(
    viewModel: AttendanceViewModel,
    modifier: Modifier = Modifier
) {
    var mode by remember { mutableStateOf(AuthMode.SIGN_IN) }
    val authError by viewModel.authError.collectAsState()
    val focusManager = LocalFocusManager.current

    // Sign In states
    var signInIdentifier by remember { mutableStateOf("CS23B1042") }
    var signInPassword by remember { mutableStateOf("password123") }
    var isPasswordVisible by remember { mutableStateOf(false) }

    // Register states
    var registerName by remember { mutableStateOf("") }
    var registerStudentId by remember { mutableStateOf("") }
    var registerEmail by remember { mutableStateOf("") }
    var registerMajor by remember { mutableStateOf("Computer Science & Engineering") }
    var registerSemester by remember { mutableStateOf("Semester 6") }
    var registerTargetGoal by remember { mutableDoubleStateOf(75.0) }
    var registerPassword by remember { mutableStateOf("") }
    var registerConfirmPassword by remember { mutableStateOf("") }
    var isRegisterPasswordVisible by remember { mutableStateOf(false) }
    var localValidationMsg by remember { mutableStateOf<String?>(null) }

    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedContainerColor = Color(0xFF0F172A),
        unfocusedContainerColor = Color(0xFF0F172A),
        focusedBorderColor = PrimaryElectricViolet,
        unfocusedBorderColor = Color(0x33FFFFFF),
        focusedTextColor = Color.White,
        unfocusedTextColor = Color.White,
        focusedLabelColor = SecondaryTeal,
        unfocusedLabelColor = Color(0xFF94A3B8),
        focusedLeadingIconColor = PrimaryElectricViolet,
        unfocusedLeadingIconColor = Color(0xFF94A3B8),
        focusedTrailingIconColor = SecondaryTeal,
        unfocusedTrailingIconColor = Color(0xFF94A3B8)
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(CockpitCanvas)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            contentPadding = PaddingValues(top = 40.dp, bottom = 48.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // App Branding Hero Header
            item {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(68.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(PrimaryElectricViolet, TertiaryIndigo)
                                )
                            )
                            .border(1.5.dp, SecondaryTeal.copy(alpha = 0.5f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.School,
                            contentDescription = "App Icon",
                            tint = Color.White,
                            modifier = Modifier.size(34.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Smart Attendance",
                        fontFamily = OutfitFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 26.sp,
                        color = Color.White
                    )

                    Text(
                        text = "Predictor & Command Center",
                        fontFamily = OutfitFontFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp,
                        color = SecondaryTeal
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Track thresholds, calculate safe bunks, and simulate what-if scenarios",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF94A3B8),
                        modifier = Modifier.padding(horizontal = 16.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }

            // Segmented Auth Mode Switcher
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .border(1.dp, GhostBorder, RoundedCornerShape(12.dp)),
                    color = CardBase
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp)
                    ) {
                        // Sign In tab
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    if (mode == AuthMode.SIGN_IN) PrimaryElectricViolet else Color.Transparent
                                )
                                .clickable {
                                    mode = AuthMode.SIGN_IN
                                    localValidationMsg = null
                                    viewModel.clearAuthError()
                                }
                                .padding(vertical = 10.dp)
                                .testTag("auth_tab_signin"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Sign In",
                                fontFamily = OutfitFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = if (mode == AuthMode.SIGN_IN) Color.White else Color(0xFF94A3B8)
                            )
                        }

                        // Register tab
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    if (mode == AuthMode.REGISTER) PrimaryElectricViolet else Color.Transparent
                                )
                                .clickable {
                                    mode = AuthMode.REGISTER
                                    localValidationMsg = null
                                    viewModel.clearAuthError()
                                }
                                .padding(vertical = 10.dp)
                                .testTag("auth_tab_register"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Create Account",
                                fontFamily = OutfitFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = if (mode == AuthMode.REGISTER) Color.White else Color(0xFF94A3B8)
                            )
                        }
                    }
                }
            }

            // Error Message Banner (if any)
            val activeError = authError ?: localValidationMsg
            if (activeError != null) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(TierCriticalBg)
                            .border(1.dp, TierCriticalBorder, RoundedCornerShape(8.dp))
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ErrorOutline,
                            contentDescription = "Error",
                            tint = TierCriticalRose,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = activeError,
                            style = MaterialTheme.typography.bodySmall,
                            color = TierCriticalRose,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // Sign In Form
            if (mode == AuthMode.SIGN_IN) {
                item {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .border(1.dp, GhostBorder, RoundedCornerShape(16.dp)),
                        color = CardBase
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Text(
                                text = "Student Login",
                                fontFamily = OutfitFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = Color.White
                            )

                            // Student ID / Email
                            OutlinedTextField(
                                value = signInIdentifier,
                                onValueChange = {
                                    signInIdentifier = it
                                    localValidationMsg = null
                                },
                                label = { Text("Roll Number or University Email") },
                                placeholder = { Text("e.g. CS23B1042 or alex@university.edu") },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Badge,
                                        contentDescription = null
                                    )
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("signin_identifier_input"),
                                shape = RoundedCornerShape(10.dp),
                                colors = textFieldColors,
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Email,
                                    imeAction = ImeAction.Next
                                )
                            )

                            // Password
                            OutlinedTextField(
                                value = signInPassword,
                                onValueChange = {
                                    signInPassword = it
                                    localValidationMsg = null
                                },
                                label = { Text("Password") },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = null
                                    )
                                },
                                trailingIcon = {
                                    IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                                        Icon(
                                            imageVector = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                            contentDescription = if (isPasswordVisible) "Hide password" else "Show password"
                                        )
                                    }
                                },
                                visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("signin_password_input"),
                                shape = RoundedCornerShape(10.dp),
                                colors = textFieldColors,
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Password,
                                    imeAction = ImeAction.Done
                                ),
                                keyboardActions = KeyboardActions(onDone = {
                                    focusManager.clearFocus()
                                    viewModel.login(signInIdentifier, signInPassword)
                                })
                            )

                            // Sign In Button
                            Button(
                                onClick = {
                                    focusManager.clearFocus()
                                    viewModel.login(signInIdentifier, signInPassword)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("signin_submit_btn"),
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryElectricViolet),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text = "Sign In",
                                        fontFamily = OutfitFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = Color.White
                                    )
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }

                            // Quick Demo Sign-in Shortcut Card
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color(0xFF0F172A))
                                    .border(1.dp, SecondaryTeal.copy(alpha = 0.35f), RoundedCornerShape(10.dp))
                                    .clickable {
                                        viewModel.quickSignIn()
                                    }
                                    .padding(12.dp)
                                    .testTag("signin_demo_btn")
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(CircleShape)
                                            .background(SecondaryTeal.copy(alpha = 0.2f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.FlashOn,
                                            contentDescription = null,
                                            tint = SecondaryTeal,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                    Column {
                                        Text(
                                            text = "Quick Demo Student Sign-In",
                                            fontFamily = OutfitFontFamily,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = Color.White
                                        )
                                        Text(
                                            text = "Instant 1-tap access with preloaded courses (Alex Chen)",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color(0xFF94A3B8),
                                            fontSize = 11.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Register / Create Account Form
            if (mode == AuthMode.REGISTER) {
                item {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .border(1.dp, GhostBorder, RoundedCornerShape(16.dp)),
                        color = CardBase
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Text(
                                text = "New Student Registration",
                                fontFamily = OutfitFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = Color.White
                            )

                            // Full Name
                            OutlinedTextField(
                                value = registerName,
                                onValueChange = {
                                    registerName = it
                                    localValidationMsg = null
                                },
                                label = { Text("Full Name") },
                                placeholder = { Text("e.g. Jordan Smith") },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = null
                                    )
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("register_name_input"),
                                shape = RoundedCornerShape(10.dp),
                                colors = textFieldColors,
                                singleLine = true
                            )

                            // Roll Number / Student ID
                            OutlinedTextField(
                                value = registerStudentId,
                                onValueChange = {
                                    registerStudentId = it
                                    localValidationMsg = null
                                },
                                label = { Text("Student ID / Roll Number") },
                                placeholder = { Text("e.g. CS24B1055") },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Badge,
                                        contentDescription = null
                                    )
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("register_id_input"),
                                shape = RoundedCornerShape(10.dp),
                                colors = textFieldColors,
                                singleLine = true
                            )

                            // University Email
                            OutlinedTextField(
                                value = registerEmail,
                                onValueChange = {
                                    registerEmail = it
                                    localValidationMsg = null
                                },
                                label = { Text("University Email") },
                                placeholder = { Text("e.g. jordan@university.edu") },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Email,
                                        contentDescription = null
                                    )
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("register_email_input"),
                                shape = RoundedCornerShape(10.dp),
                                colors = textFieldColors,
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                            )

                            // Major / Branch
                            OutlinedTextField(
                                value = registerMajor,
                                onValueChange = { registerMajor = it },
                                label = { Text("Major / Department") },
                                placeholder = { Text("e.g. Computer Science") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("register_major_input"),
                                shape = RoundedCornerShape(10.dp),
                                colors = textFieldColors,
                                singleLine = true
                            )

                            // Semester / Year
                            OutlinedTextField(
                                value = registerSemester,
                                onValueChange = { registerSemester = it },
                                label = { Text("Semester / Year") },
                                placeholder = { Text("e.g. Semester 6") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("register_semester_input"),
                                shape = RoundedCornerShape(10.dp),
                                colors = textFieldColors,
                                singleLine = true
                            )

                            // Target Attendance Goal Threshold Chips
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(
                                    text = "Attendance Target Goal: ${registerTargetGoal.toInt()}%",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = SecondaryTeal,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    listOf(75.0, 80.0, 85.0).forEach { goal ->
                                        val isSelected = registerTargetGoal == goal
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(
                                                    if (isSelected) PrimaryElectricViolet.copy(alpha = 0.35f) else Color(0xFF0F172A)
                                                )
                                                .border(
                                                    1.dp,
                                                    if (isSelected) PrimaryElectricViolet else Color(0x33FFFFFF),
                                                    RoundedCornerShape(8.dp)
                                                )
                                                .clickable { registerTargetGoal = goal }
                                                .padding(vertical = 8.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = "${goal.toInt()}%",
                                                fontFamily = OutfitFontFamily,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                color = if (isSelected) Color.White else Color(0xFF94A3B8),
                                                fontSize = 13.sp
                                            )
                                        }
                                    }
                                }
                            }

                            // Password
                            OutlinedTextField(
                                value = registerPassword,
                                onValueChange = {
                                    registerPassword = it
                                    localValidationMsg = null
                                },
                                label = { Text("Password") },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = null
                                    )
                                },
                                trailingIcon = {
                                    IconButton(onClick = { isRegisterPasswordVisible = !isRegisterPasswordVisible }) {
                                        Icon(
                                            imageVector = if (isRegisterPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                            contentDescription = null
                                        )
                                    }
                                },
                                visualTransformation = if (isRegisterPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("register_password_input"),
                                shape = RoundedCornerShape(10.dp),
                                colors = textFieldColors,
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                            )

                            // Confirm Password
                            OutlinedTextField(
                                value = registerConfirmPassword,
                                onValueChange = {
                                    registerConfirmPassword = it
                                    localValidationMsg = null
                                },
                                label = { Text("Confirm Password") },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = null
                                    )
                                },
                                visualTransformation = if (isRegisterPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("register_confirm_password_input"),
                                shape = RoundedCornerShape(10.dp),
                                colors = textFieldColors,
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                            )

                            // Register Button
                            Button(
                                onClick = {
                                    focusManager.clearFocus()
                                    when {
                                        registerName.isBlank() -> localValidationMsg = "Please enter your name"
                                        registerStudentId.isBlank() -> localValidationMsg = "Please enter your student ID"
                                        registerEmail.isBlank() -> localValidationMsg = "Please enter your email"
                                        registerPassword.length < 4 -> localValidationMsg = "Password must be at least 4 characters"
                                        registerPassword != registerConfirmPassword -> localValidationMsg = "Passwords do not match"
                                        else -> {
                                            viewModel.register(
                                                name = registerName,
                                                studentId = registerStudentId,
                                                email = registerEmail,
                                                major = registerMajor,
                                                semester = registerSemester,
                                                targetGoal = registerTargetGoal,
                                                password = registerPassword
                                            )
                                        }
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("register_submit_btn"),
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryElectricViolet),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text(
                                    text = "Create Account & Enter",
                                    fontFamily = OutfitFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }

            // Guest Mode Shortcut Footer
            item {
                TextButton(
                    onClick = {
                        focusManager.clearFocus()
                        viewModel.quickSignIn()
                    },
                    modifier = Modifier.testTag("guest_signin_btn")
                ) {
                    Text(
                        text = "Continue as Guest Student →",
                        fontFamily = OutfitFontFamily,
                        color = Color(0xFF94A3B8),
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}
