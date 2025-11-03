package com.green_solar.gs_app.data.repository

import android.content.Context
import com.green_solar.gs_app.core.utils.toDomain
import com.green_solar.gs_app.data.local.SessionManager
import com.green_solar.gs_app.data.remote.ApiService
import com.green_solar.gs_app.data.remote.RetrofitClient
import com.green_solar.gs_app.data.remote.dto.LoginRequest
import com.green_solar.gs_app.domain.model.User
import com.green_solar.gs_app.domain.repository.AuthRepository

class AuthRepositoryImpl(context: Context) : AuthRepository {
    private val api = RetrofitClient.create(context).create(ApiService::class.java)
    private val session = SessionManager(context)

    override suspend fun login(email: String, password: String): Result<User> = runCatching {
        val res = api.login(LoginRequest(email, password))
        session.saveToken(res.authToken)          // 1) guardas el token
        val me = api.getCurrentUser()             // 2) pides el usuario real
        me.toDomain()                             // 3) mapeas a dominio
    }

    override suspend fun signup(name: String, email: String, password: String): Result<User> = runCatching {
        // La API dummy espera un LoginRequest, por lo que el 'name' no se envía al backend.
        // Se recibe para mantener la consistencia, pero no se usa en la llamada a la API.
        val res = api.signup(LoginRequest(email, password))
        session.saveToken(res.authToken)         //  guarda el token
        api.getCurrentUser().toDomain()          // traemos el User con el token recién guardado
    }

    override suspend fun logout() {
        session.clear()
    }
}
