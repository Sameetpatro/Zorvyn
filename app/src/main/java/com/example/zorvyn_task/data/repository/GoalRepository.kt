package com.example.zorvyn_task.data.repository

import com.example.zorvyn_task.data.local.GoalPreferences
import kotlinx.coroutines.flow.Flow

class GoalRepository(private val prefs: GoalPreferences) {

    val dailyLimit: Flow<Double> = prefs.dailyLimit
    val streakCount: Flow<Int> = prefs.streakCount
    val lastCheckedDate: Flow<String> = prefs.lastCheckedDate

    suspend fun setDailyLimit(limit: Double) = prefs.setDailyLimit(limit)
    suspend fun setStreakCount(count: Int) = prefs.setStreakCount(count)
    suspend fun setLastCheckedDate(date: String) = prefs.setLastCheckedDate(date)
}