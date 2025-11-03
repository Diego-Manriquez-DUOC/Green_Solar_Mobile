package com.green_solar.gs_app.data.remote.dto

import com.google.gson.annotations.SerializedName

// data/remote/dto/UserDto.kt (ejemplo de posibles campos)
data class UserDto(
    val id: String? = null,
    val username: String? = null,
    val email: String? = null,
    val name: String? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val avatarUrl: String? = null
)

