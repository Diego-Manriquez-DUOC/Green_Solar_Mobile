package com.green_solar.gs_app.data.remote.dto

// La API solo pide email y password para login, para signup pide email, password y name.
data class LoginRequest(val email: String, val password: String)

// 1. Añadimos el campo "role" a la PETICIÓN de registro.
data class SignupRequest(
    val name: String, 
    val email: String, 
    val password: String,
    val role: String? = null // Será "USER" por defecto
)

// Lo que devuelve el api en login
data class LoginResponseDto(
    val access_token: String,
    val message: String,
    val user: UserDto
)

// 2. Quitamos el "role" de aquí, porque ya viene dentro de "user".
data class SignupResponseDto(
    val access_token: String,
    val message: String,
    val user: UserDto 
)

data class MeResponse(
    val user: UserDto
)
