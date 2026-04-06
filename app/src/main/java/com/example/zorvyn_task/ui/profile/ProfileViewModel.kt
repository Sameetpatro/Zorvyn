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

enum class TransactionFilter { ALL, INCOME, EXPENSE }

data class ProfileUiState(
    val userName: String = "",
    val userId: String = "",
    val transactionCount: Int = 0,
    val totalIncome: Double = 0.0,
    val totalExpense: Double = 0.0,
    val allTransactions: List<TransactionEntity> = emptyList(),
    val searchQuery: String = "",
    val activeFilter: TransactionFilter = TransactionFilter.ALL,
    val resetConfirmVisible: Boolean = false,
    val addHistoryVisible: Boolean = false,
    val isLoading: Boolean = false,
    val successMessage: String? = null,
    val errorMessage: String? = null,
    val editingTransaction: TransactionEntity? = null,
    val showDeleteConfirm: TransactionEntity? = null
)

class ProfileViewModel(
    private val userRepository: UserRepository,
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    private val _activeFilter = MutableStateFlow(TransactionFilter.ALL)
    private val _editingTransaction = MutableStateFlow<TransactionEntity?>(null)
    private val _showDeleteConfirm = MutableStateFlow<TransactionEntity?>(null)
    private val _uiExtras = MutableStateFlow(
        ProfileUiState()
    )

    val uiState: StateFlow<ProfileUiState> = combine(
        userRepository.userName,
        userRepository.userId,
        transactionRepository.getAllTransactions(),
        _searchQuery,
        _activeFilter
    ) { name, id, txs, query, filter ->
        val income = txs.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
        val expense = txs.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }
        ProfileUiState(
            userName = name,
            userId = id,
            transactionCount = txs.size,
            totalIncome = income,
            totalExpense = expense,
            allTransactions = txs,
            searchQuery = query,
            activeFilter = filter
        )
    }.combine(_uiExtras) { base, extras ->
        base.copy(
            resetConfirmVisible = extras.resetConfirmVisible,
            addHistoryVisible = extras.addHistoryVisible,
            isLoading = extras.isLoading,
            successMessage = extras.successMessage,
            errorMessage = extras.errorMessage,
            editingTransaction = extras.editingTransaction,
            showDeleteConfirm = extras.showDeleteConfirm
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ProfileUiState()
    )

    fun setSearchQuery(query: String) { _searchQuery.value = query }
    fun setFilter(filter: TransactionFilter) { _activeFilter.value = filter }

    fun showResetConfirm() = _uiExtras.update { it.copy(resetConfirmVisible = true) }
    fun hideResetConfirm() = _uiExtras.update { it.copy(resetConfirmVisible = false) }

    fun resetAllData() {
        viewModelScope.launch {
            _uiExtras.update { it.copy(isLoading = true, resetConfirmVisible = false) }
            try {
                transactionRepository.deleteAll()
                _uiExtras.update { it.copy(isLoading = false, successMessage = "All data cleared") }
            } catch (e: Exception) {
                _uiExtras.update { it.copy(isLoading = false, errorMessage = "Failed to clear data: ${e.message}") }
            }
        }
    }

    fun showAddHistory() = _uiExtras.update { it.copy(addHistoryVisible = true) }
    fun hideAddHistory() = _uiExtras.update { it.copy(addHistoryVisible = false) }

    fun addPastTransaction(amount: Double, type: TransactionType, category: String, note: String?, epochMillis: Long) {
        viewModelScope.launch {
            try {
                transactionRepository.insert(
                    TransactionEntity(amount = amount, type = type, category = category, note = note, date = epochMillis)
                )
                _uiExtras.update { it.copy(successMessage = "Transaction added") }
            } catch (e: Exception) {
                _uiExtras.update { it.copy(errorMessage = "Failed to add: ${e.message}") }
            }
        }
    }

    fun startEdit(transaction: TransactionEntity) = _uiExtras.update { it.copy(editingTransaction = transaction) }
    fun cancelEdit() = _uiExtras.update { it.copy(editingTransaction = null) }

    fun saveEdit(id: Int, amount: Double, type: TransactionType, category: String, note: String?, date: Long) {
        viewModelScope.launch {
            try {
                transactionRepository.update(
                    TransactionEntity(id = id, amount = amount, type = type, category = category, note = note, date = date)
                )
                _uiExtras.update { it.copy(editingTransaction = null, successMessage = "Transaction updated") }
            } catch (e: Exception) {
                _uiExtras.update { it.copy(errorMessage = "Failed to update: ${e.message}") }
            }
        }
    }

    fun promptDelete(transaction: TransactionEntity) = _uiExtras.update { it.copy(showDeleteConfirm = transaction) }
    fun cancelDelete() = _uiExtras.update { it.copy(showDeleteConfirm = null) }

    fun confirmDelete() {
        viewModelScope.launch {
            try {
                _uiExtras.value.showDeleteConfirm?.let { transactionRepository.delete(it) }
                _uiExtras.update { it.copy(showDeleteConfirm = null, successMessage = "Transaction deleted") }
            } catch (e: Exception) {
                _uiExtras.update { it.copy(showDeleteConfirm = null, errorMessage = "Failed to delete: ${e.message}") }
            }
        }
    }

    fun clearMessage() = _uiExtras.update { it.copy(successMessage = null, errorMessage = null) }

    class Factory(
        private val userRepository: UserRepository,
        private val transactionRepository: TransactionRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            ProfileViewModel(userRepository, transactionRepository) as T
    }
}