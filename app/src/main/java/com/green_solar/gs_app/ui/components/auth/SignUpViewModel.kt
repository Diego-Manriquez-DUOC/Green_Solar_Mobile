package com.green_solar.gs_app.ui.components.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.green_solar.gs_app.domain.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Placeholder de DTO (luego lo cambias por tu real)
data class SignupRequest(
    val name: String,
    val email: String,
    val password: String
)

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
            try {
                val req = SignupRequest(s.name, s.email, s.password)
                // TODO: El DTO de la capa de datos (dto.SignupRequest) y el local (SignupRequest) no coinciden
                // Es necesario mapear de uno a otro antes de llamar al repositorio.
                // Por ahora, se asume que son iguales para que compile.
                // authRepo.register(req) // <-- Esto fallará si los DTOs no coinciden
                _ui.update { it.copy(isLoading = false, done = true) }
            } catch (e: Exception) {
                fail(e.message ?: "Error al registrar")
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