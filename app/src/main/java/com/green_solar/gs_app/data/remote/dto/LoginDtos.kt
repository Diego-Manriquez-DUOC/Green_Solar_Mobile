package com.green_solar.gs_app.data.remote.dto

// La API solo pide email y password para login, para signup pide email, password y name.
data class LoginRequest(val email: String, val password: String)

data class SignupRequest(val name: String, val email: String, val password: String)

// Lo que devuelve el api en login
data class LoginResponseDto(
    val access_token: String,
    val message: String,
    val user: UserDto
)

data class SignupResponseDto(
    val access_token: String,
    val message: String,
    val user: UserDto
)

data class MeResponse(
    val user: UserDto
)