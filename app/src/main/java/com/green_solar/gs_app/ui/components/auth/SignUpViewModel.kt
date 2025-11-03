package com.green_solar.gs_app.ui.components.auth

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.green_solar.gs_app.domain.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SignupViewModel(
    private val authRepo: AuthRepository
) : ViewModel() {

    private val _ui = MutableStateFlow(SignupUiState())
    val ui: StateFlow<SignupUiState> = _ui

    fun onName(v: String)    = _ui.update { it.copy(name = v,    nameError = null, generalError = null) }
    fun onEmail(v: String)   = _ui.update { it.copy(email = v,   emailError = null, generalError = null) }
    fun onPass(v: String)    = _ui.update { it.copy(password = v, passwordError = null, generalError = null) }
    fun onConfirm(v: String) = _ui.update { it.copy(confirm = v, confirmError = null, generalError = null) }

    fun submit() {
        // 1) validación local
        if (!validate()) return

        // 2) llamada al repo
        viewModelScope.launch {
            _ui.update { it.copy(isLoading = true, generalError = null) }
            val s = _ui.value

            // 👇 Ajusta esta firma si tu AuthRepository devuelve otro tipo
            // Recomendado: Result<Unit> o Result<LoginResponse>
            authRepo.signup(name = s.name, email = s.email, password = s.password)
                .onSuccess {
                    _ui.update { it.copy(isLoading = false, done = true) }
                }
                .onFailure { e ->
                    _ui.update { it.copy(isLoading = false, generalError = e.message ?: "Error desconocido") }
                }
        }
    }

    private fun validate(): Boolean {
        val s = _ui.value

        val nameError = if (s.name.isBlank()) "Ingresa tu nombre" else null
        val emailError = if (!Patterns.EMAIL_ADDRESS.matcher(s.email).matches()) "Email inválido" else null
        val passwordError = if (s.password.length < 6 || !s.password.any { it.isDigit() }) "Mínimo 6 caracteres y 1 número" else null
        val confirmError = if (s.password != s.confirm) "Las contraseñas no coinciden" else null

        _ui.update {
            it.copy(
                nameError = nameError,
                emailError = emailError,
                passwordError = passwordError,
                confirmError = confirmError
            )
        }
        return nameError == null && emailError == null && passwordError == null && confirmError == null
    }

    fun resetDone() {
        _ui.update { it.copy(done = false) }
    }
}
