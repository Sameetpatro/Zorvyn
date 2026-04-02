package com.example.zorvyn_task.ui.goal

data class GoalUiState(
    val dailyLimit: Double = 0.0,
    val todaySpent: Double = 0.0,
    val streakCount: Int = 0,
    val limitSet: Boolean = false
) {
    val isWithinLimit: Boolean get() = dailyLimit > 0 && todaySpent <= dailyLimit
    val progressFraction: Float get() = if (dailyLimit > 0) (todaySpent / dailyLimit).toFloat().coerceIn(0f, 1f) else 0f
}