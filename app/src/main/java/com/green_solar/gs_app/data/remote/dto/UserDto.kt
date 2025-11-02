package com.green_solar.gs_app.data.remote.dto

import com.google.gson.annotations.SerializedName

data class UserDto(
    @SerializedName("id")        val id: Int,
    @SerializedName("username")  val username: String?,     // ← nullable
    @SerializedName("email")     val email: String,
    @SerializedName("firstName") val firstName: String?,    // ← nullable
    @SerializedName("lastName")  val lastName: String?,     // ← nullable
    @SerializedName("name")      val name: String?,         // ← a veces viene junto
    @SerializedName("image")     val avatarUrl: String? = null
)