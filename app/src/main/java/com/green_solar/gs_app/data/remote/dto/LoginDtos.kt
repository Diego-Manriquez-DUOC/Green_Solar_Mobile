package com.green_solar.gs_app.data.remote.dto

import com.google.gson.annotations.SerializedName

// Ejemplo: ajusta los nombres exactos a lo que te entrega Xano
data class LoginRequest(val email: String, val password: String)

data class SignupRequest(val username: String, val email: String, val password: String)

// Lo que devuelve el api en login
data class LoginResponseDto(
    val authToken: String,
    val user_id: Int?    // el api devuelve user_id, no el objeto user asi que lo agregamos
)

data class SignupResponseDto(
    val authToken: String,
    val user_id: Int
)