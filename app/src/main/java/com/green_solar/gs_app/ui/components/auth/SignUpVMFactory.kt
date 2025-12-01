package com.green_solar.gs_app.ui.components.auth

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.green_solar.gs_app.data.local.SessionManager
import com.green_solar.gs_app.data.remote.RetrofitClient
import com.green_solar.gs_app.data.repository.AuthRepositoryImpl

class SignupVMFactory(
    private val context: Context
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SignupViewModel::class.java)) {
            val api = RetrofitClient.create(context).create(com.green_solar.gs_app.data.remote.ApiService::class.java)
            val session = SessionManager(context)
            val repo = AuthRepositoryImpl(api, session)
            return SignupViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class ${modelClass.name}")
    }
}