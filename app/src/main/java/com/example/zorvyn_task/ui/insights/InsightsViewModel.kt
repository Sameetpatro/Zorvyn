package com.example.zorvyn_task.ui.insights

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.zorvyn_task.data.local.TransactionType
import com.example.zorvyn_task.data.repository.TransactionRepository
import kotlinx.coroutines.flow.*
import java.util.*

class InsightsViewModel(
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    val uiState: StateFlow<InsightsUiState> = transactionRepository
        .getAllTransactions()
        .map { transactions ->
            val expenses = transactions.filter { it.type == TransactionType.EXPENSE }

            if (expenses.isEmpty()) return@map InsightsUiState()

            val cal = Calendar.getInstance()

            // This week's expenses
            val thisWeekStart = Calendar.getInstance().apply {
                set(Calendar.DAY_OF_WEEK, firstDayOfWeek)
                set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
            }.timeInMillis

            val lastWeekStart = Calendar.getInstance().apply {
                set(Calendar.DAY_OF_WEEK, firstDayOfWeek)
                set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
                add(Calendar.WEEK_OF_YEAR, -1)
            }.timeInMillis

            val lastWeekEnd = thisWeekStart - 1

            // This month
            val monthStart = Calendar.getInstance().apply {
                set(Calendar.DAY_OF_MONTH, 1)
                set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
            }.timeInMillis

            val thisWeekTotal = expenses
                .filter { it.date >= thisWeekStart }
                .sumOf { it.amount }

            val lastWeekTotal = expenses
                .filter { it.date in lastWeekStart..lastWeekEnd }
                .sumOf { it.amount }

            val monthlyTotal = expenses
                .filter { it.date >= monthStart }
                .sumOf { it.amount }

            // Top category
            val topCategoryEntry = expenses
                .groupBy { it.category }
                .mapValues { (_, txs) -> txs.sumOf { it.amount } }
                .maxByOrNull { it.value }

            val weeklyChangePercent = if (lastWeekTotal > 0)
                ((thisWeekTotal - lastWeekTotal) / lastWeekTotal) * 100
            else 0.0

            InsightsUiState(
                topCategory = topCategoryEntry?.key ?: "",
                topCategoryAmount = topCategoryEntry?.value ?: 0.0,
                thisWeekTotal = thisWeekTotal,
                lastWeekTotal = lastWeekTotal,
                monthlyTotal = monthlyTotal,
                weeklyChangePercent = weeklyChangePercent,
                hasData = true
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = InsightsUiState()
        )

    class Factory(private val repository: TransactionRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return InsightsViewModel(repository) as T
        }
    }
}