package com.green_solar.gs_app.ui.components.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.green_solar.gs_app.domain.repository.AuthRepository
import com.green_solar.gs_app.domain.repository.UserRepository

class ProfileVMFactory(
    private val userRepo: UserRepository,
    private val authRepo: AuthRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ProfileViewModel(userRepo, authRepo) as T
    }
}
