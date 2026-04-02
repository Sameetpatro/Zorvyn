package com.example.zorvyn_task.data.local

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.security.SecureRandom
import java.util.Base64

private val Context.userDataStore by preferencesDataStore(name = "user_prefs")

class UserPreferences(private val context: Context) {

    companion object {
        val USER_ID = stringPreferencesKey("user_id")
        val IS_AUTHENTICATED = booleanPreferencesKey("is_authenticated")
        val USER_PIN_HASH = stringPreferencesKey("user_pin_hash")
        val USER_NAME = stringPreferencesKey("user_name")
    }

    val userId: Flow<String> = context.userDataStore.data
        .map { prefs -> prefs[USER_ID] ?: "" }

    val isAuthenticated: Flow<Boolean> = context.userDataStore.data
        .map { prefs -> prefs[IS_AUTHENTICATED] ?: false }

    val userPinHash: Flow<String> = context.userDataStore.data
        .map { prefs -> prefs[USER_PIN_HASH] ?: "" }

    val userName: Flow<String> = context.userDataStore.data
        .map { prefs -> prefs[USER_NAME] ?: "" }

    suspend fun generateAndStoreUserId(): String {
        val random = SecureRandom()
        val bytes = ByteArray(24)
        random.nextBytes(bytes)
        val id = "ZRV-" + Base64.getUrlEncoder().withoutPadding().encodeToString(bytes)
        context.userDataStore.edit { prefs -> prefs[USER_ID] = id }
        return id
    }

    suspend fun setAuthenticated(auth: Boolean) {
        context.userDataStore.edit { prefs -> prefs[IS_AUTHENTICATED] = auth }
    }

    suspend fun setPinHash(hash: String) {
        context.userDataStore.edit { prefs -> prefs[USER_PIN_HASH] = hash }
    }

    suspend fun setUserName(name: String) {
        context.userDataStore.edit { prefs -> prefs[USER_NAME] = name }
    }

    fun hashPin(pin: String): String {
        val digest = java.security.MessageDigest.getInstance("SHA-256")
        val hash = digest.digest(pin.toByteArray())
        return Base64.getEncoder().encodeToString(hash)
    }
}