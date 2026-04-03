package com.example.zorvyn_task.ui.theme

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.themeDataStore by preferencesDataStore(name = "theme_prefs")

class ThemeManager(private val context: Context) {
    companion object {
        val DARK_MODE = booleanPreferencesKey("dark_mode")
    }

    val isDarkMode: Flow<Boolean> = context.themeDataStore.data
        .map { prefs -> prefs[DARK_MODE] ?: true }   // default = dark

    suspend fun setDarkMode(enabled: Boolean) {
        context.themeDataStore.edit { prefs -> prefs[DARK_MODE] = enabled }
    }
}