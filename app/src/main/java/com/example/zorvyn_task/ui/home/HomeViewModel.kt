package com.example.zorvyn_task.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.zorvyn_task.data.local.TransactionEntity
import com.example.zorvyn_task.data.local.TransactionType
import com.example.zorvyn_task.data.repository.TransactionRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: TransactionRepository) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    private val _activeFilter = MutableStateFlow(TransactionFilter.ALL)
    private val _editingTransaction = MutableStateFlow<TransactionEntity?>(null)
    private val _showDeleteConfirm = MutableStateFlow<TransactionEntity?>(null)

    val uiState: StateFlow<HomeUiState> = combine(
        repository.getAllTransactions(),
        _searchQuery,
        _activeFilter,
        _editingTransaction,
        _showDeleteConfirm
    ) { transactions, query, filter, editing, deleteConfirm ->
        val income = transactions.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
        val expense = transactions.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }

        val filtered = transactions.filter { tx ->
            val matchesFilter = when (filter) {
                TransactionFilter.ALL -> true
                TransactionFilter.INCOME -> tx.type == TransactionType.INCOME
                TransactionFilter.EXPENSE -> tx.type == TransactionType.EXPENSE
            }
            val matchesQuery = if (query.isBlank()) true else {
                tx.category.contains(query, ignoreCase = true) ||
                        tx.note?.contains(query, ignoreCase = true) == true
            }
            matchesFilter && matchesQuery
        }

        HomeUiState(
            balance = income - expense,
            totalIncome = income,
            totalExpense = expense,
            transactions = transactions,
            filteredTransactions = filtered,
            searchQuery = query,
            activeFilter = filter,
            isLoading = false,
            editingTransaction = editing,
            showDeleteConfirm = deleteConfirm
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = HomeUiState()
    )

    fun setSearchQuery(query: String) { _searchQuery.value = query }

    fun setFilter(filter: TransactionFilter) { _activeFilter.value = filter }

    fun startEdit(transaction: TransactionEntity) { _editingTransaction.value = transaction }

    fun cancelEdit() { _editingTransaction.value = null }

    fun saveEdit(
        id: Int,
        amount: Double,
        type: TransactionType,
        category: String,
        note: String?,
        date: Long
    ) {
        viewModelScope.launch {
            repository.update(
                TransactionEntity(
                    id = id,
                    amount = amount,
                    type = type,
                    category = category,
                    note = note,
                    date = date
                )
            )
            _editingTransaction.value = null
        }
    }

    fun promptDelete(transaction: TransactionEntity) { _showDeleteConfirm.value = transaction }

    fun cancelDelete() { _showDeleteConfirm.value = null }

    fun confirmDelete() {
        viewModelScope.launch {
            _showDeleteConfirm.value?.let { repository.delete(it) }
            _showDeleteConfirm.value = null
        }
    }

    class Factory(private val repository: TransactionRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return HomeViewModel(repository) as T
        }
    }
}