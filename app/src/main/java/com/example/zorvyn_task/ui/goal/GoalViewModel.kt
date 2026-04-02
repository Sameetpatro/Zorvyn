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

    // Called once on app open to evaluate streak for today
    fun evaluateStreakForToday() {
        viewModelScope.launch {
            val todayStr = dateFormat.format(Date())
            val lastChecked = goalRepository.lastCheckedDate.first()

            if (lastChecked == todayStr) return@launch // Already evaluated today

            val limit = goalRepository.dailyLimit.first()
            if (limit <= 0.0) return@launch // Limit not set, skip

            val transactions = transactionRepository.getAllTransactions().first()
            val yesterdayStr = dateFormat.format(
                Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }.time
            )
            val yesterdaySpent = transactions.filter { tx ->
                tx.type == TransactionType.EXPENSE &&
                        dateFormat.format(Date(tx.date)) == yesterdayStr
            }.sumOf { it.amount }

            val currentStreak = goalRepository.streakCount.first()
            val newStreak = if (yesterdaySpent <= limit) currentStreak + 1 else 0

            goalRepository.setStreakCount(newStreak)
            goalRepository.setLastCheckedDate(todayStr)
        }
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