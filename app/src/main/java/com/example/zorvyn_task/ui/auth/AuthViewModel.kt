package com.example.zorvyn_task.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.zorvyn_task.data.repository.UserRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class AuthViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                userRepository.userId,
                userRepository.userPinHash,
                userRepository.userName
            ) { id, pin, name ->
                Triple(id, pin, name)
            }.collect { (id, pin, name) ->
                _uiState.update {
                    it.copy(
                        userId = id,
                        pinHash = pin,
                        userName = name,
                        isNewUser = pin.isEmpty(),
                        isLoading = false
                    )
                }
            }
        }
    }

    fun setupNewUser(name: String, pin: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val id = if (_uiState.value.userId.isEmpty()) {
                    userRepository.generateAndStoreUserId()
                } else {
                    _uiState.value.userId
                }
                userRepository.setUserName(name)
                userRepository.setPinHash(userRepository.hashPin(pin))
                userRepository.setAuthenticated(true)
                _uiState.update { it.copy(authSuccess = true, isLoading = false, userId = id) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = "Setup failed: ${e.message}") }
            }
        }
    }

    fun verifyPin(pin: String) {
        viewModelScope.launch {
            val hash = userRepository.hashPin(pin)
            if (hash == _uiState.value.pinHash) {
                userRepository.setAuthenticated(true)
                _uiState.update { it.copy(authSuccess = true, error = null) }
            } else {
                _uiState.update { it.copy(error = "Incorrect PIN") }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    class Factory(private val userRepository: UserRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return AuthViewModel(userRepository) as T
        }
    }
}