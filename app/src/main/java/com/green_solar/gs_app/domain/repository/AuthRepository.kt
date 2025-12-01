package com.green_solar.gs_app.domain.repository

import com.green_solar.gs_app.domain.model.User

interface AuthRepository {
    suspend fun login(email: String, password: String): Result<User>
    suspend fun signup(username: String, email: String, password: String): Result<User> // Changed from name
    suspend fun logout()
}
