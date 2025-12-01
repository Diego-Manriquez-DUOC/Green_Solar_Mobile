package com.green_solar.gs_app.domain.model



/**
 * Modelo de dominio para user, basado en la estructura definida en al API.
 */
data class User(
    val user_id: String,
    val username: String,
    val email: String,
    val role : String,
    val imgUrl: String?
)
