package com.example.zorvyn_task.ui.addtransaction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.zorvyn_task.data.local.TransactionEntity
import com.example.zorvyn_task.data.local.TransactionType
import com.example.zorvyn_task.data.repository.TransactionRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class AddTransactionViewModel(
    private val repository: TransactionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddTransactionUiState())
    val uiState: StateFlow<AddTransactionUiState> = _uiState.asStateFlow()

    fun setAmount(amount: String) {
        _uiState.update { it.copy(amount = amount, error = null) }
    }

    fun setType(type: TransactionType) {
        _uiState.update { it.copy(type = type, category = "", error = null) }
    }

    fun setCategory(category: String) {
        _uiState.update { it.copy(category = category, error = null) }
    }

    fun setNote(note: String) {
        _uiState.update { it.copy(note = note) }
    }

    fun save() {
        val state = _uiState.value
        val amount = state.amount.toDoubleOrNull()

        when {
            amount == null || amount <= 0 ->
                _uiState.update { it.copy(error = "Please enter a valid amount") }
            state.category.isBlank() ->
                _uiState.update { it.copy(error = "Please select a category") }
            else -> {
                viewModelScope.launch {
                    _uiState.update { it.copy(isSaving = true) }
                    try {
                        repository.insert(
                            TransactionEntity(
                                amount = amount,
                                type = state.type,
                                category = state.category,
                                date = System.currentTimeMillis(),
                                note = state.note.ifBlank { null }
                            )
                        )
                        _uiState.update { it.copy(saved = true, isSaving = false) }
                    } catch (e: Exception) {
                        _uiState.update {
                            it.copy(isSaving = false, error = "Failed to save: ${e.message}")
                        }
                    }
                }
            }
        }
    }

    class Factory(private val repository: TransactionRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return AddTransactionViewModel(repository) as T
        }
    }
}