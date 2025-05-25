// SettingsDataStore.kt
package com.example.taskmanager

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "settings")

object SettingsDataStore {
    private val DARK_MODE_KEY = booleanPreferencesKey("dark_mode")

    fun getThemeFlow(context: Context): Flow<Boolean> =
        context.dataStore.data.map { preferences ->
            preferences[DARK_MODE_KEY] ?: false // default to light
        }

    suspend fun setDarkMode(context: Context, isDark: Boolean) {
        context.dataStore.edit { settings ->
            settings[DARK_MODE_KEY] = isDark
        }
    }
}
