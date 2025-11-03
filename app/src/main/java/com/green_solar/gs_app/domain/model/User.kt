package com.green_solar.gs_app.domain.model



/**
 * Modelo de dominio SIN nulos (defaults seguros).
 */
data class User(
    val id: String,
    val username: String,
    val email: String,
    val firstName: String,
    val lastName: String,
    val imageUrl: String
)
