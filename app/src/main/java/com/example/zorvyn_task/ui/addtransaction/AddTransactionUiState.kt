package com.example.zorvyn_task.ui.addtransaction

import com.example.zorvyn_task.data.local.TransactionType

data class AddTransactionUiState(
    val amount: String = "",
    val type: TransactionType = TransactionType.EXPENSE,
    val category: String = "",
    val note: String = "",
    val error: String? = null,
    val isSaving: Boolean = false,
    val saved: Boolean = false
)