package com.nagarseva.app.util

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.themeDataStore: DataStore<Preferences> by preferencesDataStore(name = "theme_prefs")

@Singleton
class ThemeManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        val KEY_DARK_MODE = booleanPreferencesKey("dark_mode_enabled")
    }
    
    val isDarkModeFlow: Flow<Boolean> = context.themeDataStore.data.map { prefs ->
        prefs[KEY_DARK_MODE] ?: false
    }
    
    suspend fun setDarkMode(enabled: Boolean) {
        context.themeDataStore.edit { prefs ->
            prefs[KEY_DARK_MODE] = enabled
        }
    }
}
