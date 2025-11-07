package com.green_solar.gs_app.data.remote.dto

// Basado en la estructura de user de la API.
data class UserDto(
    val user_id: String,
    val name: String,
    val email: String,
    val img_url: String?
)
