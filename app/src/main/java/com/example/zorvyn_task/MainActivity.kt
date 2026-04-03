package com.example.zorvyn_task

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.lifecycleScope
import com.example.zorvyn_task.data.local.GoalPreferences
import com.example.zorvyn_task.data.local.TransactionDatabase
import com.example.zorvyn_task.data.local.UserPreferences
import com.example.zorvyn_task.data.repository.GoalRepository
import com.example.zorvyn_task.data.repository.TransactionRepository
import com.example.zorvyn_task.data.repository.UserRepository
import com.example.zorvyn_task.ui.navigation.AppNavigation
import com.example.zorvyn_task.ui.theme.ThemeManager
import com.example.zorvyn_task.ui.theme.ZorvynTaskTheme
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val transactionRepository by lazy {
        val db = TransactionDatabase.getInstance(applicationContext)
        TransactionRepository(db.transactionDao())
    }

    private val goalRepository by lazy {
        GoalRepository(GoalPreferences(applicationContext))
    }

    private val userRepository by lazy {
        UserRepository(UserPreferences(applicationContext))
    }

    private val themeManager by lazy {
        ThemeManager(applicationContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val isDarkFlow = themeManager.isDarkMode.stateIn(
            scope = lifecycleScope,
            started = SharingStarted.Eagerly,
            initialValue = true
        )

        setContent {
            val isDark by isDarkFlow.collectAsState()

            ZorvynTaskTheme(isDark = isDark) {
                AppNavigation(
                    transactionRepository = transactionRepository,
                    goalRepository        = goalRepository,
                    userRepository        = userRepository,
                    isDarkMode            = isDark,
                    onToggleDarkMode      = {
                        lifecycleScope.launch {
                            themeManager.setDarkMode(!isDark)
                        }
                    }
                )
            }
        }
    }
}