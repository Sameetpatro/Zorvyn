package com.example.zorvyn_task.ui.goal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.zorvyn_task.data.local.TransactionType
import com.example.zorvyn_task.data.repository.GoalRepository
import com.example.zorvyn_task.data.repository.TransactionRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class GoalViewModel(
    private val goalRepository: GoalRepository,
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    val uiState: StateFlow<GoalUiState> = combine(
        goalRepository.dailyLimit,
        goalRepository.streakCount,
        transactionRepository.getAllTransactions()
    ) { limit, streak, transactions ->
        val todayStr = dateFormat.format(Date())
        val todaySpent = transactions.filter { tx ->
            tx.type == TransactionType.EXPENSE &&
                    dateFormat.format(Date(tx.date)) == todayStr
        }.sumOf { it.amount }

        GoalUiState(
            dailyLimit = limit,
            todaySpent = todaySpent,
            streakCount = streak,
            limitSet = limit > 0.0
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = GoalUiState()
    )

    fun evaluateStreakForToday() {
        viewModelScope.launch {
            val todayStr = dateFormat.format(Date())
            val lastChecked = goalRepository.lastCheckedDate.first()
            if (lastChecked == todayStr) return@launch

            val limit = goalRepository.dailyLimit.first()
            if (limit <= 0.0) return@launch

            val transactions = transactionRepository.getAllTransactions().first()

            val dailySpendMap = transactions
                .filter { it.type == TransactionType.EXPENSE }
                .groupBy { dateFormat.format(Date(it.date)) }
                .mapValues { (_, txs) -> txs.sumOf { it.amount } }

            val newStreak = computeCurrentStreak(limit, dailySpendMap, todayStr)

            goalRepository.setStreakCount(newStreak)
            goalRepository.setLastCheckedDate(todayStr)
        }
    }

    private fun computeCurrentStreak(limit: Double, dailySpend: Map<String, Double>, todayStr: String): Int {
        var streak = 0
        var daysBack = 1
        while (true) {
            val cal = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -daysBack) }
            val dayStr = dateFormat.format(cal.time)
            val spent = dailySpend[dayStr] ?: 0.0
            if (spent <= limit) {
                streak++
                daysBack++
                if (daysBack > 365) break
            } else {
                break
            }
        }
        return streak
    }

    fun setDailyLimit(limit: Double) {
        viewModelScope.launch {
            goalRepository.setDailyLimit(limit)
        }
    }

    class Factory(
        private val goalRepository: GoalRepository,
        private val transactionRepository: TransactionRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return GoalViewModel(goalRepository, transactionRepository) as T
        }
    }
}