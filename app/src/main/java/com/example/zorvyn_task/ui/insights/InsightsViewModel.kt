package com.example.zorvyn_task.ui.insights

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.zorvyn_task.data.local.TransactionType
import com.example.zorvyn_task.data.repository.TransactionRepository
import kotlinx.coroutines.flow.*
import java.text.SimpleDateFormat
import java.util.*

class InsightsViewModel(
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    val uiState: StateFlow<InsightsUiState> = transactionRepository
        .getAllTransactions()
        .map { transactions ->
            val expenses = transactions.filter { it.type == TransactionType.EXPENSE }
            if (transactions.isEmpty()) return@map InsightsUiState()

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

            val monthStart = Calendar.getInstance().apply {
                set(Calendar.DAY_OF_MONTH, 1)
                set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
            }.timeInMillis

            val thisWeekTotal = expenses.filter { it.date >= thisWeekStart }.sumOf { it.amount }
            val lastWeekTotal = expenses.filter { it.date in lastWeekStart..lastWeekEnd }.sumOf { it.amount }
            val monthlyTotal = expenses.filter { it.date >= monthStart }.sumOf { it.amount }

            val topCategoryEntry = expenses
                .filter { it.date >= monthStart }
                .groupBy { it.category }
                .mapValues { (_, txs) -> txs.sumOf { it.amount } }
                .maxByOrNull { it.value }

            val categoryTotals = expenses
                .filter { it.date >= monthStart }
                .groupBy { it.category }
                .mapValues { (_, txs) -> txs.sumOf { it.amount } }
                .entries.sortedByDescending { it.value }
                .associate { it.key to it.value }

            val weeklyChangePercent = if (lastWeekTotal > 0)
                ((thisWeekTotal - lastWeekTotal) / lastWeekTotal) * 100
            else 0.0

            val incomeCount = transactions.filter { it.type == TransactionType.INCOME }.size
            val expenseCount = expenses.size
            val frequentType = if (incomeCount >= expenseCount) "Income" else "Expense"
            val frequentCount = if (incomeCount >= expenseCount) incomeCount else expenseCount

            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val labelFmt = SimpleDateFormat("EEE dd", Locale.getDefault())
            val dayMap = expenses.groupBy { sdf.format(Date(it.date)) }
                .mapValues { (_, txs) -> txs.sumOf { it.amount } }

            val last35 = (34 downTo 0).map { daysBack ->
                val cal = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -daysBack) }
                val key = sdf.format(cal.time)
                val label = labelFmt.format(cal.time)
                Pair(label, dayMap[key] ?: 0.0)
            }

            InsightsUiState(
                topCategory = topCategoryEntry?.key ?: "",
                topCategoryAmount = topCategoryEntry?.value ?: 0.0,
                frequentTransactionType = frequentType,
                frequentTypeCount = frequentCount,
                thisWeekTotal = thisWeekTotal,
                lastWeekTotal = lastWeekTotal,
                monthlyTotal = monthlyTotal,
                weeklyChangePercent = weeklyChangePercent,
                categoryTotals = categoryTotals,
                last35DaySpending = last35,
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
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            InsightsViewModel(repository) as T
    }
}