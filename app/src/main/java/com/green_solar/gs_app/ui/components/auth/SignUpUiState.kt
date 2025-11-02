package com.green_solar.gs_app.ui.components.auth

data class SignupUiState(
    val isLoading: Boolean = false,
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val confirm: String = "",
    val error: String? = null,
    val done: Boolean = false
)