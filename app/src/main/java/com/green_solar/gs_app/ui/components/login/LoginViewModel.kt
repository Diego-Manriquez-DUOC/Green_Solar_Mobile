package com.green_solar.gs_app.ui.components.login
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.green_solar.gs_app.domain.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(
    private val repo: AuthRepository
) : ViewModel() {

    private val _ui = MutableStateFlow(LoginUiState())
    val ui: StateFlow<LoginUiState> = _ui

    fun onEmailChange(v: String)  = _ui.update { it.copy(email = v) }
    fun onPasswordChange(v: String) = _ui.update { it.copy(password = v) }

    fun login() {
        val (email, password) = _ui.value
        _ui.update { it.copy(isLoading = true, error = null, done = false) }

        viewModelScope.launch {
            repo.login(email, password)
                .onSuccess {
                    _ui.update { it.copy(isLoading = false, done = true) }
                }
                .onFailure { e ->
                    _ui.update {
                        it.copy(
                            isLoading = false,
                            error = e.message ?: "Error de login"
                        )
                    }
                }
        }
    }
}