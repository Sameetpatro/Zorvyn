package com.example.zorvyn_task.ui.insights

import androidx.compose.animation.core.*
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.zorvyn_task.ui.components.GlassBackground
import com.example.zorvyn_task.ui.components.GlassCard
import com.example.zorvyn_task.ui.theme.LocalAppColors
import kotlin.math.abs
import kotlin.math.roundToInt

// ── Feed card data ────────────────────────────────────────────────────────────

enum class FeedCardType { USEFUL, FUN, TIP, NEUTRAL }

data class FeedCard(
    val emoji: String,
    val title: String,
    val body: String,
    val type: FeedCardType,
    val tag: String
)

fun buildFeedCards(state: InsightsUiState): List<FeedCard> {
    val cards = mutableListOf<FeedCard>()

    // Dynamic cards from real data (highest priority, shown first)
    if (state.hasData) {
        if (state.monthlyTotal > 0) {
            val dayOfMonth = java.util.Calendar.getInstance().get(java.util.Calendar.DAY_OF_MONTH)
            val projected = state.monthlyTotal * 30 / dayOfMonth
            cards += FeedCard(
                emoji = "📅",
                title = "Your Month So Far",
                body  = "You've spent ₹${"%.0f".format(state.monthlyTotal)} this month. At this pace, your projected monthly total is ₹${"%.0f".format(projected)}.",
                type  = FeedCardType.NEUTRAL,
                tag   = "📈 Your Data"
            )
        }
        if (state.topCategory.isNotEmpty()) {
            cards += FeedCard(
                emoji = "🏷️",
                title = "Your Top Category",
                body  = "${state.topCategory} is where most of your money goes — ₹${"%.0f".format(state.topCategoryAmount)} this month. Is this intentional?",
                type  = FeedCardType.NEUTRAL,
                tag   = "📈 Your Data"
            )
        }
        val change = state.weeklyChangePercent
        if (state.lastWeekTotal > 0) {
            val dir = if (change > 0) "more" else "less"
            val pct = abs(change).roundToInt()
            cards += FeedCard(
                emoji = if (change > 0) "📈" else "📉",
                title = "Week-over-Week",
                body  = "You spent $pct% $dir this week vs last week. ${if (change < 0) "Great trend — keep it going!" else "Your spending is climbing — worth a check."}",
                type  = if (change < 0) FeedCardType.USEFUL else FeedCardType.NEUTRAL,
                tag   = "📈 Your Data"
            )
        }
        if (state.categoryTotals.size >= 3) {
            val top3 = state.categoryTotals.entries.take(3).joinToString(", ") { (k, v) ->
                "${k.split(" ").last()} (₹${"%.0f".format(v)})"
            }
            cards += FeedCard(
                emoji = "🧾",
                title = "Your Top 3 Spends",
                body  = "This month: $top3. Knowing where your money goes is the first step to directing it better.",
                type  = FeedCardType.NEUTRAL,
                tag   = "📈 Your Data"
            )
        }
    }

    // General finance tips (always shown, fill remaining up to 7 total)
    val tips = listOf(
        FeedCard(
            emoji = "☕",
            title = "The Latte Factor",
            body  = "₹150/day on snacks or coffee = ₹54,750 a year. Small daily habits quietly shape your financial future.",
            type  = FeedCardType.TIP,
            tag   = "💡 Useful"
        ),
        FeedCard(
            emoji = "📊",
            title = "The 50/30/20 Rule",
            body  = "50% needs, 30% wants, 20% savings. The simplest budgeting framework that actually works long-term.",
            type  = FeedCardType.USEFUL,
            tag   = "💡 Useful"
        ),
        FeedCard(
            emoji = "🎯",
            title = "Pay Yourself First",
            body  = "Auto-transfer to savings the moment your salary hits. If you save what's left after spending, you'll rarely save anything.",
            type  = FeedCardType.TIP,
            tag   = "💡 Useful"
        ),
        FeedCard(
            emoji = "🧠",
            title = "Anchoring Bias",
            body  = "A ₹10,000 item marked down to ₹7,000 feels like a win — but you still spent ₹7,000. Sales are designed to make spending feel smart.",
            type  = FeedCardType.FUN,
            tag   = "🎉 Fun Fact"
        ),
        FeedCard(
            emoji = "🔄",
            title = "Compound Interest",
            body  = "₹10,000 at 12% for 20 years becomes ₹96,462 — without adding a single rupee. Starting early matters more than the amount.",
            type  = FeedCardType.USEFUL,
            tag   = "💡 Useful"
        ),
        FeedCard(
            emoji = "🛍️",
            title = "The 24-Hour Rule",
            body  = "Before any non-essential purchase above ₹500, wait 24 hours. Most impulse urges vanish by morning.",
            type  = FeedCardType.TIP,
            tag   = "💡 Useful"
        ),
        FeedCard(
            emoji = "🎲",
            title = "Hedonic Adaptation",
            body  = "The joy of a new phone fades in weeks — humans adapt to new things fast. Experiences tend to bring longer-lasting happiness than stuff.",
            type  = FeedCardType.FUN,
            tag   = "🎉 Fun Fact"
        )
    )

    // Fill up to 7 cards total
    for (tip in tips) {
        if (cards.size >= 7) break
        cards += tip
    }

    return cards.take(7)
}

