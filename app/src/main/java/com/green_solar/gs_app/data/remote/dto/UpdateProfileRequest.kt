package com.green_solar.gs_app.data.remote.dto

data class UpdateProfileRequest(
    val firstName: String,
    val lastName: String
    // Para actualizar perfil muy sencillo cuando utilicemos una API bien hecha, podríamos agregar más campos aquí.
)
