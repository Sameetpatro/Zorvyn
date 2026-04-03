package com.example.zorvyn_task.ui.insights

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.zorvyn_task.ui.components.GlassBackground
import com.example.zorvyn_task.ui.components.GlassCard
import com.example.zorvyn_task.ui.theme.LocalAppColors
import kotlin.math.abs
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InsightsScreen(viewModel: InsightsViewModel) {
    val state by viewModel.uiState.collectAsState()
    val colors = LocalAppColors.current

    GlassBackground(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(top = 56.dp, bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                "Insights",
                style = MaterialTheme.typography.titleLarge.copy(
                    color = colors.textPrimary,
                    fontWeight = FontWeight.Light
                )
            )

            if (!state.hasData) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 60.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Add some transactions to see insights",
                        style = MaterialTheme.typography.bodyMedium.copy(color = colors.textTertiary)
                    )
                }
            } else {
                // Monthly total card
                InsightCard(
                    emoji = "📅",
                    title = "This Month",
                    body = "You've spent ₹${"%.2f".format(state.monthlyTotal)} so far this month."
                )

                // Weekly comparison
                val weekChange = state.weeklyChangePercent
                val weekBody = when {
                    state.lastWeekTotal == 0.0 -> "No spending last week to compare."
                    weekChange > 0 -> "You spent ${abs(weekChange).roundToInt()}% more this week vs last week."
                    weekChange < 0 -> "You spent ${abs(weekChange).roundToInt()}% less this week vs last week. Great job!"
                    else -> "Your spending matches last week."
                }
                InsightCard(
                    emoji = if (weekChange > 0) "📈" else "📉",
                    title = "Weekly Comparison",
                    body = weekBody,
                    highlight = if (weekChange > 10) colors.accentRed else if (weekChange < 0) colors.accentGreen else null
                )

                // Top category
                if (state.topCategory.isNotEmpty()) {
                    InsightCard(
                        emoji = "🏷️",
                        title = "Top Category",
                        body = "${state.topCategory} is your biggest expense at ₹${"%.2f".format(state.topCategoryAmount)}."
                    )
                }

                // Bar chart – weekly breakdown
                SpendingBarChart(
                    thisWeek = state.thisWeekTotal,
                    lastWeek = state.lastWeekTotal
                )

                // 7-day category bar chart
                if (state.categoryTotals.isNotEmpty()) {
                    CategoryBarChart(categoryTotals = state.categoryTotals)
                }

                // LeetCode-style daily heatmap (last ~35 days = 5 weeks)
                SpendingHeatmap(daySpending = state.last35DaySpending)

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

// ── Bar chart: this week vs last week ─────────────────────────────────────────

@Composable
private fun SpendingBarChart(thisWeek: Double, lastWeek: Double) {
    val colors = LocalAppColors.current
    val maxVal = maxOf(thisWeek, lastWeek, 1.0)

    val animProgress by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(900, easing = EaseOutCubic),
        label = "barAnim"
    )

    GlassCard(modifier = Modifier.fillMaxWidth(), cornerRadius = 20.dp) {
        Text(
            "📊  Weekly Spending",
            style = MaterialTheme.typography.labelMedium.copy(
                color = colors.textSecondary, letterSpacing = 0.5.sp
            )
        )
        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Bottom
        ) {
            BarColumn(
                label = "Last Week",
                value = lastWeek,
                fraction = (lastWeek / maxVal * animProgress).toFloat(),
                barColor = Brush.verticalGradient(
                    listOf(colors.accentBlue.copy(alpha = 0.6f), colors.accentBlue.copy(alpha = 0.2f))
                ),
                textColor = colors.textSecondary
            )
            BarColumn(
                label = "This Week",
                value = thisWeek,
                fraction = (thisWeek / maxVal * animProgress).toFloat(),
                barColor = Brush.verticalGradient(
                    listOf(colors.accentGreen, colors.accentGreen.copy(alpha = 0.3f))
                ),
                textColor = colors.accentGreen
            )
        }
    }
}

@Composable
private fun RowScope.BarColumn(
    label: String,
    value: Double,
    fraction: Float,
    barColor: Brush,
    textColor: Color
) {
    val maxBarHeight = 120.dp
    Column(
        modifier = Modifier.weight(1f),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "₹${"%.0f".format(value)}",
            style = MaterialTheme.typography.bodySmall.copy(color = textColor, fontWeight = FontWeight.SemiBold)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier.height(maxBarHeight),
            contentAlignment = Alignment.BottomCenter
        ) {
            Box(
                modifier = Modifier
                    .width(48.dp)
                    .fillMaxHeight(fraction.coerceAtLeast(0.04f))
                    .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                    .background(barColor)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            label,
            style = MaterialTheme.typography.labelSmall.copy(color = LocalAppColors.current.textTertiary)
        )
    }
}

// ── Category breakdown bar chart ───────────────────────────────────────────────

