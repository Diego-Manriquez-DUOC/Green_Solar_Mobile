package com.green_solar.gs_app.core.utils

import com.green_solar.gs_app.data.remote.dto.UserDto
import com.green_solar.gs_app.domain.model.User

fun UserDto.toDomain(): User {
    // Si solo viene "name", lo parto en nombre/apellido
    val (fn, ln) = when {
        !name.isNullOrBlank() -> {
            val parts = name.trim().split(" ", limit = 2)
            val f = parts.getOrNull(0) ?: ""
            val l = parts.getOrNull(1) ?: ""
            f to l
        }
        else -> (firstName ?: "") to (lastName ?: "")
    }

    // Si no hay username, uso el email antes del @ como fallback
    val safeUsername = username ?: email.substringBefore('@')

    return User(
        id = id,
        username = safeUsername,
        email = email,
        firstName = fn,
        lastName = ln,
        imageUrl = avatarUrl
    )
}