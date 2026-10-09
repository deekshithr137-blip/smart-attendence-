package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.collectAsState
import com.example.model.AttendanceTier
import com.example.model.Subject
import com.example.ui.theme.CardBase
import com.example.ui.theme.GhostBorder
import com.example.ui.theme.GhostBorderSubtle
import com.example.ui.theme.OutfitFontFamily
import com.example.ui.theme.PrimaryElectricViolet
import com.example.ui.theme.SecondaryTeal
import com.example.ui.theme.TertiaryIndigo
import com.example.viewmodel.AttendanceViewModel
import java.util.Locale

@Composable
fun ProfileAvatarButton(
    viewModel: AttendanceViewModel,
    modifier: Modifier = Modifier,
    size: Dp = 38.dp
) {
    val userProfile by viewModel.userProfile.collectAsState()
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(
                Brush.linearGradient(
                    listOf(PrimaryElectricViolet, TertiaryIndigo)
                )
            )
            .border(1.5.dp, Color.White.copy(alpha = 0.35f), CircleShape)
            .clickable { viewModel.openProfileDialog() }
            .testTag("profile_avatar_top_right"),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = userProfile.initials,
            fontFamily = OutfitFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = if (size > 36.dp) 14.sp else 12.sp,
            color = Color.White
        )
    }
}

@Composable
fun StatusCapsule(
    tier: AttendanceTier,
    modifier: Modifier = Modifier,
    customText: String? = null
) {
    val text = customText ?: tier.label
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(tier.backgroundColor)
            .border(1.dp, tier.borderColor, CircleShape)
            .padding(horizontal = 10.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(tier.textColor)
            )
            Text(
                text = text,
                color = tier.textColor,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun AttendanceCircularGauge(
    percentage: Double,
    tier: AttendanceTier,
    modifier: Modifier = Modifier,
    size: Dp = 80.dp,
    strokeWidth: Dp = 7.dp,
    showLabel: Boolean = true
) {
    val animatedProgress by animateFloatAsState(
        targetValue = (percentage / 100.0).toFloat().coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 800),
        label = "gauge_progress"
    )

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val strokePx = strokeWidth.toPx()
            val diameter = this.size.minDimension - strokePx
            val topLeft = Offset(strokePx / 2, strokePx / 2)
            val arcSize = Size(diameter, diameter)

            // Background track
            drawArc(
                color = Color(0x1AFFFFFF),
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokePx, cap = StrokeCap.Round)
            )

            // Active track
            if (animatedProgress > 0f) {
                drawArc(
                    brush = Brush.sweepGradient(
                        0f to tier.textColor.copy(alpha = 0.85f),
                        0.5f to tier.textColor,
                        1f to tier.textColor
                    ),
                    startAngle = -90f,
                    sweepAngle = 360f * animatedProgress,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = strokePx, cap = StrokeCap.Round)
                )
            }
        }

        if (showLabel) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = String.format(Locale.US, "%.0f%%", percentage),
                    fontFamily = OutfitFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = if (size > 90.dp) 20.sp else 15.sp,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun PrimaryGradientButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: @Composable (() -> Unit)? = null,
    enabled: Boolean = true
) {
    val gradientBrush = Brush.linearGradient(
        colors = listOf(PrimaryElectricViolet, TertiaryIndigo)
    )

    Box(
        modifier = modifier
            .height(44.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(if (enabled) gradientBrush else Brush.linearGradient(listOf(Color(0xFF334155), Color(0xFF334155))))
            .border(1.dp, GhostBorderSubtle, RoundedCornerShape(8.dp))
            .clickable(
                enabled = enabled,
                onClick = onClick,
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(color = Color.White.copy(alpha = 0.3f))
            )
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (icon != null) {
                icon()
            }
            Text(
                text = text,
                fontFamily = OutfitFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                color = Color.White
            )
        }
    }
}

