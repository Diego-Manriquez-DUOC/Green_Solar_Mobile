package com.green_solar.gs_app.core.utils

import com.green_solar.gs_app.data.remote.dto.UserDto
import com.green_solar.gs_app.domain.model.User

fun UserDto.toDomain(): User = User(
    id = id,
    username = username,
    email = email,
    firstName = firstName,
    lastName = lastName,
    imageUrl = avatarUrl
)
