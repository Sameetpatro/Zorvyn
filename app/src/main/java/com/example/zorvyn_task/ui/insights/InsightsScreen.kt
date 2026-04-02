package com.example.zorvyn_task.ui.insights

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.zorvyn_task.ui.components.GlassBackground
import com.example.zorvyn_task.ui.components.GlassCard
import com.example.zorvyn_task.ui.theme.*
import kotlin.math.abs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InsightsScreen(
    viewModel: InsightsViewModel,
    onNavigateBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    GlassBackground(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            "Insights",
                            style = MaterialTheme.typography.titleLarge.copy(
                                color = TextPrimary,
                                fontWeight = FontWeight.Light
                            )
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = TextPrimary
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Spacer(modifier = Modifier.height(4.dp))

                if (!state.hasData) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Add some transactions to see insights",
                            style = MaterialTheme.typography.bodyMedium.copy(color = TextTertiary)
                        )
                    }
                } else {
                    // Monthly total
                    InsightCard(
                        emoji = "📅",
                        title = "This Month",
                        body = "You've spent ₹${"%.2f".format(state.monthlyTotal)} so far this month."
                    )

                    // Weekly comparison
                    val weekChange = state.weeklyChangePercent
                    val weekBody = when {
                        state.lastWeekTotal == 0.0 -> "No spending last week to compare."
                        weekChange > 0 -> "You spent ${abs(weekChange).toInt()}% more this week compared to last week."
                        weekChange < 0 -> "You spent ${abs(weekChange).toInt()}% less this week compared to last week. Great job!"
                        else -> "Your spending is the same as last week."
                    }
                    InsightCard(
                        emoji = if (weekChange > 0) "📈" else "📉",
                        title = "Weekly Comparison",
                        body = weekBody,
                        highlight = if (weekChange > 10) AccentRed else if (weekChange < 0) AccentGreen else null
                    )

                    // Top category
                    if (state.topCategory.isNotEmpty()) {
                        InsightCard(
                            emoji = "🏷️",
                            title = "Top Category",
                            body = "${state.topCategory} is your highest expense category at ₹${"%.2f".format(state.topCategoryAmount)}."
                        )
                    }

                    // This week vs last week breakdown
                    GlassCard(modifier = Modifier.fillMaxWidth(), cornerRadius = 20.dp) {
                        Text(
                            "📊  Weekly Breakdown",
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = TextSecondary,
                                letterSpacing = 0.5.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            WeekColumn("This Week", state.thisWeekTotal)
                            WeekColumn("Last Week", state.lastWeekTotal)
                        }
                    }

                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }
}

@Composable
private fun InsightCard(
    emoji: String,
    title: String,
    body: String,
    highlight: Color? = null
) {
    GlassCard(modifier = Modifier.fillMaxWidth(), cornerRadius = 20.dp) {
        Row(verticalAlignment = Alignment.Top) {
            Text(emoji, fontSize = 22.sp)
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    title,
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = highlight ?: TextSecondary,
                        letterSpacing = 0.5.sp
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    body,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = TextPrimary,
                        fontWeight = FontWeight.Normal
                    )
                )
            }
        }
    }
}

@Composable
private fun WeekColumn(label: String, amount: Double) {
    Column(horizontalAlignment = Alignment.Start) {
        Text(
            label,
            style = MaterialTheme.typography.labelSmall.copy(color = TextTertiary)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            "₹ ${"%.2f".format(amount)}",
            style = MaterialTheme.typography.bodyLarge.copy(
                color = TextPrimary,
                fontWeight = FontWeight.Medium
            )
        )
    }
}