@Composable
fun SecondaryDarkButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: @Composable (() -> Unit)? = null
) {
    Box(
        modifier = modifier
            .height(44.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(CardBase)
            .border(1.dp, GhostBorder, RoundedCornerShape(8.dp))
            .clickable(
                onClick = onClick,
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(color = SecondaryTeal.copy(alpha = 0.2f))
            )
            .padding(horizontal = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            if (icon != null) {
                icon()
            }
            Text(
                text = text,
                fontFamily = OutfitFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                color = SecondaryTeal
            )
        }
    }
}

@Composable
fun SubjectCard(
    subject: Subject,
    onMarkPresent: () -> Unit,
    onMarkAbsent: () -> Unit,
    onSimulate: () -> Unit,
    onClick: () -> Unit,
    onEdit: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, GhostBorder, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .testTag("subject_card_${subject.id}"),
        color = CardBase,
        tonalElevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header row: Subject info & status capsule & edit action
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = subject.code,
                            style = MaterialTheme.typography.labelSmall,
                            color = SecondaryTeal,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "• ${subject.credits} Credits",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF94A3B8)
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = subject.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontFamily = OutfitFontFamily,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (subject.professor.isNotBlank()) {
                        Text(
                            text = subject.professor,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF94A3B8),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    StatusCapsule(tier = subject.tier)
                    if (onEdit != null) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFF334155))
                                .clickable(onClick = onEdit)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                .testTag("edit_subject_btn_${subject.id}")
                        ) {
                            Text(
                                text = "Edit",
                                fontFamily = OutfitFontFamily,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 11.sp,
                                color = SecondaryTeal
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Metrics row: Gauge + stats
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                AttendanceCircularGauge(
                    percentage = subject.percentage,
                    tier = subject.tier,
                    size = 72.dp,
                    strokeWidth = 6.dp
                )

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Attended Ratio",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF94A3B8)
                        )
                        Text(
                            text = "${subject.attendedClasses}/${subject.totalClasses} Classes",
                            fontFamily = OutfitFontFamily,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White,
                            fontSize = 14.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Predictive advice banner
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(subject.tier.backgroundColor.copy(alpha = 0.08f))
                            .border(1.dp, subject.tier.borderColor.copy(alpha = 0.25f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 6.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = when (subject.tier) {
                                    AttendanceTier.SAFE -> Icons.Default.Check
                                    AttendanceTier.CAUTION -> Icons.Default.Warning
                                    AttendanceTier.CRITICAL -> Icons.Default.Warning
                                },
                                contentDescription = null,
                                tint = subject.tier.textColor,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = subject.predictionAdvice,
                                style = MaterialTheme.typography.bodySmall,
                                color = subject.tier.textColor,
                                fontSize = 11.5.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action row: + Present, + Absent, Simulate
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Quick + Present
                Button(
                    onClick = onMarkPresent,
                    modifier = Modifier
                        .weight(1f)
                        .height(38.dp)
                        .testTag("present_button_${subject.id}"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0x1F10B981),
                        contentColor = Color(0xFF10B981)
                    ),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Present",
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "+ Present",
                            fontFamily = OutfitFontFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp
                        )
                    }
                }

                // Quick + Absent
                Button(
                    onClick = onMarkAbsent,
                    modifier = Modifier
                        .weight(1f)
                        .height(38.dp)
                        .testTag("absent_button_${subject.id}"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0x1FF43F5E),
                        contentColor = Color(0xFFF43F5E)
                    ),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Absent",
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "+ Absent",
                            fontFamily = OutfitFontFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp
                        )
                    }
                }

                // Simulate sandbox trigger
                Button(
                    onClick = onSimulate,
                    modifier = Modifier
                        .height(38.dp)
                        .testTag("simulate_button_${subject.id}"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF334155),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoGraph,
                        contentDescription = "Simulate",
                        modifier = Modifier.size(16.dp),
                        tint = SecondaryTeal
                    )
                }
            }
        }
    }
}
