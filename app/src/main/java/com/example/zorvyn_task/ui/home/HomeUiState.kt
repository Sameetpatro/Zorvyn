package com.example.zorvyn_task.ui.home

import com.example.zorvyn_task.data.local.TransactionEntity

data class HomeUiState(
    val balance: Double = 0.0,
    val totalIncome: Double = 0.0,
    val totalExpense: Double = 0.0,
    val transactions: List<TransactionEntity> = emptyList(),
    val isLoading: Boolean = true
)