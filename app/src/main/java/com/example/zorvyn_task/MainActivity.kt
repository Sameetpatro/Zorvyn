package com.example.zorvyn_task

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.zorvyn_task.data.local.GoalPreferences
import com.example.zorvyn_task.data.local.TransactionDatabase
import com.example.zorvyn_task.data.repository.GoalRepository
import com.example.zorvyn_task.data.repository.TransactionRepository
import com.example.zorvyn_task.ui.navigation.AppNavigation
import com.example.zorvyn_task.ui.theme.ZorvynTaskTheme

class MainActivity : ComponentActivity() {

    private val transactionRepository by lazy {
        val db = TransactionDatabase.getInstance(applicationContext)
        TransactionRepository(db.transactionDao())
    }

    private val goalRepository by lazy {
        GoalRepository(GoalPreferences(applicationContext))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ZorvynTaskTheme {
                AppNavigation(
                    transactionRepository = transactionRepository,
                    goalRepository = goalRepository
                )
            }
        }
    }
}