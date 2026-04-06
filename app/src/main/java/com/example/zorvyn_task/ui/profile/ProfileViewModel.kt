package com.example.zorvyn_task.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.zorvyn_task.data.local.TransactionEntity
import com.example.zorvyn_task.data.local.TransactionType
import com.example.zorvyn_task.data.repository.TransactionRepository
import com.example.zorvyn_task.data.repository.UserRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class ProfileUiState(
    val userName: String = "",
    val userId: String = "",
    val transactionCount: Int = 0,
    val totalIncome: Double = 0.0,
    val totalExpense: Double = 0.0,
    val resetConfirmVisible: Boolean = false,
    val addHistoryVisible: Boolean = false,
    val isLoading: Boolean = false,
    val successMessage: String? = null,
    val errorMessage: String? = null
)

class ProfileViewModel(
    private val userRepository: UserRepository,
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                userRepository.userName,
                userRepository.userId,
                transactionRepository.getAllTransactions()
            ) { name, id, txs ->
                val income = txs.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
                val expense = txs.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }
                _uiState.update {
                    it.copy(
                        userName = name,
                        userId = id,
                        transactionCount = txs.size,
                        totalIncome = income,
                        totalExpense = expense
                    )
                }
            }.collect()
        }
    }

    fun showResetConfirm() = _uiState.update { it.copy(resetConfirmVisible = true) }
    fun hideResetConfirm() = _uiState.update { it.copy(resetConfirmVisible = false) }

    fun resetAllData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, resetConfirmVisible = false) }
            try {
                transactionRepository.deleteAll()
                _uiState.update { it.copy(isLoading = false, successMessage = "All data cleared") }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Failed to clear data: ${e.message}") }
            }
        }
    }

    fun showAddHistory() = _uiState.update { it.copy(addHistoryVisible = true) }
    fun hideAddHistory() = _uiState.update { it.copy(addHistoryVisible = false) }

    fun addPastTransaction(
        amount: Double,
        type: TransactionType,
        category: String,
        note: String?,
        epochMillis: Long
    ) {
        viewModelScope.launch {
            try {
                transactionRepository.insert(
                    TransactionEntity(
                        amount = amount,
                        type = type,
                        category = category,
                        note = note,
                        date = epochMillis
                    )
                )
                _uiState.update { it.copy(successMessage = "Transaction added") }
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = "Failed to add: ${e.message}") }
            }
        }
    }

    fun clearMessage() = _uiState.update { it.copy(successMessage = null, errorMessage = null) }

    class Factory(
        private val userRepository: UserRepository,
        private val transactionRepository: TransactionRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            ProfileViewModel(userRepository, transactionRepository) as T
    }
}