// ── Insights Screen ───────────────────────────────────────────────────────────

enum class InsightsTab { FEED, ANALYTICS }

@Composable
fun InsightsScreen(viewModel: InsightsViewModel) {
    val state  by viewModel.uiState.collectAsState()
    val colors = LocalAppColors.current

    var selectedTab by remember { mutableStateOf(InsightsTab.FEED) }

    GlassBackground(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
                .padding(top = 56.dp, bottom = 100.dp)
        ) {
            // ── Title ─────────────────────────────────────────────────────────
            Text(
                "Insights",
                style = MaterialTheme.typography.titleLarge.copy(
                    color = colors.textPrimary, fontWeight = FontWeight.Light
                )
            )

            Spacer(modifier = Modifier.height(14.dp))

            // ── Tab toggle (Feed | Analytics) — same style as Income/Expense ──
            val toggleShape = RoundedCornerShape(16.dp)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(toggleShape)
                    .background(colors.glassWhite10)
                    .border(1.dp, colors.glassBorder, toggleShape)
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                listOf(InsightsTab.FEED to "✨  Feed", InsightsTab.ANALYTICS to "📊  Analytics").forEach { (tab, label) ->
                    val isSelected = selectedTab == tab
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (isSelected) colors.accentGreen.copy(alpha = 0.25f)
                                else Color.Transparent
                            )
                            .border(
                                width = if (isSelected) 1.dp else 0.dp,
                                color = if (isSelected) colors.accentGreen.copy(alpha = 0.6f)
                                else Color.Transparent,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable { selectedTab = tab },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            label,
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = if (isSelected) colors.accentGreen else colors.textSecondary,
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ── Tab content ───────────────────────────────────────────────────
            AnimatedContent(
                targetState   = selectedTab,
                transitionSpec = { fadeIn(tween(250)).togetherWith(fadeOut(tween(200))) },
                modifier      = Modifier.fillMaxSize(),
                label         = "insightsTab"
            ) { tab ->
                when (tab) {
                    InsightsTab.FEED      -> FeedContent(state = state)
                    InsightsTab.ANALYTICS -> AnalyticsContent(state = state)
                }
            }
        }
    }
}

// ── Feed section ──────────────────────────────────────────────────────────────

