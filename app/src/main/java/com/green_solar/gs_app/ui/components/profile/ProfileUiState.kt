package com.green_solar.gs_app.ui.components.profile
import com.green_solar.gs_app.domain.model.User

/*
*
* Anotaciones
*
 */
data class ProfileUiState(
    val isLoading: Boolean = false,
    val user: User? = null,
    val error: String? = null
)
