package com.green_solar.gs_app.ui.components.profile

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.green_solar.gs_app.core.utils.saveImageToInternalStorage
import com.green_solar.gs_app.data.local.SessionManager
import com.green_solar.gs_app.domain.repository.AuthRepository
import com.green_solar.gs_app.domain.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

class ProfileViewModel(
    application: Application,
    private val userRepo: UserRepository,
    private val authRepo: AuthRepository
) : AndroidViewModel(application) {

    private val sessionManager = SessionManager(application)

    private val _apiUserState = MutableStateFlow(ProfileUiState(isLoading = true))

    // --- ¡AQUÍ ESTÁ LA MAGIA! ---
    // Combinamos los datos del usuario de la API con la URI del avatar local.
    val uiState: StateFlow<ProfileUiState> = combine(
        _apiUserState,
        sessionManager.avatarUriFlow
    ) { apiState, localAvatarUri ->
        // Si hay una URI local, esa tiene prioridad. Si no, usamos la de la API.
        val finalImageUrl = localAvatarUri ?: apiState.user?.imageUrl
        // Creamos un nuevo estado con la imagen correcta.
        apiState.copy(user = apiState.user?.copy(imageUrl = finalImageUrl ?: ""))
    }.stateIn( // Convertimos el Flow combinado en un StateFlow que la UI pueda usar.
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ProfileUiState(isLoading = true)
    )

    fun loadMe() {
        _apiUserState.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch(Dispatchers.IO) {
            val result = runCatching { userRepo.getCurrentUser().getOrThrow() }

            withContext(Dispatchers.Main) {
                result.onSuccess {
                    _apiUserState.update { s -> s.copy(isLoading = false, user = it, error = null) }
                }.onFailure {
                    val msg = when (it) {
                        is HttpException -> if (it.code() == 401) "Sesión caducada" else "Error del servidor (${it.code()})"
                        is IOException -> "Sin conexión"
                        else -> it.message ?: "Error inesperado"
                    }
                    _apiUserState.update { s -> s.copy(isLoading = false, user = null, error = msg) }
                }
            }
        }
    }

    /**
     * Se llama cuando el usuario elige una nueva foto de la galería o la cámara.
     */
    fun onAvatarChange(uri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            // 1. Guarda la imagen en el almacenamiento interno y obtiene su nueva URI permanente.
            val permanentUri = saveImageToInternalStorage(getApplication(), uri)
            permanentUri?.let {
                // 2. Guarda la nueva URI en el SessionManager.
                sessionManager.saveAvatarUri(it.toString())
            }
        }
    }

    fun retry() = loadMe()
    fun logout() {
        viewModelScope.launch {
            authRepo.logout()
        }
    }
}
