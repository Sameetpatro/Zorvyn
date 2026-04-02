package com.example.zorvyn_task.ui.insights

data class InsightsUiState(
    val topCategory: String = "",
    val topCategoryAmount: Double = 0.0,
    val thisWeekTotal: Double = 0.0,
    val lastWeekTotal: Double = 0.0,
    val monthlyTotal: Double = 0.0,
    val weeklyChangePercent: Double = 0.0,
    val hasData: Boolean = false
)