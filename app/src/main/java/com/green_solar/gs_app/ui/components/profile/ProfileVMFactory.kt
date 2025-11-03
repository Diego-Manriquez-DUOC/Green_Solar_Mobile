package com.green_solar.gs_app.ui.components.profile

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.green_solar.gs_app.domain.repository.AuthRepository
import com.green_solar.gs_app.domain.repository.UserRepository

/**
 * Fábrica para crear un ProfileViewModel.
 * Ahora también necesita la Application para pasársela al ViewModel.
 */
class ProfileVMFactory(
    private val application: Application, // <-- La fábrica ahora recibe la Application
    private val userRepo: UserRepository,
    private val authRepo: AuthRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            // Y se la pasa al constructor del ViewModel
            return ProfileViewModel(application, userRepo, authRepo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
