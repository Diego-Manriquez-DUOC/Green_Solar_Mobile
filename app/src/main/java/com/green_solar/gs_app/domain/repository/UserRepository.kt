package com.green_solar.gs_app.domain.repository
import com.green_solar.gs_app.domain.model.User
interface UserRepository {
    suspend fun getCurrentUser(): Result<User>
    suspend fun getUserById(id : Int): Result<User>
}