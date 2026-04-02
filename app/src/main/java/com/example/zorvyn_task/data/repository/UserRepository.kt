package com.example.zorvyn_task.data.repository

import com.example.zorvyn_task.data.local.UserPreferences
import kotlinx.coroutines.flow.Flow

class UserRepository(private val prefs: UserPreferences) {

    val userId: Flow<String> = prefs.userId
    val isAuthenticated: Flow<Boolean> = prefs.isAuthenticated
    val userPinHash: Flow<String> = prefs.userPinHash
    val userName: Flow<String> = prefs.userName

    suspend fun generateAndStoreUserId(): String = prefs.generateAndStoreUserId()
    suspend fun setAuthenticated(auth: Boolean) = prefs.setAuthenticated(auth)
    suspend fun setPinHash(hash: String) = prefs.setPinHash(hash)
    suspend fun setUserName(name: String) = prefs.setUserName(name)
    fun hashPin(pin: String): String = prefs.hashPin(pin)
}