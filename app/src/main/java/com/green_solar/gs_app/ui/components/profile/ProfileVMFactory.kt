package com.green_solar.gs_app.ui.components.profile

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.green_solar.gs_app.data.local.SessionManager
import com.green_solar.gs_app.data.remote.ApiService
import com.green_solar.gs_app.data.remote.RetrofitClient
import com.green_solar.gs_app.data.repository.AuthRepositoryImpl
import com.green_solar.gs_app.data.repository.UserRepositoryImpl

/**
 * Factory for creating ProfileViewModel.
 * It now creates its own dependencies.
 */
class ProfileVMFactory(
    private val application: Application
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            val apiService = RetrofitClient.create(application).create(ApiService::class.java)
            val sessionManager = SessionManager(application)
            val authRepository = AuthRepositoryImpl(apiService, sessionManager)
            val userRepository = UserRepositoryImpl(apiService, sessionManager, application)

            return ProfileViewModel(application, userRepository, authRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
