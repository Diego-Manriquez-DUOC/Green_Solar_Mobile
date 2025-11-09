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
            try {
                repo.login(email, password)
                    .onSuccess { _ui.update { it.copy(isLoading = false, done = true) } }
                    .onFailure {
                        // Esto se ejecuta si el repo maneja el error y devuelve un Result.failure
                        _ui.update {
                            it.copy(
                                isLoading = false,
                                error = "El email o la contraseña son incorrectos."
                            )
                        }
                    }
            } catch (e: Exception) {
                // Esto se ejecuta si la llamamos al repo lanza una excepción (como un HTTP 401)
                _ui.update {
                    it.copy(
                        isLoading = false,
                        error = "El email o la contraseña son incorrectos."
                    )
                }
            }
        }
    }

    private fun validateLogin(): Boolean {
        val s = _ui.value

        val emailError = when {
            s.email.isBlank() -> "Introduzca su email"
            !Patterns.EMAIL_ADDRESS.matcher(s.email).matches() -> "El formato del email es inválido"
            else -> null
        }

        val passwordError = when {
            s.password.isEmpty() -> "Introduzca su contraseña"
            s.password.length < 6 -> "La contraseña debe tener al menos 6 caracteres"
            !s.password.any { it.isDigit() } -> "La contraseña debe contener al menos un número"
            else -> null
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
