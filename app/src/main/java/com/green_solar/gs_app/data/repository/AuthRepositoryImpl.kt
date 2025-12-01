package com.green_solar.gs_app.data.repository

import com.green_solar.gs_app.data.local.SessionManager
import com.green_solar.gs_app.data.remote.ApiService
import com.green_solar.gs_app.data.remote.dto.AuthResponse
import com.green_solar.gs_app.data.remote.dto.LoginRequest
import com.green_solar.gs_app.data.remote.dto.MeResponse
import com.green_solar.gs_app.data.remote.dto.SignupRequest
import com.green_solar.gs_app.domain.model.User
import com.green_solar.gs_app.domain.repository.AuthRepository

class AuthRepositoryImpl(
    private val api: ApiService,
    private val session: SessionManager
) : AuthRepository {

    override suspend fun login(email: String, password: String): Result<User> = runCatching {
        // 1. Authenticate and get token
        val authResponse = api.login(LoginRequest(email, password))
        val token = authResponse.token

        // 2. Use token to get full user data
        val meResponse = api.getCurrentUser("Bearer $token")

        // 3. Combine responses to create domain User
        val user = mapToDomain(authResponse, meResponse)

        // 4. Save session data
        session.saveToken(token)
        session.saveUserId(authResponse.id) // <-- PASO CLAVE AÑADIDO

        user
    }

    override suspend fun signup(name: String, email: String, password: String): Result<User> = runCatching {
        // 1. Register and get token
        val authResponse = api.signup(SignupRequest(name, email, password))
        val token = authResponse.token

        // 2. Use token to get full user data
        val meResponse = api.getCurrentUser("Bearer $token")

        // 3. Combine responses to create domain User
        val user = mapToDomain(authResponse, meResponse)

        // 4. Save session data
        session.saveToken(token)
        session.saveUserId(authResponse.id) // <-- PASO CLAVE AÑADIDO

        user
    }

    override suspend fun logout() {
        session.clear()
    }

    private fun mapToDomain(auth: AuthResponse, me: MeResponse): User {
        return User(
            user_id = auth.id.toString(),
            name = auth.username,
            email = me.email,
            role = me.role,
            img_url = auth.imgUrl
        )
    }
}
