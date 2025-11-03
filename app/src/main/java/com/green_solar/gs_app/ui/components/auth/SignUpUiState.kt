package com.green_solar.gs_app.ui.components.auth

data class SignupUiState(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val confirm: String = "",

    val isLoading: Boolean = false,
    val done: Boolean = false,

    // errores por campo
    val nameError: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null,
    val confirmError: String? = null,
    // error general (API/red)
    val generalError: String? = null
)
