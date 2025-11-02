package com.green_solar.gs_app.data.remote.dto

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String
)

data class LoginResponseDto(
    @SerializedName("authToken") val authToken: String,
    @SerializedName("user") val user: UserDto
)
