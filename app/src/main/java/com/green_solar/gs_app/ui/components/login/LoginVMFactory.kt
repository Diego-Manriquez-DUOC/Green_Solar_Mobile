package com.green_solar.gs_app.ui.components.login

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.green_solar.gs_app.data.remote.RetrofitClient
import com.green_solar.gs_app.data.local.SessionManager
import com.green_solar.gs_app.data.repository.AuthRepositoryImpl

class LoginVMFactory(
    private val context: Context
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // Creates its own dependencies
        val api = RetrofitClient.create(context).create(com.green_solar.gs_app.data.remote.ApiService::class.java)
        val session = SessionManager(context)
        val repo = AuthRepositoryImpl(api, session)
        return LoginViewModel(repo) as T
    }
}