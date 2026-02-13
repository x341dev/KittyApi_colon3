package dev.x341.kittyapi_colon3.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "app_settings")

class SettingsPreferences(private val context: Context) {
    companion object {
        val DARK_MODE = booleanPreferencesKey("dark_mode")
        val SHOW_MODE = stringPreferencesKey("show_mode")
        val SHOW_UNNAMED = booleanPreferencesKey("show_unnamed_cats")
    }

    val darkModeFlow: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[DARK_MODE] ?: false
        }

    val showModeFlow: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[SHOW_MODE] ?: "List"
        }

    val showUnnamedCatsFlow: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[SHOW_UNNAMED] ?: false
        }

    suspend fun setDarkMode(isDark: Boolean) {
        context.dataStore.updateData { preferences ->
            preferences.toMutablePreferences().apply {
                this[DARK_MODE] = isDark
            }
        }
    }

    suspend fun setShowMode(mode: String) {
        context.dataStore.updateData { preferences ->
            preferences.toMutablePreferences().apply {
                this[SHOW_MODE] = mode
            }
        }
    }

    suspend fun setShowUnnamedCats(show: Boolean) {
        context.dataStore.updateData { preferences ->
            preferences.toMutablePreferences().apply {
                this[SHOW_UNNAMED] = show
            }
        }
    }
}
