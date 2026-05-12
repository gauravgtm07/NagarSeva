package com.nagarseva.app.util

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "nagarseva_session")

@Singleton
class SessionManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        val KEY_USER_ID = stringPreferencesKey("user_id")
        val KEY_USER_EMAIL = stringPreferencesKey("user_email")
        val KEY_USER_NAME = stringPreferencesKey("user_name")
        val KEY_IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        val KEY_LOGIN_TIME = longPreferencesKey("login_time")
    }
    
    // SAVE session after successful login
    suspend fun saveSession(
        userId: String,
        email: String,
        fullName: String
    ) {
        context.dataStore.edit { preferences ->
            preferences[KEY_USER_ID] = userId
            preferences[KEY_USER_EMAIL] = email
            preferences[KEY_USER_NAME] = fullName
            preferences[KEY_IS_LOGGED_IN] = true
            preferences[KEY_LOGIN_TIME] = System.currentTimeMillis()
        }
    }
    
    // GET current user ID (returns Flow)
    val userIdFlow: Flow<String?> = 
        context.dataStore.data.map { preferences ->
            preferences[KEY_USER_ID]
        }
    
    // GET current user ID (one-shot suspend)
    suspend fun getUserId(): String? {
        return context.dataStore.data.first()[KEY_USER_ID]
    }
    
    // GET user name
    suspend fun getUserName(): String? {
        return context.dataStore.data.first()[KEY_USER_NAME]
    }
    
    // GET user email
    suspend fun getUserEmail(): String? {
        return context.dataStore.data.first()[KEY_USER_EMAIL]
    }
    
    // CHECK if logged in (Flow for observing)
    val isLoggedInFlow: Flow<Boolean> = 
        context.dataStore.data.map { preferences ->
            preferences[KEY_IS_LOGGED_IN] ?: false
        }
    
    // CHECK if logged in (one-shot)
    suspend fun isLoggedIn(): Boolean {
        return context.dataStore.data.first()[KEY_IS_LOGGED_IN] ?: false
    }
    
    // CLEAR session on logout
    suspend fun clearSession() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
    
    // UPDATE user name (after profile edit)
    suspend fun updateUserName(newName: String) {
        context.dataStore.edit { preferences ->
            preferences[KEY_USER_NAME] = newName
        }
    }
}
