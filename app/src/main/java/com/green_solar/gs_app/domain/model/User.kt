package com.green_solar.gs_app.domain.model

data class User (
    val id : Int,
    val username : String,
    val email: String,
    val firstName : String,
    val lastName : String,
    val imageUrl : String? = null
)