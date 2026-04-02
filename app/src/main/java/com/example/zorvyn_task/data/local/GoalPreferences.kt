package com.example.zorvyn_task.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "goal_prefs")

class GoalPreferences(private val context: Context) {

    companion object {
        val DAILY_LIMIT = doublePreferencesKey("daily_limit")
        val STREAK_COUNT = intPreferencesKey("streak_count")
        val LAST_CHECKED_DATE = stringPreferencesKey("last_checked_date")
    }

    val dailyLimit: Flow<Double> = context.dataStore.data
        .map { prefs -> prefs[DAILY_LIMIT] ?: 0.0 }

    val streakCount: Flow<Int> = context.dataStore.data
        .map { prefs -> prefs[STREAK_COUNT] ?: 0 }

    val lastCheckedDate: Flow<String> = context.dataStore.data
        .map { prefs -> prefs[LAST_CHECKED_DATE] ?: "" }

    suspend fun setDailyLimit(limit: Double) {
        context.dataStore.edit { prefs -> prefs[DAILY_LIMIT] = limit }
    }

    suspend fun setStreakCount(count: Int) {
        context.dataStore.edit { prefs -> prefs[STREAK_COUNT] = count }
    }

    suspend fun setLastCheckedDate(date: String) {
        context.dataStore.edit { prefs -> prefs[LAST_CHECKED_DATE] = date }
    }
}