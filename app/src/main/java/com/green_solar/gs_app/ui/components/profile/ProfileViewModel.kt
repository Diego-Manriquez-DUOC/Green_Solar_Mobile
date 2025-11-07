package com.green_solar.gs_app.ui.components.profile

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.green_solar.gs_app.domain.repository.AuthRepository
import com.green_solar.gs_app.domain.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

class ProfileViewModel(
    application: Application,
    private val userRepo: UserRepository,
    private val authRepo: AuthRepository
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(ProfileUiState(isLoading = true))
    val uiState = _uiState.asStateFlow()

    fun loadMe() {
        _uiState.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch(Dispatchers.IO) {
            val result = userRepo.getCurrentUser()

            withContext(Dispatchers.Main) {
                result.onSuccess {
                    _uiState.update { s -> s.copy(isLoading = false, user = it, error = null) }
                }.onFailure {
                    handleError(it)
                }
            }
        }
    }

    //Ahora la API se encarga de almacenar la foto de perfil del usuario.
    fun onAvatarChange(uri: Uri) {
        _uiState.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch(Dispatchers.IO) {
            val result = userRepo.updateProfileImage(uri)

            withContext(Dispatchers.Main) {
                result.onSuccess {
                    _uiState.update { s -> s.copy(isLoading = false, user = it, error = null) }
                }.onFailure {
                    handleError(it)
                }
            }
        }
    }

    private fun handleError(exception: Throwable) {
        val msg = when (exception) {
            is HttpException -> "Error del servidor: ${exception.code()} ${exception.message()}"
            is IOException -> "Sin conexión a Internet"
            is IllegalStateException -> "Error: ${exception.message}"
            else -> exception.message ?: "Error inesperado"
        }
        _uiState.update { s -> s.copy(isLoading = false, error = msg) }
    }

    fun retry() = loadMe()
    fun logout() {
        viewModelScope.launch {
            authRepo.logout()
        }
    }
}