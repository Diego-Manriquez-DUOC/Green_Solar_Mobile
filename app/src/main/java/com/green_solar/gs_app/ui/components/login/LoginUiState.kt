package com.green_solar.gs_app.ui.components.login

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val done: Boolean = false
)