package com.green_solar.gs_app.ui.components.auth

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

    fun onName(v: String) = _ui.update { it.copy(name = v, error = null) }
    fun onEmail(v: String) = _ui.update { it.copy(email = v, error = null) }
    fun onPass(v: String) = _ui.update { it.copy(password = v, error = null) }
    fun onConfirm(v: String) = _ui.update { it.copy(confirm = v, error = null) }

    fun submit() {
        val s = _ui.value
        // Validaciones básicas
        if (s.name.isBlank()) return fail("Ingresa tu nombre")
        if (!s.email.contains("@")) return fail("Email inválido")
        if (s.password.length < 6) return fail("Mínimo 6 caracteres")
        if (s.password != s.confirm) return fail("Las contraseñas no coinciden")

        viewModelScope.launch {
            _ui.update { it.copy(isLoading = true, error = null) }

            authRepo.signup(s.name, s.email, s.password)
                .onSuccess {
                    _ui.update { it.copy(isLoading = false, done = true) }
                }
                .onFailure { e ->
                    fail(e.message ?: "Error desconocido al registrar")
                }
        }
    }

    private fun fail(msg: String) {
        _ui.update { it.copy(isLoading = false, error = msg) }
    }

    fun resetDone() {
        _ui.update { it.copy(done = false) }
    }
}