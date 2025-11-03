package com.green_solar.gs_app.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "session_prefs")

class SessionManager(private val context: Context) {
    private val KEY_TOKEN = stringPreferencesKey("auth_token")
    private val KEY_AVATAR_URI = stringPreferencesKey("avatar_uri") // <-- Clave para la URI del avatar

    suspend fun saveToken(token: String) {
        context.dataStore.edit { it[KEY_TOKEN] = token }
    }

    suspend fun getToken(): String? =
        context.dataStore.data.map { it[KEY_TOKEN] }.first()

    suspend fun clear() {
        context.dataStore.edit { preferences ->
            preferences.remove(KEY_TOKEN)
            preferences.remove(KEY_AVATAR_URI) // <-- También borramos la URI al hacer logout
        }
    }

    suspend fun hasToken(): Boolean = !getToken().isNullOrBlank()

    // --- Funcionalidad para el Avatar ---

    /**
     * Guarda la URI (convertida a String) de la nueva foto de perfil.
     */
    suspend fun saveAvatarUri(uriString: String) {
        context.dataStore.edit { it[KEY_AVATAR_URI] = uriString }
    }

    /**
     * Expone un Flow que emite la URI del avatar cada vez que cambia.
     * La pantalla escuchará de este Flow para actualizar la imagen.
     */
    val avatarUriFlow: Flow<String?> = context.dataStore.data.map { it[KEY_AVATAR_URI] }
}
