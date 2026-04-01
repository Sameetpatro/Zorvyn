package com.example.zorvyn_task

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.zorvyn_task.data.local.TransactionDatabase
import com.example.zorvyn_task.data.repository.TransactionRepository
import com.example.zorvyn_task.ui.navigation.AppNavigation
import com.example.zorvyn_task.ui.theme.ZorvynTaskTheme

class MainActivity : ComponentActivity() {

    private val repository by lazy {
        val db = TransactionDatabase.getInstance(applicationContext)
        TransactionRepository(db.transactionDao())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ZorvynTaskTheme {
                AppNavigation(repository = repository)
            }
        }
    }
}