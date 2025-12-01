package com.green_solar.gs_app.data.remote.dto

import com.google.gson.annotations.SerializedName

// Request for login
data class LoginRequest(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String
)

// Request for sign up. Added SerializedName to prevent obfuscation issues.
data class SignupRequest(
    @SerializedName("username") val username: String,
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String
)

// Response from login/signup
data class AuthResponse(
    @SerializedName("id") val id: Long,
    @SerializedName("token") val token: String,
    @SerializedName("username") val username: String,
    @SerializedName("imgUrl") val imgUrl: String?
)

// Response from /api/auth/me
data class MeResponse(
    @SerializedName("username") val username: String,
    @SerializedName("role") val role: String,
    @SerializedName("email") val email: String,
    @SerializedName("imgUrl") val imgUrl: String?
)
