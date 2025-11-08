package com.green_solar.gs_app.ui.components.login

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val error: String? = null, // Para errores generales de login
    val done: Boolean = false,

    // Campos para los errores de validación de cada campo
    val emailError: String? = null,
    val passwordError: String? = null
)
