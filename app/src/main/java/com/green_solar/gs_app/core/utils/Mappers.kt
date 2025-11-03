package com.green_solar.gs_app.core.utils

import com.green_solar.gs_app.data.remote.dto.UserDto
import com.green_solar.gs_app.domain.model.User

fun UserDto.toDomain(): User {
    // 1. Lógica para derivar el nombre y apellido.
    val (finalFirstName, finalLastName) = when {
        !name.isNullOrBlank() -> {
            val parts = name.trim().split(" ", limit = 2)
            val f = parts.getOrNull(0) ?: ""
            val l = parts.getOrNull(1) ?: ""
            f to l
        }
        else -> (firstName ?: "") to (lastName ?: "")
    }

    // 2. Lógica para el nombre de usuario (segura contra nulos).
    val safeUsername = username?.takeIf { it.isNotBlank() }
        ?: email?.substringBefore('@')?.takeIf { it.isNotBlank() }
        ?: "usuario"

    // 3. Lógica para la imagen de perfil (segura contra nulos).
    val safeImageUrl = avatarUrl?.takeIf { it.isNotBlank() } ?: ""

    // 4. Construye el objeto User, asegurando que no haya nulos.
    return User(
        id = id ?: "",
        username = safeUsername,
        email = email ?: "",
        firstName = finalFirstName,
        lastName = finalLastName,
        imageUrl = safeImageUrl
    )
}
