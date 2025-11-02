
package com.green_solar.gs_app.data.local
import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class SessionManager(private val context: Context) {
    companion object {
        private val Context.dataStore by preferencesDataStore("session_prefs")
        private val KEY_AUTH = stringPreferencesKey("auth_token")
    }
    suspend fun saveToken(token: String) = context.dataStore.edit { it[KEY_AUTH] = token }
    suspend fun getToken(): String? = context.dataStore.data.map { it[KEY_AUTH] }.first()
    suspend fun clear() = context.dataStore.edit { it.remove(KEY_AUTH) }
}