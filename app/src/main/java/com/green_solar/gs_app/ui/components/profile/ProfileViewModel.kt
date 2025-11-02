package com.green_solar.gs_app.ui.components.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.green_solar.gs_app.domain.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch




import com.green_solar.gs_app.domain.model.User
import com.green_solar.gs_app.domain.repository.AuthRepository

import retrofit2.HttpException

data class ProfileUiState(
    val isLoading: Boolean = false,
    val user: User? = null,
    val error: String? = null
)

class ProfileViewModel(
    private val userRepo: UserRepository,
    private val authRepo: AuthRepository   // 👈 inyectamos AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState(isLoading = false))
    val uiState: StateFlow<ProfileUiState> = _uiState

    fun loadMe() {
        _uiState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            userRepo.getCurrentUser()
                .onSuccess { user ->
                    _uiState.update { it.copy(isLoading = false, user = user, error = null) }
                }
                .onFailure { e ->
                    val msg = when (e) {
                        is HttpException -> when (e.code()) {
                            401 -> "No autenticado. Inicia sesión."
                            else -> "HTTP ${e.code()}: ${e.message()}"
                        }
                        else -> e.message ?: "Error desconocido"
                    }
                    _uiState.update { it.copy(isLoading = false, error = msg) }
                }
        }
    }

    fun retry() = loadMe()

    /** Cierra sesión limpiando el token en DataStore */
    fun logout() {
        viewModelScope.launch {
            authRepo.logout()   // Internamente hace SessionManager.clear()
        }
    }
}
