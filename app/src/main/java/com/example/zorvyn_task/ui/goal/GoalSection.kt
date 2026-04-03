package com.example.zorvyn_task.ui.goal

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.zorvyn_task.ui.components.GlassCard
import com.example.zorvyn_task.ui.theme.LocalAppColors

@Composable
fun GoalSection(state: GoalUiState, onSetLimit: (Double) -> Unit) {
    var showDialog by remember { mutableStateOf(false) }
    val colors = LocalAppColors.current

    GlassCard(modifier = Modifier.fillMaxWidth(), cornerRadius = 20.dp) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Daily Goal",
                style = MaterialTheme.typography.labelMedium.copy(
                    color = colors.textSecondary, letterSpacing = 1.sp
                )
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(
                            if (state.limitSet && state.isWithinLimit) colors.accentGreen else colors.accentRed,
                            CircleShape
                        )
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    "🔥 ${state.streakCount} day streak",
                    style = MaterialTheme.typography.labelMedium.copy(color = colors.textSecondary)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (!state.limitSet) {
            TextButton(onClick = { showDialog = true }, contentPadding = PaddingValues(0.dp)) {
                Text(
                    "Tap to set a daily limit",
                    style = MaterialTheme.typography.bodyMedium.copy(color = colors.accentBlue)
                )
            }
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column {
                    Text(
                        "₹ ${"%.0f".format(state.todaySpent)} spent",
                        style = MaterialTheme.typography.titleLarge.copy(
                            color = if (state.isWithinLimit) colors.textPrimary else colors.accentRed,
                            fontWeight = FontWeight.Medium
                        )
                    )
                    Text(
                        "of ₹ ${"%.0f".format(state.dailyLimit)} limit",
                        style = MaterialTheme.typography.bodySmall.copy(color = colors.textTertiary)
                    )
                }
                TextButton(onClick = { showDialog = true }, contentPadding = PaddingValues(0.dp)) {
                    Text("Edit", style = MaterialTheme.typography.labelMedium.copy(color = colors.accentBlue))
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            val shape = RoundedCornerShape(50)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(shape)
                    .background(colors.glassWhite15)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(state.progressFraction)
                        .fillMaxHeight()
                        .clip(shape)
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = if (state.isWithinLimit)
                                    listOf(colors.accentGreen, colors.accentGreen.copy(alpha = 0.5f))
                                else
                                    listOf(colors.accentRed, colors.accentRed.copy(alpha = 0.5f))
                            )
                        )
                )
            }
        }
    }

    if (showDialog) {
        SetLimitDialog(
            currentLimit = state.dailyLimit,
            onConfirm = { limit -> onSetLimit(limit); showDialog = false },
            onDismiss = { showDialog = false }
        )
    }
}

@Composable
private fun SetLimitDialog(
    currentLimit: Double,
    onConfirm: (Double) -> Unit,
    onDismiss: () -> Unit
) {
    val colors = LocalAppColors.current
    var input by remember { mutableStateOf(if (currentLimit > 0) currentLimit.toInt().toString() else "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = if (colors.isDark) Color(0xFF1A1F3E) else Color(0xFFF0FAF6),
        title = {
            Text("Set Daily Limit",
                style = MaterialTheme.typography.titleMedium.copy(color = colors.textPrimary))
        },
        text = {
            OutlinedTextField(
                value = input,
                onValueChange = { input = it.filter { c -> c.isDigit() || c == '.' } },
                label = { Text("Amount (₹)", color = colors.textSecondary) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = colors.textPrimary,
                    unfocusedTextColor = colors.textPrimary,
                    focusedBorderColor = colors.accentBlue,
                    unfocusedBorderColor = colors.glassBorder,
                    cursorColor = colors.accentBlue
                )
            )
        },
        confirmButton = {
            TextButton(onClick = {
                val limit = input.toDoubleOrNull()
                if (limit != null && limit > 0) onConfirm(limit)
            }) {
                Text("Set", color = colors.accentBlue)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = colors.textSecondary)
            }
        }
    )
}