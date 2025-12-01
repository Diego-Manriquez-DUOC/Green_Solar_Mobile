package com.green_solar.gs_app.data.remote.dto

// La API solo pide email y password para login, para signup pide email, password y name.
data class LoginRequest(val email: String, val password: String)

// 1. Añadimos el campo "role" a la PETICIÓN de registro.
data class SignupRequest(
    val username: String,
    val email: String, 
    val password: String
)

// Lo que devuelve el api en login
data class AuthResponse(
    val id : Long,
    val token : String,
    val username : String,
    val imgUrl : String?
)
data class MeResponse(
    val username : String,
    val role : String,
    val email : String,
    val imgUrl : String?
)