@Composable
private fun CategoryBarChart(categoryTotals: Map<String, Double>) {
    val colors = LocalAppColors.current
    val maxVal = categoryTotals.values.maxOrNull() ?: 1.0
    val animProgress by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(1000, delayMillis = 100, easing = EaseOutCubic),
        label = "catAnim"
    )

    GlassCard(modifier = Modifier.fillMaxWidth(), cornerRadius = 20.dp) {
        Text(
            "🏷️  By Category (this month)",
            style = MaterialTheme.typography.labelMedium.copy(
                color = colors.textSecondary, letterSpacing = 0.5.sp
            )
        )
        Spacer(modifier = Modifier.height(16.dp))

        val catColors = listOf(
            colors.accentBlue, colors.accentGreen, colors.accentRed,
            Color(0xFFF59E0B), Color(0xFF8B5CF6), Color(0xFFEC4899)
        )

        categoryTotals.entries.take(6).forEachIndexed { idx, (cat, amount) ->
            val fraction = (amount / maxVal * animProgress).toFloat().coerceAtLeast(0.02f)
            Column(modifier = Modifier.padding(vertical = 4.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(cat, style = MaterialTheme.typography.bodySmall.copy(color = colors.textPrimary))
                    Text("₹${"%.0f".format(amount)}", style = MaterialTheme.typography.bodySmall.copy(
                        color = catColors[idx % catColors.size], fontWeight = FontWeight.SemiBold))
                }
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(7.dp)
                        .clip(RoundedCornerShape(50))
                        .background(colors.glassWhite10)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(fraction)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(50))
                            .background(catColors[idx % catColors.size])
                    )
                }
            }
        }
    }
}

// ── LeetCode-style spending heatmap ───────────────────────────────────────────

@Composable
private fun SpendingHeatmap(daySpending: List<Pair<String, Double>>) {
    val colors = LocalAppColors.current
    // daySpending: last 35 entries, each is (label like "Mon 01", amount)
    val maxSpend = daySpending.maxOfOrNull { it.second } ?: 1.0

    val animProgress by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(700, delayMillis = 200),
        label = "heatAnim"
    )

    GlassCard(modifier = Modifier.fillMaxWidth(), cornerRadius = 20.dp) {
        Text(
            "🗓  Daily Spending (last 35 days)",
            style = MaterialTheme.typography.labelMedium.copy(
                color = colors.textSecondary, letterSpacing = 0.5.sp
            )
        )
        Spacer(modifier = Modifier.height(12.dp))

        // 5 rows × 7 cols grid
        val rows = daySpending.chunked(7)
        val dayLabels = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            dayLabels.forEach { d ->
                Text(d, style = MaterialTheme.typography.labelSmall.copy(
                    color = colors.textTertiary, fontSize = 9.sp),
                    modifier = Modifier.width(34.dp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))

        rows.forEach { week ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val padded = week + List(7 - week.size) { Pair("", 0.0) }
                padded.forEach { (_, amt) ->
                    val intensity = if (maxSpend > 0) (amt / maxSpend * animProgress).toFloat() else 0f
                    val cellColor = when {
                        amt <= 0 -> colors.glassWhite10
                        intensity < 0.25f -> colors.accentGreen.copy(alpha = 0.25f)
                        intensity < 0.50f -> colors.accentGreen.copy(alpha = 0.50f)
                        intensity < 0.75f -> colors.accentGreen.copy(alpha = 0.75f)
                        else -> colors.accentGreen
                    }
                    Box(
                        modifier = Modifier
                            .size(width = 34.dp, height = 26.dp)
                            .clip(RoundedCornerShape(5.dp))
                            .background(cellColor)
                            .border(0.5.dp, colors.glassBorder, RoundedCornerShape(5.dp))
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))
        // Legend
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Less", style = MaterialTheme.typography.labelSmall.copy(
                color = colors.textTertiary, fontSize = 9.sp))
            Spacer(modifier = Modifier.width(6.dp))
            listOf(0.1f, 0.35f, 0.6f, 0.85f, 1.0f).forEach { alpha ->
                Box(
                    modifier = Modifier
                        .padding(horizontal = 2.dp)
                        .size(14.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(if (alpha < 0.15f) colors.glassWhite10 else colors.accentGreen.copy(alpha = alpha))
                )
            }
            Spacer(modifier = Modifier.width(6.dp))
            Text("More", style = MaterialTheme.typography.labelSmall.copy(
                color = colors.textTertiary, fontSize = 9.sp))
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
    val colors = LocalAppColors.current
    GlassCard(modifier = Modifier.fillMaxWidth(), cornerRadius = 20.dp) {
        Row(verticalAlignment = Alignment.Top) {
            Text(emoji, fontSize = 22.sp)
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    title,
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = highlight ?: colors.textSecondary,
                        letterSpacing = 0.5.sp
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    body,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = colors.textPrimary,
                        fontWeight = FontWeight.Normal
                    )
                )
            }
        }
    }
}