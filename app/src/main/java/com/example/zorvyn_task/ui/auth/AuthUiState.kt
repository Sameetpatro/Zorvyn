package com.example.zorvyn_task.ui.auth

data class AuthUiState(
    val userId: String = "",
    val userName: String = "",
    val isNewUser: Boolean = false,
    val isLoading: Boolean = true,
    val error: String? = null,
    val authSuccess: Boolean = false,
    val pinHash: String = ""
)