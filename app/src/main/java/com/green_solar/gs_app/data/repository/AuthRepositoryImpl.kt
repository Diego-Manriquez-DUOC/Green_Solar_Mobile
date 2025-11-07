package com.green_solar.gs_app.data.repository

import android.content.Context
import com.green_solar.gs_app.core.utils.toDomain
import com.green_solar.gs_app.data.local.SessionManager
import com.green_solar.gs_app.data.remote.ApiService
import com.green_solar.gs_app.data.remote.RetrofitClient
import com.green_solar.gs_app.data.remote.dto.LoginRequest
import com.green_solar.gs_app.data.remote.dto.SignupRequest
import com.green_solar.gs_app.domain.model.User
import com.green_solar.gs_app.domain.repository.AuthRepository

class AuthRepositoryImpl(context: Context) : AuthRepository {
    /**
     *  RetrofitClient es el cliente que se encarga de hacer las llamadas a la API
     *  ApiService es la interfaz que define los endpoints de la API
    */
    private val api = RetrofitClient.create(context).create(ApiService::class.java)
    private val session = SessionManager(context)

    override suspend fun signup(name: String, email: String, password: String): Result<User> = runCatching {
        val signupRequest = SignupRequest(name, email, password)
        val response = api.signup(signupRequest) // 1. Llama a la API

        // 2. Guarda el token
        session.saveToken(response.access_token)

        // 3. Guarda el objeto user de la respuesta
        response.user.toDomain()
    }

    override suspend fun login(email: String, password: String): Result<User> = runCatching {
        val loginRequest = LoginRequest(email, password)
        val response = api.login(loginRequest) // 1. Llama a la API

        // 2. Guarda el token
        session.saveToken(response.access_token)

        // 3. Guarda el objeto user de la respuesta
        response.user.toDomain()
    }

    override suspend fun logout() {
        session.clear()
    }
}