@Composable
private fun FeedContent(state: InsightsUiState) {
    val colors = LocalAppColors.current
    val cards  = remember(state) { buildFeedCards(state) }

    var currentIndex by remember(cards) { mutableIntStateOf(0) }
    var finished     by remember(cards) { mutableStateOf(false) }

    Column(
        modifier            = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Progress indicator
        if (!finished) {
            Text(
                "${currentIndex + 1} of ${cards.size}",
                style = MaterialTheme.typography.labelSmall.copy(color = colors.textTertiary)
            )
            Spacer(modifier = Modifier.height(6.dp))
            // Dot progress
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                cards.forEachIndexed { idx, _ ->
                    Box(
                        modifier = Modifier
                            .size(if (idx == currentIndex) 20.dp else 7.dp, 7.dp)
                            .clip(RoundedCornerShape(50))
                            .background(
                                if (idx <= currentIndex) colors.accentGreen
                                else colors.glassWhite15
                            )
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        if (finished) {
            // ── All done ──────────────────────────────────────────────────────
            Spacer(modifier = Modifier.weight(1f))
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(horizontal = 8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(88.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(colors.accentGreen, colors.accentGreen.copy(alpha = 0.6f))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) { Text("🎉", fontSize = 40.sp) }

                Text(
                    "All cards explored!",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        color = colors.textPrimary, fontWeight = FontWeight.Light,
                        textAlign = TextAlign.Center
                    ),
                    textAlign = TextAlign.Center
                )
                Text(
                    "Switch to Analytics above to dive into your full financial breakdown.",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = colors.textSecondary, textAlign = TextAlign.Center, lineHeight = 22.sp
                    ),
                    textAlign = TextAlign.Center,
                    modifier  = Modifier.padding(horizontal = 12.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                // Restart button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(colors.accentGreen, colors.accentGreen.copy(alpha = 0.7f))
                            )
                        )
                        .clickable { currentIndex = 0; finished = false },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "🔄  Start Over",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = if (colors.isDark) Color.Black else Color.White,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            }
            Spacer(modifier = Modifier.weight(1f))
        } else {
            // ── Card stack ────────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                // Background stack (depth cards)
                val preview = minOf(2, cards.size - currentIndex - 1)
                for (i in preview downTo 1) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(1f - i * 0.05f)
                            .height(380.dp)
                            .offset(y = (i * 10).dp)
                            .clip(RoundedCornerShape(28.dp))
                            .background(colors.glassWhite10.copy(alpha = 0.4f - i * 0.1f))
                            .border(1.dp, colors.glassBorder.copy(alpha = 0.4f), RoundedCornerShape(28.dp))
                            .zIndex((2 - i).toFloat())
                    )
                }

                // Active swipeable card
                SwipeCard(
                    card   = cards[currentIndex],
                    onSwipe = {
                        if (currentIndex < cards.size - 1) currentIndex++
                        else finished = true
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Swipe hint row
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment     = Alignment.CenterVertically,
                modifier              = Modifier.fillMaxWidth()
            ) {
                Text("👈 swipe", style = MaterialTheme.typography.labelSmall.copy(color = colors.textTertiary))
                Spacer(modifier = Modifier.width(16.dp))
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(colors.accentGreen.copy(alpha = 0.15f))
                        .border(1.dp, colors.accentGreen.copy(alpha = 0.4f), CircleShape)
                        .clickable {
                            if (currentIndex < cards.size - 1) currentIndex++
                            else finished = true
                        },
                    contentAlignment = Alignment.Center
                ) { Text("→", fontSize = 18.sp, color = colors.accentGreen) }
                Spacer(modifier = Modifier.width(16.dp))
                Text("swipe 👉", style = MaterialTheme.typography.labelSmall.copy(color = colors.textTertiary))
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

// ── Swipeable card ────────────────────────────────────────────────────────────

@Composable
private fun SwipeCard(card: FeedCard, onSwipe: () -> Unit) {
    val colors = LocalAppColors.current

    var offsetX    by remember { mutableFloatStateOf(0f) }
    var isDragging by remember { mutableStateOf(false) }

    val animOffsetX by animateFloatAsState(
        targetValue   = if (isDragging) offsetX else 0f,
        animationSpec = if (isDragging) tween(0) else spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness    = Spring.StiffnessMediumLow
        ),
        label = "swipeX"
    )

    val rotation = (animOffsetX / 28f).coerceIn(-14f, 14f)
    val likeAlpha = (animOffsetX / 180f).coerceIn(0f, 1f)
    val skipAlpha = (-animOffsetX / 180f).coerceIn(0f, 1f)

    val tagColor = when (card.type) {
        FeedCardType.USEFUL  -> colors.accentGreen
        FeedCardType.FUN     -> Color(0xFFF59E0B)
        FeedCardType.TIP     -> colors.accentBlue
        FeedCardType.NEUTRAL -> colors.textSecondary
    }
    val cardGradient = when (card.type) {
        FeedCardType.USEFUL  -> Brush.verticalGradient(listOf(colors.accentGreen.copy(alpha = 0.14f), colors.glassWhite10))
        FeedCardType.FUN     -> Brush.verticalGradient(listOf(Color(0xFFF59E0B).copy(alpha = 0.14f), colors.glassWhite10))
        FeedCardType.TIP     -> Brush.verticalGradient(listOf(colors.accentBlue.copy(alpha = 0.14f), colors.glassWhite10))
        FeedCardType.NEUTRAL -> Brush.verticalGradient(listOf(colors.glassWhite15, colors.glassWhite10))
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(380.dp)
            .offset(x = animOffsetX.dp)
            .rotate(rotation)
            .zIndex(10f)
            .pointerInput(card) {
                detectDragGestures(
                    onDragStart  = { isDragging = true },
                    onDragEnd    = {
                        isDragging = false
                        if (abs(offsetX) > 110f) { offsetX = 0f; onSwipe() }
                        else offsetX = 0f
                    },
                    onDragCancel = { isDragging = false; offsetX = 0f },
                    onDrag       = { change, drag -> change.consume(); offsetX += drag.x }
                )
            }
            .clip(RoundedCornerShape(28.dp))
            .background(cardGradient)
            .border(1.dp, tagColor.copy(alpha = 0.3f), RoundedCornerShape(28.dp))
    ) {
        // GOT IT overlay
        Box(
            modifier = Modifier
                .align(Alignment.TopStart).padding(20.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(colors.accentGreen.copy(alpha = likeAlpha * 0.85f))
                .padding(horizontal = 10.dp, vertical = 5.dp)
        ) {
            Text("GOT IT ✓",
                style = MaterialTheme.typography.labelMedium.copy(
                    color = Color.White.copy(alpha = likeAlpha), fontWeight = FontWeight.Bold))
        }
        // SKIP overlay
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd).padding(20.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(colors.textTertiary.copy(alpha = skipAlpha * 0.7f))
                .padding(horizontal = 10.dp, vertical = 5.dp)
        ) {
            Text("SKIP →",
                style = MaterialTheme.typography.labelMedium.copy(
                    color = Color.White.copy(alpha = skipAlpha), fontWeight = FontWeight.Bold))
        }

        // Content
        Column(
            modifier            = Modifier.fillMaxSize().padding(28.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Tag pill
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(tagColor.copy(alpha = 0.15f))
                    .border(1.dp, tagColor.copy(alpha = 0.4f), RoundedCornerShape(50))
                    .padding(horizontal = 12.dp, vertical = 5.dp)
            ) {
                Text(card.tag,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = tagColor, fontWeight = FontWeight.SemiBold))
            }

            // Emoji + title + body
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(card.emoji, fontSize = 56.sp, textAlign = TextAlign.Center)
                Text(
                    card.title,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        color = colors.textPrimary, fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center
                    ),
                    textAlign = TextAlign.Center
                )
                Text(
                    card.body,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = colors.textSecondary, textAlign = TextAlign.Center, lineHeight = 22.sp
                    ),
                    textAlign = TextAlign.Center
                )
            }

            Text(
                "← swipe either way →",
                style = MaterialTheme.typography.labelSmall.copy(color = colors.textTertiary),
                textAlign = TextAlign.Center,
                modifier  = Modifier.fillMaxWidth()
            )
        }
    }
}

