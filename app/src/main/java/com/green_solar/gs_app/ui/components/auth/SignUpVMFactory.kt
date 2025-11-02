package com.green_solar.gs_app.ui.components.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.green_solar.gs_app.domain.repository.AuthRepository

class SignupVMFactory(
    private val repo: AuthRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SignupViewModel::class.java)) {
            return SignupViewModel(authRepo = repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
