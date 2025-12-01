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
        // 1. Autenticar y obtener el token
        val authResponse = api.login(LoginRequest(email, password))
        val token = authResponse.token

        // 2. Usar el token para obtener los datos completos del usuario
        val meResponse = api.getCurrentUser("Bearer $token")

        // 3. Combinar respuestas para crear el usuario de dominio
        val user = mapToDomain(authResponse, meResponse)

        // 4. Guardar el token en la sesión
        session.saveToken(token)

        user
    }

    override suspend fun signup(name: String, email: String, password: String): Result<User> = runCatching {
        // 1. Registrar y obtener el token
        val authResponse = api.signup(SignupRequest(name, email, password))
        val token = authResponse.token

        // 2. Usar el token para obtener los datos completos del usuario
        val meResponse = api.getCurrentUser("Bearer $token")

        // 3. Combinar respuestas para crear el usuario de dominio
        val user = mapToDomain(authResponse, meResponse)

        // 4. Guardar el token en la sesión
        session.saveToken(token)

        user
    }

    override suspend fun logout() {
        session.clear()
    }

    /**
     * Función privada que mapea las respuestas de la API a un objeto User de dominio.
     */
    private fun mapToDomain(auth: AuthResponse, me: MeResponse): User {
        return User(
            user_id = auth.id.toString(), // Convertimos el Long a String
            name = auth.username,           // Usamos el username de la respuesta de autenticación
            email = me.email,               // Usamos el email de la respuesta /me
            role = me.role,                 // Usamos el rol de la respuesta /me
            img_url = auth.imgUrl
        )
    }
}
