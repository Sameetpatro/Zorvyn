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
import com.example.zorvyn_task.ui.theme.*

@Composable
fun GoalSection(state: GoalUiState, onSetLimit: (Double) -> Unit) {
    var showDialog by remember { mutableStateOf(false) }

    GlassCard(modifier = Modifier.fillMaxWidth(), cornerRadius = 20.dp) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Daily Goal",
                style = MaterialTheme.typography.labelMedium.copy(
                    color = TextSecondary,
                    letterSpacing = 1.sp
                )
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(
                            if (state.limitSet && state.isWithinLimit) AccentGreen else AccentRed,
                            CircleShape
                        )
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    "🔥 ${state.streakCount} day streak",
                    style = MaterialTheme.typography.labelMedium.copy(color = TextSecondary)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (!state.limitSet) {
            TextButton(
                onClick = { showDialog = true },
                contentPadding = PaddingValues(0.dp)
            ) {
                Text(
                    "Tap to set a daily limit",
                    style = MaterialTheme.typography.bodyMedium.copy(color = AccentBlue)
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
                            color = if (state.isWithinLimit) TextPrimary else AccentRed,
                            fontWeight = FontWeight.Medium
                        )
                    )
                    Text(
                        "of ₹ ${"%.0f".format(state.dailyLimit)} limit",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextTertiary)
                    )
                }
                TextButton(
                    onClick = { showDialog = true },
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(
                        "Edit",
                        style = MaterialTheme.typography.labelMedium.copy(color = AccentBlue)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Progress bar
            val shape = RoundedCornerShape(50)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(shape)
                    .background(GlassWhite15)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(state.progressFraction)
                        .fillMaxHeight()
                        .clip(shape)
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = if (state.isWithinLimit)
                                    listOf(AccentGreen, Color(0xFF6EE7B7))
                                else
                                    listOf(AccentRed, Color(0xFFFCA5A5))
                            )
                        )
                )
            }
        }
    }

    if (showDialog) {
        SetLimitDialog(
            currentLimit = state.dailyLimit,
            onConfirm = { limit ->
                onSetLimit(limit)
                showDialog = false
            },
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
    var input by remember { mutableStateOf(if (currentLimit > 0) currentLimit.toInt().toString() else "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF1A1F3E),
        title = {
            Text(
                "Set Daily Limit",
                style = MaterialTheme.typography.titleMedium.copy(color = TextPrimary)
            )
        },
        text = {
            OutlinedTextField(
                value = input,
                onValueChange = { input = it.filter { c -> c.isDigit() || c == '.' } },
                label = { Text("Amount (₹)", color = TextSecondary) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedBorderColor = AccentBlue,
                    unfocusedBorderColor = GlassBorder,
                    cursorColor = AccentBlue
                )
            )
        },
        confirmButton = {
            TextButton(onClick = {
                val limit = input.toDoubleOrNull()
                if (limit != null && limit > 0) onConfirm(limit)
            }) {
                Text("Set", color = AccentBlue)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondary)
            }
        }
    )
}