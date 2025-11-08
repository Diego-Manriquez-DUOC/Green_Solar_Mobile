package com.green_solar.gs_app.ui.components.login
import android.util.Patterns
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

    fun onEmailChange(v: String)  = _ui.update { it.copy(email = v, emailError = null) }

    fun onPasswordChange(v: String) = _ui.update { it.copy(password = v, passwordError = null) }

    fun login() {
        if (!validateLogin()) {
            return
        }
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
    private fun validateLogin(): Boolean {
        val s = _ui.value

        val emailError = if (!Patterns.EMAIL_ADDRESS.matcher(s.email).matches()) "Email inválido" else null
        val passwordError = if (s.password.isEmpty()) {
            "Introduzca su contraseña porfavor"
        } else if (s.password.length < 6 || !s.password.any { it.isDigit() }) {
            "Mínimo 6 caracteres y un numero"
        } else {
            null
        }

        _ui.update {
            it.copy(

                emailError = emailError,
                passwordError = passwordError,

            )
        }
        return emailError == null && passwordError == null
    }
}