// ── Analytics section (original charts) ──────────────────────────────────────

@Composable
private fun AnalyticsContent(state: InsightsUiState) {
    val colors = LocalAppColors.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        if (!state.hasData) {
            Box(
                modifier = Modifier.fillMaxWidth().padding(vertical = 60.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Add some transactions to see analytics",
                    style = MaterialTheme.typography.bodyMedium.copy(color = colors.textTertiary)
                )
            }
        } else {
            // Monthly total card
            InsightCard(
                emoji = "📅",
                title = "This Month",
                body  = "You've spent ₹${"%.2f".format(state.monthlyTotal)} so far this month."
            )

            // Weekly comparison
            val weekChange = state.weeklyChangePercent
            val weekBody = when {
                state.lastWeekTotal == 0.0 -> "No spending last week to compare."
                weekChange > 0 -> "You spent ${abs(weekChange).roundToInt()}% more this week vs last week."
                weekChange < 0 -> "You spent ${abs(weekChange).roundToInt()}% less this week vs last week. Great job!"
                else           -> "Your spending matches last week."
            }
            InsightCard(
                emoji     = if (weekChange > 0) "📈" else "📉",
                title     = "Weekly Comparison",
                body      = weekBody,
                highlight = if (weekChange > 10) colors.accentRed else if (weekChange < 0) colors.accentGreen else null
            )

            // Top category
            if (state.topCategory.isNotEmpty()) {
                InsightCard(
                    emoji = "🏷️",
                    title = "Top Category",
                    body  = "${state.topCategory} is your biggest expense at ₹${"%.2f".format(state.topCategoryAmount)}."
                )
            }

            // Bar chart – weekly
            SpendingBarChart(thisWeek = state.thisWeekTotal, lastWeek = state.lastWeekTotal)

            // Category bar chart
            if (state.categoryTotals.isNotEmpty()) {
                CategoryBarChart(categoryTotals = state.categoryTotals)
            }

            // Heatmap
            SpendingHeatmap(daySpending = state.last35DaySpending)

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

// ── Chart composables (unchanged from original) ───────────────────────────────

@Composable
private fun SpendingBarChart(thisWeek: Double, lastWeek: Double) {
    val colors = LocalAppColors.current
    val maxVal = maxOf(thisWeek, lastWeek, 1.0)
    val animProgress by animateFloatAsState(
        targetValue   = 1f,
        animationSpec = tween(900, easing = EaseOutCubic),
        label         = "barAnim"
    )
    GlassCard(modifier = Modifier.fillMaxWidth(), cornerRadius = 20.dp) {
        Text("📊  Weekly Spending",
            style = MaterialTheme.typography.labelMedium.copy(
                color = colors.textSecondary, letterSpacing = 0.5.sp))
        Spacer(modifier = Modifier.height(20.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment     = Alignment.Bottom
        ) {
            BarColumn(
                label    = "Last Week",
                value    = lastWeek,
                fraction = (lastWeek / maxVal * animProgress).toFloat(),
                barColor = Brush.verticalGradient(listOf(colors.accentBlue.copy(alpha = 0.6f), colors.accentBlue.copy(alpha = 0.2f))),
                textColor = colors.textSecondary
            )
            BarColumn(
                label    = "This Week",
                value    = thisWeek,
                fraction = (thisWeek / maxVal * animProgress).toFloat(),
                barColor = Brush.verticalGradient(listOf(colors.accentGreen, colors.accentGreen.copy(alpha = 0.3f))),
                textColor = colors.accentGreen
            )
        }
    }
}

@Composable
private fun RowScope.BarColumn(
    label: String, value: Double, fraction: Float, barColor: Brush, textColor: Color
) {
    val maxBarHeight = 120.dp
    Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
        Text("₹${"%.0f".format(value)}",
            style = MaterialTheme.typography.bodySmall.copy(color = textColor, fontWeight = FontWeight.SemiBold))
        Spacer(modifier = Modifier.height(4.dp))
        Box(modifier = Modifier.height(maxBarHeight), contentAlignment = Alignment.BottomCenter) {
            Box(
                modifier = Modifier
                    .width(48.dp)
                    .fillMaxHeight(fraction.coerceAtLeast(0.04f))
                    .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                    .background(barColor)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(label, style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF98FF98)))
    }
}

@Composable
private fun CategoryBarChart(categoryTotals: Map<String, Double>) {
    val colors = LocalAppColors.current
    val maxVal = categoryTotals.values.maxOrNull() ?: 1.0
    val animProgress by animateFloatAsState(
        targetValue   = 1f,
        animationSpec = tween(1000, delayMillis = 100, easing = EaseOutCubic),
        label         = "catAnim"
    )
    GlassCard(modifier = Modifier.fillMaxWidth(), cornerRadius = 20.dp) {
        Text("🏷️  By Category (this month)",
            style = MaterialTheme.typography.labelMedium.copy(
                color = colors.textSecondary, letterSpacing = 0.5.sp))
        Spacer(modifier = Modifier.height(16.dp))
        val catColors = listOf(
            colors.accentBlue, colors.accentGreen, colors.accentRed,
            Color(0xFFF59E0B), Color(0xFF8B5CF6), Color(0xFFEC4899)
        )
        categoryTotals.entries.take(6).forEachIndexed { idx, (cat, amount) ->
            val fraction = (amount / maxVal * animProgress).toFloat().coerceAtLeast(0.02f)
            Column(modifier = Modifier.padding(vertical = 4.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(cat, style = MaterialTheme.typography.bodySmall.copy(color = colors.textPrimary))
                    Text("₹${"%.0f".format(amount)}", style = MaterialTheme.typography.bodySmall.copy(
                        color = catColors[idx % catColors.size], fontWeight = FontWeight.SemiBold))
                }
                Spacer(modifier = Modifier.height(4.dp))
                Box(modifier = Modifier.fillMaxWidth().height(7.dp).clip(RoundedCornerShape(50)).background(colors.glassWhite10)) {
                    Box(modifier = Modifier.fillMaxWidth(fraction).fillMaxHeight().clip(RoundedCornerShape(50)).background(catColors[idx % catColors.size]))
                }
            }
        }
    }
}

@Composable
private fun SpendingHeatmap(daySpending: List<Pair<String, Double>>) {
    val colors   = LocalAppColors.current
    val maxSpend = daySpending.maxOfOrNull { it.second } ?: 1.0
    val animProgress by animateFloatAsState(
        targetValue = 1f, animationSpec = tween(700, delayMillis = 200), label = "heatAnim")

    GlassCard(modifier = Modifier.fillMaxWidth(), cornerRadius = 20.dp) {
        Text("🗓  Daily Spending (last 35 days)",
            style = MaterialTheme.typography.labelMedium.copy(color = colors.textSecondary, letterSpacing = 0.5.sp))
        Spacer(modifier = Modifier.height(12.dp))
        val rows      = daySpending.chunked(7)
        val dayLabels = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            dayLabels.forEach { d ->
                Text(d, style = MaterialTheme.typography.labelSmall.copy(
                    color = colors.textTertiary, fontSize = 9.sp),
                    modifier  = Modifier.width(34.dp),
                    textAlign = TextAlign.Center)
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        rows.forEach { week ->
            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween) {
                val padded = week + List(7 - week.size) { Pair("", 0.0) }
                padded.forEach { (_, amt) ->
                    val intensity = if (maxSpend > 0) (amt / maxSpend * animProgress).toFloat() else 0f
                    val cellColor = when {
                        amt <= 0        -> colors.glassWhite10
                        intensity < 0.25f -> colors.accentGreen.copy(alpha = 0.25f)
                        intensity < 0.50f -> colors.accentGreen.copy(alpha = 0.50f)
                        intensity < 0.75f -> colors.accentGreen.copy(alpha = 0.75f)
                        else              -> colors.accentGreen
                    }
                    Box(modifier = Modifier.size(width = 34.dp, height = 26.dp)
                        .clip(RoundedCornerShape(5.dp)).background(cellColor)
                        .border(0.5.dp, colors.glassBorder, RoundedCornerShape(5.dp)))
                }
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Less", style = MaterialTheme.typography.labelSmall.copy(color = colors.textTertiary, fontSize = 9.sp))
            Spacer(modifier = Modifier.width(6.dp))
            listOf(0.1f, 0.35f, 0.6f, 0.85f, 1.0f).forEach { alpha ->
                Box(modifier = Modifier.padding(horizontal = 2.dp).size(14.dp).clip(RoundedCornerShape(3.dp))
                    .background(if (alpha < 0.15f) colors.glassWhite10 else colors.accentGreen.copy(alpha = alpha)))
            }
            Spacer(modifier = Modifier.width(6.dp))
            Text("More", style = MaterialTheme.typography.labelSmall.copy(color = colors.textTertiary, fontSize = 9.sp))
        }
    }
}

@Composable
private fun InsightCard(emoji: String, title: String, body: String, highlight: Color? = null) {
    val colors = LocalAppColors.current
    GlassCard(modifier = Modifier.fillMaxWidth(), cornerRadius = 20.dp) {
        Row(verticalAlignment = Alignment.Top) {
            Text(emoji, fontSize = 22.sp)
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(title, style = MaterialTheme.typography.labelMedium.copy(
                    color = highlight ?: colors.textSecondary, letterSpacing = 0.5.sp))
                Spacer(modifier = Modifier.height(4.dp))
                Text(body, style = MaterialTheme.typography.bodyMedium.copy(
                    color = colors.textPrimary, fontWeight = FontWeight.Normal))
            }
        }
    }
}