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
        val authResponse = api.login(LoginRequest(email, password))
        val token = authResponse.token
        val meResponse = api.getCurrentUser("Bearer $token")
        val user = mapToDomain(authResponse, meResponse)

        // Guardar toda la información del usuario en la sesión
        session.saveToken(token)
        session.saveUserId(authResponse.id)
        user.imgUrl?.let { session.saveAvatarUri(it) } // <-- ¡CORRECCIÓN AÑADIDA!

        user
    }

    override suspend fun signup(username: String, email: String, password: String): Result<User> = runCatching {
        val authResponse = api.signup(SignupRequest(username, email, password))
        val token = authResponse.token
        val meResponse = api.getCurrentUser("Bearer $token")
        val user = mapToDomain(authResponse, meResponse)

        // Guardar toda la información del usuario en la sesión
        session.saveToken(token)
        session.saveUserId(authResponse.id)
        user.imgUrl?.let { session.saveAvatarUri(it) } // <-- ¡CORRECCIÓN AÑADIDA!

        user
    }

    override suspend fun logout() {
        session.clear()
    }

    // MODIFIED: Aligned with the User.kt data class
    private fun mapToDomain(auth: AuthResponse, me: MeResponse): User {
        return User(
            user_id = auth.id.toString(),
            username = auth.username, // Correct: was name
            email = me.email,
            role = me.role,
            imgUrl = auth.imgUrl      // Correct: was img_url
        )
    }
}
