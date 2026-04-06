package com.example.zorvyn_task.ui.home

import com.example.zorvyn_task.data.local.TransactionEntity
import com.example.zorvyn_task.data.local.TransactionType

enum class TransactionFilter { ALL, INCOME, EXPENSE }

data class HomeUiState(
    val balance: Double = 0.0,
    val totalIncome: Double = 0.0,
    val totalExpense: Double = 0.0,
    val transactions: List<TransactionEntity> = emptyList(),
    val filteredTransactions: List<TransactionEntity> = emptyList(),
    val searchQuery: String = "",
    val activeFilter: TransactionFilter = TransactionFilter.ALL,
    val isLoading: Boolean = true,
    val editingTransaction: TransactionEntity? = null,
    val showDeleteConfirm: TransactionEntity? = null
)