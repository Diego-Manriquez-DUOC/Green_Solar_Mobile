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

    fun onUsername(v: String) = _ui.update { it.copy(username = v, usernameError = null, generalError = null) }
    fun onEmail(v: String)    = _ui.update { it.copy(email = v,    emailError = null, generalError = null) }
    fun onPass(v: String)     = _ui.update { it.copy(password = v,  passwordError = null, generalError = null) }
    fun onConfirm(v: String)  = _ui.update { it.copy(confirm = v,  confirmError = null, generalError = null) }

    fun submit() {
        if (!validate()) return

        viewModelScope.launch {
            _ui.update { it.copy(isLoading = true, generalError = null) }
            val s = _ui.value

            authRepo.signup(username = s.username, email = s.email, password = s.password)
                .onSuccess {
                    _ui.update { it.copy(isLoading = false, done = true) }
                }
                .onFailure { e ->
                    _ui.update { it.copy(isLoading = false, generalError = e.message ?: "Unknown error") }
                }
        }
    }

    private fun validate(): Boolean {
        val s = _ui.value

        val usernameError = if (s.username.isBlank()) "Enter your username" else null
        val emailError = if (!Patterns.EMAIL_ADDRESS.matcher(s.email).matches()) "Invalid email" else null
        val passwordError = if (s.password.length < 6 || !s.password.any { it.isDigit() }) "Minimum 6 characters and 1 number" else null
        val confirmError = if (s.password != s.confirm) "Passwords do not match" else null

        _ui.update {
            it.copy(
                usernameError = usernameError,
                emailError = emailError,
                passwordError = passwordError,
                confirmError = confirmError
            )
        }
        return usernameError == null && emailError == null && passwordError == null && confirmError == null
    }

    fun resetDone() {
        _ui.update { it.copy(done = false) }
    }
}
