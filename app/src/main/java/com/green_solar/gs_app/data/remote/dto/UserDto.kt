package com.green_solar.gs_app.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * DTO for the User object from the API.
 * CORRECTED: Reverted property names to snake_case to match the original code.
 * Kept @SerializedName for robustness.
 */
data class UserDto(
    @SerializedName("user_id") val user_id: String,
    @SerializedName("name") val name: String,
    @SerializedName("email") val email: String,
    @SerializedName("role") val role: String,
    @SerializedName("imgUrl") val imgUrl: String?
)
