package com.green_solar.gs_app.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "session_prefs")

class SessionManager(private val context: Context) {
    private val KEY_TOKEN = stringPreferencesKey("auth_token")
    private val KEY_AVATAR_URI = stringPreferencesKey("avatar_uri")
    private val KEY_USER_ID = longPreferencesKey("user_id") // <-- NUEVA CLAVE PARA EL ID

    suspend fun saveToken(token: String) {
        context.dataStore.edit { it[KEY_TOKEN] = token }
    }

    suspend fun getToken(): String? =
        context.dataStore.data.map { it[KEY_TOKEN] }.first()

    // --- NUEVAS FUNCIONES PARA EL USER ID ---
    /**
     * Guarda el ID del usuario en la sesión.
     */
    suspend fun saveUserId(userId: Long) {
        context.dataStore.edit { it[KEY_USER_ID] = userId }
    }

    /**
     * Obtiene el ID del usuario de la sesión.
     */
    suspend fun getUserId(): Long? {
        return context.dataStore.data.map { it[KEY_USER_ID] }.first()
    }

    suspend fun clear() {
        context.dataStore.edit { preferences ->
            preferences.remove(KEY_TOKEN)
            preferences.remove(KEY_AVATAR_URI)
            preferences.remove(KEY_USER_ID) // <-- También borramos el ID al hacer logout
        }
    }

    suspend fun hasToken(): Boolean = !getToken().isNullOrBlank()

    // --- Funcionalidad para el Avatar ---

    suspend fun saveAvatarUri(uriString: String) {
        context.dataStore.edit { it[KEY_AVATAR_URI] = uriString }
    }

    val avatarUriFlow: Flow<String?> = context.dataStore.data.map { it[KEY_AVATAR_URI] }
}
