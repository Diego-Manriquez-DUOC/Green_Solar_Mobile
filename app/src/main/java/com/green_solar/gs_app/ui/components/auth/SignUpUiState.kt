package com.green_solar.gs_app.ui.components.auth

data class SignupUiState(
    val username: String = "", // Changed from name
    val email: String = "",
    val password: String = "",
    val confirm: String = "",

    val isLoading: Boolean = false,
    val done: Boolean = false,

    // errores por campo
    val usernameError: String? = null, // Changed from nameError
    val emailError: String? = null,
    val passwordError: String? = null,
    val confirmError: String? = null,
    // error general (API/red)
    val generalError: String? = null
)
